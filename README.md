# parking-lot
Parking Lot System-Design Specs and Java Implementation

Start with the [v1 development handover](docs/v1/README.md) for the consolidated specification, acceptance plan and implementation review. The original brainstorm documents are preserved as historical inputs.

## Build a native executable

Install [GraalVM for JDK 21](https://www.graalvm.org/jdk21/docs/getting-started/windows/) including Native Image. On Windows, also install Visual Studio 2022 Build Tools with the **Desktop development with C++** workload and Windows SDK.

From the project root in PowerShell (replace the example installation path):

```powershell
$env:GRAALVM_HOME = 'C:\path\to\graalvm-jdk-21'
& "$env:GRAALVM_HOME\bin\native-image.cmd" --version
.\gradlew.bat :app:nativeCompile
.\app\build\native\nativeCompile\parking.exe --help
```

The native executable runs without Java installed. It targets the operating system and architecture used to build it. On Linux/macOS, install the platform's C/C++ build tools, set `GRAALVM_HOME`, and run `./gradlew :app:nativeCompile`; the output is `app/build/native/nativeCompile/parking`.

Other useful tasks:

```powershell
.\gradlew.bat :app:test
.\gradlew.bat :app:nativeRun --args="--help"
```

The [GraalVM Gradle plugin](https://graalvm.github.io/native-build-tools/latest/gradle-plugin.html) supplies `nativeCompile` and `nativeRun`. Native compilation uses `GRAALVM_HOME`, falling back to `JAVA_HOME`; normal Java compilation still uses the Java 21 toolchain. No JVM fallback executable is generated.

Picocli's existing annotation processor generates command metadata. The reachability metadata repository supplies supported library metadata, while `app/src/main/resources/META-INF/native-image/com.parkinglot.app/parking/reflect-config.json` registers application classes used reflectively by Guice and Jackson. Update that file when adding injected classes or persisted model types. Guice bytecode generation is disabled for native builds and native execution.

After building, exercise `create-lot`, `park`, `reserve`, `find-vehicle`, `unpark`, and `status` with disposable parking data to verify native reflection and persistence paths; `--help` alone does not cover them. Commands use the same storage location as the JVM application.
