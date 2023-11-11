package inlinedawt.buildtime;

import inlinedawt.shared.ByteArrayUtils;
import inlinedawt.shared.Container;
import inlinedawt.shared.ContainerIO;
import inlinedawt.shared.ContainerUtils;
import inlinedawt.shared.io.RandomAccessFileUtils;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import lombok.extern.slf4j.Slf4j;
import org.graalvm.nativeimage.ImageSingletons;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeClassInitialization;
import org.graalvm.nativeimage.impl.RuntimeResourceSupport;

//@AutomaticFeature
@Slf4j
public class InlineAwtFeature implements Feature {

    private boolean duringAnalysisAlreadyCalled = false;

    @Override
    public void duringSetup(DuringSetupAccess access) {
        log.info("duringSetup called.");
        Feature.super.duringSetup(access);
        try {

            checkAccess();

            setupInitializeAtBuildtimeClasses();

            InlineAwtPreparator.defaultInstance().prepareDuringSetup(access);

        } catch (Exception e) {
            log.error(e.toString(), e);
        }
    }

    private void setupInitializeAtBuildtimeClasses() {
        RuntimeClassInitialization.initializeAtBuildTime(ch.qos.logback.core.status.InfoStatus.class);
        RuntimeClassInitialization.initializeAtBuildTime(ch.qos.logback.classic.Logger.class);
        RuntimeClassInitialization.initializeAtBuildTime(ch.qos.logback.classic.Level.class);
        RuntimeClassInitialization.initializeAtBuildTime(ch.qos.logback.core.CoreConstants.class);
        RuntimeClassInitialization.initializeAtBuildTime(ch.qos.logback.core.util.StatusPrinter.class);
        RuntimeClassInitialization.initializeAtBuildTime(org.slf4j.LoggerFactory.class);
        RuntimeClassInitialization.initializeAtBuildTime(ch.qos.logback.core.util.Loader.class);
        RuntimeClassInitialization.initializeAtBuildTime(inlinedawt.shared.ContainerUtils.class);
    }

    @Override
    public void afterImageWrite(AfterImageWriteAccess access) {
        log.info("afterImageWrite called.");
        Feature.super.afterImageWrite(access);

        try {

            importToContainers();
        } catch (Exception e) {
            log.error(e.toString(), e);
        }

    }

    @Override
    public void cleanup() {
        log.info("cleanup called.");
        Feature.super.cleanup();

        duringAnalysisAlreadyCalled = false;
    }

    public void importToContainers() throws IOException {

        byte[] buf = ByteArrayUtils.readFile("target/jvm.dll");
        importToContainer("javashims/jvm.dll", buf);

        buf = ByteArrayUtils.readFile("target/java.dll");
        importToContainer("javashims/java.dll", buf);

    }

    public void importToContainer(String contName, byte[] contents) throws IOException {
        log.info("importToContainer {}, bytes={}", contName, contents.length);

        String mainFile = "target/demo-nativeimage-swing2.exe";

        byte[] bytes = ByteArrayUtils.readFile(mainFile);

        String expectedSeparator = ContainerUtils.getSeparator(contName, true);

        int separatorOffset = ByteArrayUtils.indexOf(bytes, expectedSeparator);

        if (separatorOffset <= 0) {
            log.warn("separatorOff = {} !!!???", separatorOffset);
        }

        RandomAccessFile raf = new RandomAccessFile(mainFile, "rw");

        raf.seek(separatorOffset);
        DataInputStream dis = RandomAccessFileUtils.newBufferedDataInputStream(raf, false);

        Container c = ContainerIO.read(dis);

        c.setData(contents);

        raf.seek(separatorOffset);
        DataOutputStream dos = RandomAccessFileUtils.newBufferedDataOutputStream(raf, false);

        ContainerIO.write(dos, c);

        raf.getFD().sync();
        raf.close();

    }

    public void checkAccess() {
        try {
            ImageSingletons.lookup(RuntimeResourceSupport.class);
        } catch (IllegalAccessError iae) {
            // class build.LogFeature (in unnamed module @0x6ff65192) cannot access class
            // org.graalvm.nativeimage.impl.RuntimeResourceSupport (in module
            // org.graalvm.nativeimage) because module
            // org.graalvm.nativeimage does not export org.graalvm.nativeimage.impl to
            // unnamed module @0x6ff65192
            String hint1 = "--add-exports=org.graalvm.nativeimage/org.graalvm.nativeimage.impl=ALL-UNNAMED";
            log.warn("" + iae);
            log.warn("HINT: Try adding: " + hint1);
        } catch (Throwable t) {
            log.warn("" + t);
        }
    }

}
