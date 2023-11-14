package inlinedawt.runtime;

import inlinedawt.shared.ByteArrayUtils;
import inlinedawt.shared.ContainerIO;
import inlinedawt.shared.GraalDetector;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.graalvm.nativeimage.ProcessProperties;

@Slf4j
public class InlineAwtLoader {

    private static final InlineAwtLoader DEF_INST = new InlineAwtLoader();

    private static final String[] AWT_DLL_NAMES = { "awt.dll", "fontmanager.dll", "freetype.dll",
            "javaaccessbridge.dll", "javajpeg.dll", "jawt.dll", "jsound.dll", "lcms.dll" };

    private static final String INLINEAWT_EXECUTABLENAME = "inlineawt.executablename";

    public static InlineAwtLoader defaultInstance() {
        return DEF_INST;
    }

    public void extractAndLoad() {
        log.debug("extractAndLoad called.");

        GraalDetector.listGraalProperties(log::debug);

        log.debug("Original executable name: {}", getOriginalExecutableNameOpt().orElse("unknown"));
        log.debug("Current executable name: {}", getExecutableNameOpt().orElse("unknown"));
        log.debug("Executable was renamed: {}", wasExecutableRenamed());

        if (!GraalDetector.isGraalNativeRuntime()) {
            log.debug("We're not in Graal native image, skipping.");
            return;
        }

        try {

            Path javaShimsPath = extractJavaShims();
            log.debug("javaShimsPath = {}", javaShimsPath);
            Path nativeAwtPath = extractNativeAwt();
            log.debug("nativeAwtPath = {}", nativeAwtPath);

            String oldJLP = System.getProperty("java.library.path");
            log.debug("Old java.library.path: {}", oldJLP);
            String libPathPrepend = javaShimsPath.toString() + ";" + nativeAwtPath.toString() + ";";
            String newJLP = libPathPrepend + oldJLP;
            log.debug("New java.library.path: {}", newJLP);
            System.setProperty("java.library.path", newJLP);

            System.load(javaShimsPath.resolve("java.dll").toString());
            System.load(javaShimsPath.resolve("jvm.dll").toString());

        } catch (Exception e) {
            log.warn(e.toString(), e);
        }
    }

    public Optional<Path> getExecutablePathOpt() {
        try {
            // returns full path in C:\ddd\fff.exe format
            Path p = Path.of(ProcessProperties.getExecutableName());
            return Optional.of(p);
        } catch (Throwable t) {
            // intentional - ProcessProperties can throw java.lang.Error too when used from
            // a non-native jar
            return Optional.empty();
        }
    }

    public Optional<String> getExecutableNameOpt() {
        try {
            Path p = Path.of(ProcessProperties.getExecutableName());
            return Optional.of(p.getFileName().toString());
        } catch (Throwable t) {
            // intentional - ProcessProperties can throw java.lang.Error too when used from
            // a non-native jar
            return Optional.empty();
        }
    }

    public Optional<String> getOriginalExecutableNameOpt() {
        return Optional.ofNullable(System.getProperty(INLINEAWT_EXECUTABLENAME));
    }

    public boolean wasExecutableRenamed() {
        String s1 = getOriginalExecutableNameOpt().orElse("");
        String s2 = getExecutableNameOpt().orElse("");
        return !s1.equals(s2);
    }

    private Path extractJavaShims() throws IOException {
        Path tempPath = getTempPath();

        byte[] jvmBytes = ContainerIO.readResource("javashims/jvm.dll").getData();
        byte[] javaBytes = ContainerIO.readResource("javashims/java.dll").getData();

        if (wasExecutableRenamed()) {
            log.debug("Executable was renamed, shims need fixing.");

            fixShim(jvmBytes);
            fixShim(javaBytes);
        }

        byte[] jvmShaBytes = DigestUtils.sha1(jvmBytes);
        String jvmSha = Hex.encodeHexString(jvmShaBytes, true);

        Path jvmPathRel = Path.of("javashims-" + jvmSha);
        Path jvmPath = tempPath.resolve(jvmPathRel);
        Files.createDirectories(jvmPath);

        overwriteIfDifferent(jvmPath.resolve("jvm.dll"), jvmBytes);
        overwriteIfDifferent(jvmPath.resolve("java.dll"), javaBytes);

        return jvmPath;
    }

    private void fixShim(byte[] shimBytes) {
        String orgName = getOriginalExecutableNameOpt().get();
        String newName = getExecutableNameOpt().get();

        if (newName.length() <= orgName.length()) {
            int offset = ByteArrayUtils.indexOf(shimBytes, orgName);
            ByteArrayUtils.fill(shimBytes, offset, orgName.length(), 0);
            ByteArrayUtils.overwriteString(shimBytes, offset, newName);
        } else {
            // TODO
            log.error("YOU CAN'T RENAME THE EXE SO THE NEW NAME IS LONGER THAN {} CHARS.", orgName.length());
        }
    }

    private Path extractNativeAwt() throws IOException {
        Path tempPath = getTempPath();

        byte[] releaseBytes = loadResource("nativeawt/release");
        byte[] releaseShaBytes = DigestUtils.sha1(releaseBytes);
        String releaseSha = Hex.encodeHexString(releaseShaBytes, true);

        Path awtPathRel = Path.of("nativeawt-" + releaseSha);
        Path awtPath = tempPath.resolve(awtPathRel);
        Path awtBinPath = awtPath.resolve("bin");
        Files.createDirectories(awtBinPath);

        overwriteIfDifferent(awtPath.resolve("release"), releaseBytes);

        for (String dllName : AWT_DLL_NAMES) {
            Path curDllPath = awtBinPath.resolve(dllName);
            byte[] curDllBytes = loadResource("nativeawt/bin/" + dllName);
            overwriteIfDifferent(curDllPath, curDllBytes);
        }

        return awtBinPath;
    }

    private byte[] loadResource(String resName) throws IOException {
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        byte[] bytes = cl.getResourceAsStream(resName).readAllBytes();
        return bytes;
    }

    private Path getTempPath() {
        Path tempPath = Path.of(System.getProperty("java.io.tmpdir"));
        tempPath = tempPath.normalize().toAbsolutePath();
        return tempPath;
    }

    private void overwriteIfDifferent(Path filePath, byte[] contents) throws IOException {
        if (Files.exists(filePath)) {
            log.debug("File {} already exists.", filePath);
            byte[] oldContents = Files.readAllBytes(filePath);

            if (areEqual(contents, oldContents)) {
                log.debug("Contents are correct, no need to overwrite.");
                // touchFile(filePath); // TODO, needs more testing
                return;
            } else {
                log.debug("Contents are invalid, overwriting.");
            }
        } else {
            log.debug("File {} not found, writing.", filePath);
        }

        Files.write(filePath, contents, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        log.debug("File {} written.", filePath);
    }

    private void touchFile(Path filePath) {
        try {
            FileTime now = FileTime.fromMillis(System.currentTimeMillis());
            Files.setLastModifiedTime(filePath, now);
        } catch (Exception e) {
        }
    }

    private boolean areEqual(byte[] arrayOne, byte[] arrayTwo) {
        if (arrayOne == arrayTwo) {
            return true;
        } else if (arrayOne == null || arrayTwo == null) {
            return false;
        } else if (arrayOne.length != arrayTwo.length) {
            return false;
        }

        for (int i = 0; i < arrayOne.length; i++) {
            if (arrayOne[i] != arrayTwo[i]) {
                return false;
            }
        }

        return true;
    }

}
