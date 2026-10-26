package app.aemu.core

import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.zip.ZipFile

/**
 * Installing an APK without the package manager.
 *
 * `pm install` hands the whole job to the guest: PackageInstaller → PackageManagerService → installd (parses the
 * manifest, extracts the native libraries) → dex2oat under the translator → a rewrite of data/system/packages.xml,
 * which on 12+ is binary (ABX) rather than text. Every step of that chain runs inside qemu, and one failure anywhere
 * leaves the app half-installed.
 *
 * A firmware's own system apps are registered differently: PackageManagerService scans the partitions at boot and
 * builds the package table itself — name, code path, native library dir, signatures, uid, data dirs — from what it
 * finds in /system/app/<Name>/, /system/priv-app/<Name>/ and so on. That scan is the same code path that registers
 * PrintSpooler and its lib/arm64, so nothing here is special-cased for the emulator.
 *
 * So an APK is "installed" by putting it where the scan will find it:
 *
 *     <root>/system/app/<Dir>/<Dir>.apk
 *     <root>/system/app/<Dir>/lib/arm64/<every lib/arm64-v8a/…so, extracted>
 *
 * The native libraries must be extracted by us: the APK keeps them deflated (`extractNativeLibs=true`), and on a
 * system partition nothing would decompress them — installd is not involved, and there is no per-package lib dir to
 * unpack into. A traditional system app carries its libraries next to the apk exactly this way (lib/arm64/…).
 *
 * What this cannot do: no dex2oat (the apk is interpreted, or JIT-compiled at run time), and no launcher entry — the
 * home screen builds its workspace from the vendor's preset, so a sideloaded app is started with `am start`.
 */
object Sideload {
    /** marker written into each placed directory: lets a later placement recognise its own work */
    private const val MARK = ".aemu-sideload"

    /** Partitions PackageManagerService scans, most natural first for a user-installed app. */
    private val PARTITIONS = listOf("system/app", "system/priv-app", "system_ext/app", "product/app")

    private val ABI_DIRS = listOf("arm64-v8a", "arm64")

    /**
     * Android 17 (API 37) dropped CBS_init and the rest of OpenSSL 1.x's CBS_ API from the platform's libcrypto.
     * An app that bundles its own OpenSSL but keeps the stock names libssl.so/libcrypto.so does not get its own copy:
     * the loader already resolved those sonames to the platform's, the bundled libssl.so binds to the platform
     * libcrypto.so, and the first CBS_ symbol it imports cannot be found - dlopen fails with
     * "cannot locate symbol "CBS_init"" and any native library that pulls in TLS goes with it. That is what kept
     * Bili from starting. The bundled pair is complete on its own, so the fix is to give it private sonames: a
     * same-length byte patch inside .dynstr, no offsets move, the libraries keep working among themselves and stop
     * colliding with the platform.
     */
    private val PRIVATE_SONAMES = listOf("libcrypto.so" to "bk_crypto.so", "libssl.so" to "bk_ssl.so")

    data class Outcome(val ok: Boolean, val packageName: String?, val dir: String?, val libs: Int, val message: String)

    /**
     * The `package` attribute of the APK's binary AndroidManifest.xml, or null when it cannot be read.
     * AXML: file header (8) → string pool → resource map → start tags. The string pool is UTF-8 or UTF-16 depending
     * on its flags word, and the length prefixes differ between the two, so both are handled.
     */
    fun packageName(apk: File): String? = runCatching {
        ZipFile(apk).use { z ->
            val entry = z.getEntry("AndroidManifest.xml") ?: return@use null
            val x = z.getInputStream(entry).use { it.readBytes() }
            readPackage(x)
        }
    }.getOrNull()

    fun readPackage(x: ByteArray): String? {
        if (x.size < 16) return null
        val b = ByteBuffer.wrap(x).order(ByteOrder.LITTLE_ENDIAN)
        val strings = stringPool(x, b, b.getShort(2).toInt() and 0xffff) ?: return null
        var off = b.getShort(2).toInt() and 0xffff
        // skip the string pool chunk itself
        if (off + 8 > x.size) return null
        val poolSize = b.getInt(off + 4)
        if (poolSize < 8) return null
        off += poolSize
        while (off + 8 <= x.size) {
            val type = b.getShort(off).toInt() and 0xffff
            val size = b.getInt(off + 4)
            if (size < 8 || off + size > x.size) break
            if (type == 0x0102) {                       // start tag: ns, name, attrStart, attrSize, attrCount
                val name = strings.getOrNull(b.getInt(off + 20)) ?: ""
                if (name == "manifest") {
                    val attrStart = b.getShort(off + 24).toInt() and 0xffff
                    val attrSize = b.getShort(off + 26).toInt() and 0xffff
                    val count = b.getShort(off + 28).toInt() and 0xffff
                    for (i in 0 until count) {
                        val a = off + 16 + attrStart + i * attrSize
                        if (a + 20 > off + size) break
                        if (strings.getOrNull(b.getInt(a + 4)) != "package") continue
                        val raw = b.getInt(a + 8)
                        if (raw in strings.indices) return strings[raw]
                        // no raw value: the typed value carries the string index
                        val data = b.getInt(a + 16)
                        if (data in strings.indices) return strings[data]
                    }
                    return null
                }
            }
            off += size
        }
        return null
    }

    /** The string pool at [off]: UTF-8 when flag 0x100 is set, UTF-16 otherwise. */
    private fun stringPool(x: ByteArray, b: ByteBuffer, off: Int): List<String>? {
        if (off + 28 > x.size) return null
        if ((b.getShort(off).toInt() and 0xffff) != 0x0001) return null
        val headerSize = b.getShort(off + 2).toInt() and 0xffff
        val count = b.getInt(off + 8)
        val flags = b.getInt(off + 16)
        val stringsStart = b.getInt(off + 20)
        if (count < 0 || count > 1_000_000) return null
        val utf8 = flags and 0x100 != 0
        val base = off + stringsStart
        val out = ArrayList<String>(count)
        for (i in 0 until count) {
            val idxAt = off + headerSize + i * 4
            if (idxAt + 4 > x.size) return null
            var p = base + b.getInt(idxAt)
            if (p < 0 || p >= x.size) { out.add(""); continue }
            if (utf8) {
                // utf16 length (skipped), then the utf8 byte length, each 1 or 2 bytes
                var c = x[p].toInt() and 0xff
                p += 1
                if (c and 0x80 != 0) p += 1
                if (p >= x.size) { out.add(""); continue }
                c = x[p].toInt() and 0xff
                p += 1
                var len = c
                if (c and 0x80 != 0) {
                    if (p >= x.size) { out.add(""); continue }
                    len = ((c and 0x7f) shl 8) or (x[p].toInt() and 0xff)
                    p += 1
                }
                if (len < 0 || p + len > x.size) { out.add(""); continue }
                out.add(String(x, p, len, Charsets.UTF_8))
            } else {
                var len = b.getShort(p).toInt() and 0xffff
                p += 2
                if (len and 0x8000 != 0) {
                    len = ((len and 0x7fff) shl 16) or (b.getShort(p).toInt() and 0xffff)
                    p += 2
                }
                if (len < 0 || p + len * 2 > x.size) { out.add(""); continue }
                out.add(String(x, p, len * 2, Charsets.UTF_16LE))
            }
        }
        return out
    }

    /** A directory name that is readable and unique: the last package segment, capitalised. */
    private fun dirName(pkg: String): String {
        val last = pkg.substringAfterLast('.').ifBlank { "App" }
        val clean = last.map { if (it.isLetterOrDigit()) it else '_' }.joinToString("")
        val cap = clean.replaceFirstChar { if (it.isLowerCase()) it.uppercase() else it.toString() }
        return cap.ifBlank { "App" }
    }

    /**
     * Put [apk] on a system partition of the image at [root] so that the package manager's boot scan registers it.
     * Idempotent: a directory this object created before (it carries [MARK]) is replaced in place, everything else is
     * left alone. Returns the outcome for the caller to log and show.
     */
    fun place(
        root: File,
        apk: File,
        log: (String) -> Unit,
    ): Outcome {
        val pkg = packageName(apk)
            ?: return Outcome(false, null, null, 0, "cannot read the package name (not an APK?)")
        val name = dirName(pkg)
        val partition = PARTITIONS.map { File(root, it) }.firstOrNull { it.isDirectory }
            ?: return Outcome(false, pkg, null, 0, "no system/app in this image")
        val dir = File(partition, name)
        val existing = File(dir, MARK)
        if (dir.exists() && !existing.isFile) {
            return Outcome(false, pkg, dir.path, 0, "$name already exists and was not put there by AEmulator")
        }
        runCatching {
            dir.mkdirs()
            File(dir, "lib/arm64").mkdirs()
            dir.setReadable(true, false); dir.setExecutable(true, false)
            File(dir, "lib").setReadable(true, false); File(dir, "lib").setExecutable(true, false)
            File(dir, "lib/arm64").setReadable(true, false); File(dir, "lib/arm64").setExecutable(true, false)
            // the apk keeps its own name so the scan's code path and the data dir agree on it
            val target = File(dir, "$name.apk")
            apk.inputStream().use { i -> target.outputStream().use { o -> i.copyTo(o, 64 * 1024) } }
            target.setReadable(true, false)
            existing.writeText("$pkg\n${apk.name}\n")
            existing.setReadable(true, false)
        }.onFailure { return Outcome(false, pkg, dir.path, 0, "copy failed: ${it.message}") }

        // the libraries: the apk keeps them deflated, and on a system partition nothing would extract them
        var libs = 0
        val libDir = File(dir, "lib/arm64")
        runCatching {
            ZipFile(apk).use { z ->
                for (abi in ABI_DIRS) {
                    val prefix = "lib/$abi/"
                    val entries = java.util.Collections.list(z.entries()).filter { !it.isDirectory && it.name.startsWith(prefix) && it.name.endsWith(".so") }
                    if (entries.isEmpty()) continue
                    for (e in entries) {
                        val out = File(libDir, e.name.substringAfterLast('/'))
                        z.getInputStream(e).use { i -> out.outputStream().use { o -> i.copyTo(o, 64 * 1024) } }
                        out.setReadable(true, false)
                        libs++
                    }
                    break
                }
            }
        }.onFailure { log("sideload: libraries: ${it.message}") }
        if (libs > 0) runCatching { privateSonames(libDir, log) }.onFailure { log("sideload: sonames: ${it.message}") }

        log("sideload: $pkg → ${dir.relativeTo(root).path} ($libs native libraries)")
        return Outcome(true, pkg, dir.relativeTo(root).path, libs, "placed $pkg")
    }

    /**
     * Rewrite the bundled OpenSSL sonames, so the loader stops matching them against the platform's libraries of the
     * same name. Every replacement is the same length (libcrypto.so -> bk_crypto.so, libssl.so -> bk_ssl.so), so the
     * patch is a byte-for-byte swap inside .dynstr: no offset in the file moves, no section or program header has to
     * be rebuilt, and the libraries keep resolving each other. Files are renamed to match so the directory tells the
     * truth, and the originals are dropped - nothing in the set references them any more.
     */
    private fun privateSonames(libDir: File, log: (String) -> Unit = {}) {
        var hit = 0
        for (f in libDir.listFiles().orEmpty().filter { it.isFile && it.name.endsWith(".so") }) {
            val bytes = runCatching { f.readBytes() }.getOrNull() ?: continue
            var out = bytes
            for ((from, to) in PRIVATE_SONAMES) {
                out = replaceString(out, from, to)
            }
            if (out === bytes || out.contentEquals(bytes)) continue
            runCatching {
                f.writeBytes(out)
                f.setReadable(true, false)
                hit++
            }
        }
        if (hit == 0) return
        for ((from, to) in PRIVATE_SONAMES) {
            val src = File(libDir, from)
            if (src.isFile) runCatching {
                val dst = File(libDir, to)
                if (dst.exists()) dst.delete()
                src.delete()
            }
        }
        log("sideload: gave the bundled OpenSSL private sonames ($hit files) - the platform's libcrypto is not OpenSSL 1.x any more")
    }

    /** Same-length, NUL-terminated replacement of a soname inside the file's .dynstr. */
    private fun replaceString(data: ByteArray, from: String, to: String): ByteArray {
        val a = (from + "\u0000").toByteArray(Charsets.ISO_8859_1)
        val b = (to + "\u0000").toByteArray(Charsets.ISO_8859_1)
        if (a.size != b.size) return data
        var out: ByteArray? = null
        var i = 0
        while (i <= data.size - a.size) {
            var match = true
            for (k in a.indices) if (data[i + k] != a[k]) { match = false; break }
            if (!match) { i++; continue }
            val arr = out ?: data.copyOf().also { out = it }
            System.arraycopy(b, 0, arr, i, b.size)
            i += a.size
        }
        return out ?: data
    }

    /**
     * Re-apply the soname fix to packages that were already in the image before it existed. Runs at start, is a
     * no-op once every .so has been rewritten, so it costs a directory listing on a healthy image.
     */
    fun fixSonames(root: File, log: (String) -> Unit = {}) {
        for (d in placed(root)) {
            val libDir = File(d, "lib/arm64")
            if (!libDir.isDirectory) continue
            runCatching { privateSonames(libDir, log) }.onFailure { log("sideload: sonames in ${d.name}: ${it.message}") }
        }
    }

    /** Directories this object created in the image, for removal. */
    fun placed(root: File): List<File> = PARTITIONS.map { File(root, it) }.filter { it.isDirectory }
        .flatMap { p -> p.listFiles().orEmpty().filter { File(it, MARK).isFile } }

    /** Remove a placed package again (its data dirs are left to the guest). */
    fun remove(root: File, pkg: String): Boolean {
        val hit = placed(root).firstOrNull { File(it, MARK).readText().lineSequence().firstOrNull() == pkg } ?: return false
        return runCatching { hit.deleteRecursively() }.getOrDefault(false)
    }
}