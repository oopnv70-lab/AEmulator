// su — a uid-0 shim for guest root managers.
//
// It runs as a normal guest program (the app's uid, 10xxx). The engine's seccomp
// trap turns its setresuid/setresgid into success and reports the new ids back,
// while the host process stays the app's uid. The exec then passes AEMU64_ID=0,0
// to the shell, so the shell and every command it runs believe they are root
// inside the guest. Every guest file lives in the app's own storage, so the
// reads and writes go through as the host uid that owns those files.
typedef unsigned long u64;
typedef long i64;

static long sc(long nr, long a, long b, long c, long d, long e, long f) {
    register long x8 __asm__("x8") = nr;
    register long x0 __asm__("x0") = a;
    register long x1 __asm__("x1") = b;
    register long x2 __asm__("x2") = c;
    register long x3 __asm__("x3") = d;
    register long x4 __asm__("x4") = e;
    register long x5 __asm__("x5") = f;
    __asm__ volatile("svc 0" : "+r"(x0)
                     : "r"(x8), "r"(x1), "r"(x2), "r"(x3), "r"(x4), "r"(x5)
                     : "memory");
    return x0;
}
#define S1(n, a) sc(n, (long)(a), 0, 0, 0, 0, 0)
#define S3(n, a, b, c) sc(n, (long)(a), (long)(b), (long)(c), 0, 0, 0)
#define S4(n, a, b, c, d) sc(n, (long)(a), (long)(b), (long)(c), (long)(d), 0, 0)

static int streq(const char *a, const char *b) { while (*a && *a == *b) a++, b++; return *a == *b; }

__attribute__((naked)) void _start(void) {
    __asm__ volatile("mov x0, sp\nb real_start");
}

__attribute__((noreturn)) void real_start(long *sp) {
    long argc = sp[0];
    char **argv = (char **)(sp + 1);
    char **envp = argv + argc + 1;

    // Become root in the guest's view. The engine's trap handler lets these
    // through and updates the ids it reports back; the host process keeps the
    // app's real uid, which owns every file of the image.
    S3(147, 0, 0, 0);  // setresuid(0, 0, 0)
    S3(149, 0, 0, 0);  // setresgid(0, 0, 0)

    char *shell = "/system/bin/sh";
    char *cmd = 0;
    long k = 0;
    char *rest[16];

    for (long i = 1; i < argc; i++) {
        const char *a = argv[i];
        if (streq(a, "-c") || streq(a, "--command")) { if (i + 1 < argc) { cmd = argv[++i]; continue; } }
        else if (streq(a, "-s") || streq(a, "--shell")) { if (i + 1 < argc) { shell = argv[++i]; continue; } }
        else if (streq(a, "-v") || streq(a, "-V") || streq(a, "--version")) { S4(64, 1, "1.0:guest-root\n", 14, 0); S1(93, 0); }
        else if (streq(a, "-h") || streq(a, "--help")) { S4(64, 1, "usage: su [-c CMD] [-s SHELL]\n", 29, 0); S1(93, 0); }
        else if (k < 15) rest[k++] = argv[i];
    }

    char *nargv[24];
    long n = 0;
    nargv[n++] = shell;
    if (cmd) { nargv[n++] = "-c"; nargv[n++] = cmd; }
    else for (long i = 0; i < k && n < 23; i++) nargv[n++] = rest[i];
    nargv[n] = 0;

    sc(221, (long)nargv[0], (long)nargv, (long)envp, 0, 0, 0);  // execve

    S1(93, 1);  // exit_group(1) — execve failed
    for (;;) {}
}