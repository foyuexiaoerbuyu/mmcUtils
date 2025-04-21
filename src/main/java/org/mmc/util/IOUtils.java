package org.mmc.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * Utility class for IO operations 提供IO操作的工具类
 */
public class IOUtils {

    /**
     * Close closeable object 关闭可以关闭的对象
     */
    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
//                LogUtils.d("IOUtils",e.toString());
            }
        }
    }

}