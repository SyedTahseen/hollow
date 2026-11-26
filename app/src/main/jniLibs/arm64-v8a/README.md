# Root mode: Xray executable

Root mode (Settings > Connection > Advanced settings > Root mode) runs a standalone Xray-core
executable as root. Put the Android arm64 build of Xray-core here, named exactly:

    libxray.so

- Use Xray-core 25.x or newer (it needs the `tun` inbound), built for `linux/android arm64`.
- It is an executable, not a JNI library; the `.so` name only makes Gradle package it into the
  APK's native library folder (`useLegacyPackaging = true` is already set, so it is extracted).
- Without this file the Root mode switch refuses to turn on and says the binary is missing.
