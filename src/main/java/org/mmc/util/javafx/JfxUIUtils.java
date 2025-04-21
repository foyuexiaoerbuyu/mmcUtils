package org.mmc.util.javafx;

import javafx.application.Platform;

public class JfxUIUtils {

    public static void runOnUIThread(Runnable r) {
        Platform.runLater(r);
    }
}
