package inlinedawt.buildtime;

import inlinedawt.shared.ByteArrayUtils;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.graalvm.nativeimage.hosted.Feature;

@Slf4j
public class InlineAwtPreparator {

    private static final InlineAwtPreparator DEF_INST = new InlineAwtPreparator();

    // test shims were 10KB-ish, so make it 3x just in case
    public static final int DEF_CONTAINER_SIZE = 33 * 1024;

    private static final String[] AWT_DLL_NAMES = { "awt.dll", "fontmanager.dll", "freetype.dll",
            "javaaccessbridge.dll", "javajpeg.dll", "jawt.dll", "jsound.dll", "lcms.dll" };

    public static InlineAwtPreparator defaultInstance() {
        return DEF_INST;
    }

    public void prepareDuringSetup(Feature.DuringSetupAccess access) throws IOException {
        log.debug("prepareDuringSetup called.");

        prepareContainers();

        prepareAwt();
    }

    public void afterImageWrite(Feature.AfterImageWriteAccess access) {
        log.debug("afterImageWrite called.");

    }

    private void prepareContainers() throws IOException {
        log.debug("Preparing containers...");
        ContainerBTUtils.addEmptyContainer("javashims/java.dll", DEF_CONTAINER_SIZE);
        ContainerBTUtils.addEmptyContainer("javashims/jvm.dll", DEF_CONTAINER_SIZE);
        ContainerBTUtils.addContainerList();
        log.debug("Preparing containers done.");
    }

    private void prepareAwt() throws IOException {
        log.debug("Preparing AWT dlls...");

        String javaHomeStr = getJavaHomeStr();

        log.debug("java.home={}", javaHomeStr);

        ResourceBTUtils.addResource("nativeawt/release", javaHomeStr + "/release");

        for (String dllName : AWT_DLL_NAMES) {
            ResourceBTUtils.addResource("nativeawt/bin/" + dllName, javaHomeStr + "/bin/" + dllName);
        }

        log.debug("Preparing AWT dlls done.");
    }

    private static String getJavaHomeStr() {
        return System.getProperty("java.home");
    }

}
