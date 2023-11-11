package inlinedawt.shared;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class ResourceUtils {

    public static InputStream readResourceAsStream(String resName) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        return cl.getResourceAsStream(resName);
    }

    public static byte[] readResourceAsBytes(String resName) throws IOException {
        return readResourceAsStream(resName).readAllBytes();
    }

    public static String readResourceAsString(String resName) throws IOException {
        return new String(readResourceAsBytes(resName), StandardCharsets.UTF_8);
    }

    public static Optional<String> readResourceAsStringOpt(String resName) {
        try {
            return Optional.of(resName);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

}
