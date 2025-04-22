package org.mmc.util.javafx;

import javafx.application.Platform;

/**
 * JavaFX UI工具类
 */
public class JfxUIUtils {

    /**
     * 在UI线程中执行任务
     * @param r r
     */
    public static void runOnUIThread(Runnable r) {
        Platform.runLater(r);
    }
}
