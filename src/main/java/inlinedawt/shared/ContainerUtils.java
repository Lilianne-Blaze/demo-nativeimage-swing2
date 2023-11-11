package inlinedawt.shared;

import inlinedawt.shared.StringUtils;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ClassUtils;

@Slf4j
public class ContainerUtils {

    public static int DEF_BUF_SIZE = 64 * 1024;

    public static int SEP_BLOCK_SIZE = 256;

    private static String SEP_LEFT = "123";

    private static String SEP_RIGHT = "456";

    public static String getSeparator(Container c) {
        return getSeparator(c.getName(), true);
    }

    public static String getSeparator(String name, boolean padded) {
        StringBuilder sb = new StringBuilder();
        int reps = 3;
        for (int i = 0; i < reps; i++) {
            sb.append(SEP_LEFT).append(name).append(SEP_RIGHT);
        }
        while (padded && sb.length() < SEP_BLOCK_SIZE) {
            sb.append("=");
        }
        return sb.toString();
    }

    public static String getNameFromSeparator(String separator) {
        if (separator.startsWith(SEP_LEFT)) {
            String s1 = separator.substring(SEP_LEFT.length());
            int i1 = s1.indexOf(SEP_RIGHT);
            String s2 = s1.substring(0, i1);
            return s2;
        }
        throw new IllegalArgumentException("Invalid separator start.");
    }

}
