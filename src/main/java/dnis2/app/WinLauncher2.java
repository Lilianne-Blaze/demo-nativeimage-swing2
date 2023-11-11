package dnis2.app;

import dnis2.lib.DNISFrame2;
import inlinedawt.runtime.InlineAwtLoader;
import javax.swing.SwingUtilities;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WinLauncher2 {

    public static void main(String[] args) {
        log.info("main...");
        try {
            InlineAwtLoader.defaultInstance().extractAndLoad();

            DNISFrame2 frame = new DNISFrame2("demo-nativeimage-swing2");

            SwingUtilities.invokeAndWait(() -> {
                frame.setVisible(true);
            });
            frame.addLine("Hello world!");
            log.info("frame shown.");

        } catch (Exception e) {
            log.error("Exception " + e.getMessage(), e);
            try {
                Thread.sleep(4000);
            } catch (Exception ee) {
            }
        }
    }
}
