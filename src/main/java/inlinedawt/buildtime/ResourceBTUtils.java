package inlinedawt.buildtime;

import inlinedawt.shared.ByteArrayUtils;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.graalvm.nativeimage.ImageSingletons;
import org.graalvm.nativeimage.hosted.RuntimeResourceAccess;
import org.graalvm.nativeimage.impl.RuntimeResourceSupport;

@Slf4j
public class ResourceBTUtils {

    // TODO: is it still needed?
    public static RuntimeResourceSupport getRuntimeResourceSupport() {
        try {
            return ImageSingletons.lookup(RuntimeResourceSupport.class);
        } catch (Throwable t) {
            throw new IllegalStateException("Couldn't retrieve RuntimeResourceSupport. " + t.getMessage(), t);
        }
    }

    public static void addResource(String resPath, String filePath) throws IOException {
        log.debug("Adding resource name '{}' from file '{}'", resPath, filePath);
        byte[] content = ByteArrayUtils.readFile(filePath);

        addResource(resPath, content);
    }

    public static void addResource(String resPath, byte[] content) {
        log.debug("Adding resource name '{}', {} bytes", resPath, content.length);

        Module unnamed = ClassLoader.getSystemClassLoader().getUnnamedModule();
        RuntimeResourceAccess.addResource(unnamed, resPath, content);
    }
}
