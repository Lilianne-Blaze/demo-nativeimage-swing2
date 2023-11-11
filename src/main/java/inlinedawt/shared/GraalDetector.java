package inlinedawt.shared;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

/**
 * Note: same can be checked with org.graalvm.nativeimage.ImageInfo, but this class doesn't have Graal-specific
 * dependencies.
 */
public class GraalDetector {

    public static boolean isGraalNative() {
        String k = System.getProperty("org.graalvm.nativeimage.kind");
        return "executable".equals(k);
    }

    public static boolean isGraalNativeRuntime() {
        String ic = System.getProperty("org.graalvm.nativeimage.imagecode");
        String k = System.getProperty("org.graalvm.nativeimage.kind");
        return "runtime".equals(ic) && "executable".equals(k);
    }

    public static boolean isGraalNativeBuildtime() {
        String ic = System.getProperty("org.graalvm.nativeimage.imagecode");
        String k = System.getProperty("org.graalvm.nativeimage.kind");
        return "buildtime".equals(ic) && "executable".equals(k);
    }

    public static boolean isGraalFallback() {
        String k = System.getProperty("org.graalvm.nativeimage.kind");
        return "fallback-executable".equals(k);
    }

    public static boolean isGraalAgent() {
        String ic = System.getProperty("org.graalvm.nativeimage.imagecode");
        return "agent".equals(ic);
    }

    public static Map<String, String> getGraalProperties() {
        Map<String, String> props = new TreeMap<>();
        System.getProperties().forEach((t, u) -> {
            String tS = t.toString();
            if (tS.toLowerCase().contains("graalvm")) {
                props.put(tS, u.toString());
            }
        });
        return props;
    }

    public static void listGraalProperties(Consumer<String> out) {
        for (Map.Entry<String, String> entry : getGraalProperties().entrySet()) {
            out.accept(entry.getKey() + "=" + entry.getValue());
        }
    }

}
