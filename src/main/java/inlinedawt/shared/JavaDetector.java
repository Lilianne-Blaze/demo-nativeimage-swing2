package inlinedawt.shared;

public class JavaDetector {

    public static boolean isAtLeastVersion(int version) {
        try {
            return Runtime.getRuntime().version().feature() >= version;
        } catch (Exception e) {
            return false;
        }
    }

}
