package inlinedawt.buildtime;

import java.util.Map;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.reflect.FieldUtils;
import org.graalvm.nativeimage.ImageSingletons;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.impl.RuntimeResourceSupport;

@Slf4j
public class LogFeature implements Feature {

    public LogFeature() {
        log.info("constructor called.");
    }

    @Override
    public void beforeAnalysis(BeforeAnalysisAccess access) {
        log.info("beforeAnalysis called.");
        Feature.super.beforeAnalysis(access);
    }

    @Override
    public void beforeUniverseBuilding(BeforeUniverseBuildingAccess access) {
        log.info("beforeUniverseBuilding called.");
        Feature.super.beforeUniverseBuilding(access);
    }

    @Override
    public void beforeCompilation(BeforeCompilationAccess access) {
        log.info("beforeCompilation called.");
        Feature.super.beforeCompilation(access);
    }

    @Override
    public void beforeImageWrite(BeforeImageWriteAccess access) {
        log.info("beforeImageWrite called.");
        Feature.super.beforeImageWrite(access);
    }

    @Override
    public void duringAnalysis(DuringAnalysisAccess access) {
        log.info("duringAnalysis called.");
        Feature.super.duringAnalysis(access);
    }

    @Override
    public void duringSetup(DuringSetupAccess access) {
        log.info("duringSetup called.");
        checkAccess();
        Feature.super.duringSetup(access);
    }

    @Override
    public void afterRegistration(AfterRegistrationAccess access) {
        log.info("afterRegistration called.");
        Feature.super.afterRegistration(access);
    }

    @Override
    public void afterImageWrite(AfterImageWriteAccess access) {
        log.info("afterImageWrite called.");
        Feature.super.afterImageWrite(access);
    }

    @Override
    public void cleanup() {
        log.info("cleanup called.");
        Feature.super.cleanup();
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

    public static Map<Class<?>, Object> getImageSingletons() {
        try {
            Class c1 = Class.forName("com.oracle.svm.hosted.ImageSingletonsSupportImpl$HostedManagement");
            Object o2 = FieldUtils.readStaticField(c1, "singletonDuringImageBuild", true);
            Object o3 = FieldUtils.readField(o2, "configObjects", true);
            Map<Class<?>, Object> configObjects = (Map<Class<?>, Object>) o3;
            return configObjects;
        } catch (Throwable t) {
            throw new IllegalStateException(t);
        }
    }

    public static void logImageSingletonsNoEx(Consumer<String> out) {
        try {
            out.accept("Available ImageSingletons:");
            getImageSingletons().forEach((t, u) -> {
                out.accept("" + t);
            });
        } catch (Throwable t) {
            log.warn("Exception while trying to log ImageSingletons: {}", t);
        }
    }

}
