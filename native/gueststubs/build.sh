#!/bin/sh
# Build aemu-stubs.jar — guest-side Java stub services (dex for Dalvik 4.2+).
# Needs: JDK (javac), Android SDK (android.jar and d8 from build-tools). Linux / CI compatible.
set -e
SDK=${ANDROID_HOME:-$ANDROID_SDK_ROOT}
[ -n "$SDK" ] || { echo "ANDROID_HOME is not set" >&2; exit 1; }
JAR="$SDK/platforms/android-36/android.jar"
[ -f "$JAR" ] || { echo "android.jar not found: $JAR" >&2; exit 1; }
D8=$(ls -d "$SDK"/build-tools/*/d8 2>/dev/null | sort -V | tail -1)
[ -x "$D8" ] || { echo "d8 not found under $SDK/build-tools" >&2; exit 1; }
OUT=${1:-../../app/src/main/assets/engines/kk/aemu-stubs.jar}
rm -rf build && mkdir -p build/classes build/hidden
javac --release 8 -cp "$JAR" -d build/hidden $(find hidden -name '*.java')
javac --release 8 -cp "build/hidden:$JAR" -d build/classes $(find src -name '*.java')
"$D8" --min-api 17 --lib "$JAR" --output build $(find build/classes -name '*.class')
python3 -c "import zipfile;z=zipfile.ZipFile('build/stubs.jar','w',zipfile.ZIP_DEFLATED);z.write('build/classes.dex','classes.dex');z.close()"
cp build/stubs.jar "$OUT"
echo "done: $OUT"
