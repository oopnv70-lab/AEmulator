package app.aemu.core

import android.content.Context
import android.os.Environment
import android.system.Os
import java.io.File
import java.nio.file.Files

/**
 * Общая папка «карты памяти»: лежит в памяти телефона (Внутренний накопитель/AEmulator/<образ>),
 * гость видит её по своим путям (/mnt/sdcard, /sdcard, /storage/…) — их подменяет qemu (DHD_SDCARD).
 */
object Sdcard {
    private val LAYOUT = listOf("DCIM", "Download", "Music", "Movies", "Pictures", "Android/data", "Android/obb", "LOST.DIR")
    private val GUEST_PATHS = listOf(
        "mnt/sdcard", "sdcard", "storage/emulated/legacy", "storage/emulated/0",
        "storage/sdcard0", "storage/sdcard", "mnt/shell/emulated/0", "mnt/emmc", "storage/extSdCard",
    )

    fun setup(ctx: Context, paths: VmPaths, img: GuestImage, log: (String) -> Unit): File? {
        val d = choose(ctx, img, log) ?: return null
        LAYOUT.forEach { File(d, it).mkdirs() }
        val readme = File(d, "README-AEmulator.txt")
        if (!readme.isFile) runCatching {
            readme.writeText(
                "Shared folder of image \"${img.name}\".\n" +
                    "On the phone: ${d.absolutePath}\n" +
                    "Inside the firmware: ${img.sdcardPath} (and /sdcard)\n\n" +
                    "Put APKs, music and photos here, the guest sees them immediately.\n"
            )
        }
        val all = (GUEST_PATHS + img.sdcardPath.trimStart('/') + img.sdcardPaths.map { it.trimStart('/') }).distinct()
        // (the native arm64 engine maps the card inside its runner too: AEMU64_SDCARD)
        // The engine only redirects the points of its own list (GuestImage.NATIVE_SDCARD_POINTS). A firmware whose
        // primary storage is another one - /storage/sdcard, which is what the AOSP default storage_list.xml names -
        // keeps asking for that path, and a plain directory there is an empty card whose statfs answers with the
        // image's filesystem: a storage screen reading "0 bytes". So every such point is linked to one the engine
        // does map, and both then show the shared folder. This has to happen before the placeholders are made.
        val card = img.sdcardPaths.ifEmpty { listOf(img.sdcardPath) }.firstOrNull { it in GuestImage.NATIVE_SDCARD_POINTS }
        if (card != null) for (p in img.sdcardPaths) if (p !in GuestImage.NATIVE_SDCARD_POINTS && p.startsWith("/"))
            link(File(paths.root, p.trimStart('/')), File(paths.root, card.trimStart('/')))
        for (rel in all + listOf("storage/self/primary", "mnt/user/0/primary")) placeholder(File(paths.root, rel), keepLinks = true)
        for (rel in listOf("mnt/asec", "mnt/obb", "mnt/secure/asec")) File(paths.root, rel).mkdirs()
        runCatching { File(paths.bin, "sdcard.path").writeText(d.absolutePath + "\n") }
        log("sdcard: ${d.absolutePath} → guest sees ${img.sdcardPath}")
        return d
    }

    /** guest paths (without the leading /) that show the shared card folder */
    fun guestPaths(img: GuestImage) = (GUEST_PATHS + img.sdcardPath.trimStart('/')).distinct()

    fun hostDir(paths: VmPaths): File? =
        runCatching { File(File(paths.bin, "sdcard.path").readText().trim()) }.getOrNull()?.takeIf { it.isDirectory }

    private fun choose(ctx: Context, img: GuestImage, log: (String) -> Unit): File? {
        val safe = img.name.replace(Regex("[^\\p{L}\\p{N} ._-]"), "_").take(40).ifBlank { img.id }
        val tries = ArrayList<File>()
        runCatching {
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                tries.add(File(File(Environment.getExternalStorageDirectory(), "AEmulator"), "$safe (${img.id})"))
            }
        }
        ctx.getExternalFilesDir(null)?.let { tries.add(File(it, "sdcard-${img.id}")) }
        tries.add(File(ctx.filesDir, "sdcard-${img.id}"))
        for (t in tries) if (writable(t)) return t else log("sdcard: ${t.absolutePath} not writable, trying next")
        log("sdcard: no writable location")
        return null
    }

    private fun writable(d: File): Boolean = runCatching {
        d.mkdirs()
        if (!d.isDirectory) return false
        val p = File(d, ".aemu-probe")
        p.writeText("ok")
        val ok = p.length() > 0
        p.delete()
        ok
    }.getOrDefault(false)

    /** В дереве гостя на месте точки монтирования должен быть обычный каталог (не ссылка). */
    private fun link(f: File, target: File) {
        val p = f.toPath()
        if (Files.isSymbolicLink(p)) {
            if (runCatching { Os.readlink(f.absolutePath) }.getOrNull() == target.absolutePath) return
            runCatching { Os.remove(f.absolutePath) }
        } else if (f.isDirectory && f.list().isNullOrEmpty()) f.delete()      // our own empty placeholder
        else if (f.exists()) return                                           // the firmware's own content stays
        f.parentFile?.mkdirs()
        runCatching { Os.symlink(target.absolutePath, f.absolutePath) }
    }

    private fun placeholder(f: File, keepLinks: Boolean = false) {
        if (runCatching { Files.isSymbolicLink(f.toPath()) }.getOrDefault(false)) {
            if (keepLinks) return                                                  // a card alias: leave it as it is
            runCatching { Os.remove(f.absolutePath) }
        } else if (f.isFile) f.delete()
        f.mkdirs()
    }
}
