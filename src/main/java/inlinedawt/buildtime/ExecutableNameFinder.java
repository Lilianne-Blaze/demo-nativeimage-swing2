package inlinedawt.buildtime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Iterator;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExecutableNameFinder {

    private static final ExecutableNameFinder DEF_INST = new ExecutableNameFinder();

    public static ExecutableNameFinder defaultInstance() {
        return DEF_INST;
    }

    public Optional<String> findNameOpt() {
        return findNameFromArgs1Opt();
    }

    private Optional<String> findNameFromArgs1Opt() {
        try {
            File tmpDir = new File("target\\tmp").getAbsoluteFile();
            log.debug("Checking folder {} for args files", tmpDir);
            File[] argFiles = tmpDir.listFiles(this::isArgsFileFilter);
            log.debug("{} args files found.", argFiles.length);

            File newestFile = null;
            long newestFileMillis = Long.MIN_VALUE;
            for (File argFile : argFiles) {
                long argFileMillis = argFile.lastModified();
                if (argFileMillis > newestFileMillis) {
                    newestFile = argFile;
                    newestFileMillis = argFileMillis;
                }
            }
            log.debug("Newest args file: {}", newestFile);

            try (BufferedReader br = new BufferedReader(new FileReader(newestFile))) {

                Iterator<String> linesIt = br.lines().iterator();
                while (linesIt.hasNext()) {
                    String line = linesIt.next();
                    if (line.equals("-o")) {
                        String line2 = linesIt.next();

                        if (line2.startsWith("'")) {
                            line2 = line2.substring(1, line2.length() - 1);
                            String name = line2.substring(line2.lastIndexOf("\\") + 1);
                            if (!name.endsWith(".exe")) {
                                name = name + ".exe";
                                log.debug("Name: {}", name);
                            }
                            return Optional.of(name);
                        }
                    }
                }
            }

        } catch (Exception e) {
        }
        return Optional.empty();
    }

    private boolean isArgsFileFilter(File dir, String name) {
        return name.startsWith("native-image-") && name.endsWith(".args");
    }

}
