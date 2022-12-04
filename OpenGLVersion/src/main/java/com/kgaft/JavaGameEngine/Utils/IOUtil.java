package com.kgaft.JavaGameEngine.Utils;

import java.io.IOException;
import java.io.InputStream;

public class IOUtil {
    public static String inputStreamToString(InputStream is) {
        String result = "";
        int read;
        byte[] buffer = new byte[4 * 1024];
        try {
            while ((read = is.read(buffer, 0, buffer.length)) != -1) {
                result += new String(buffer, 0, read);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getFileExtension(String fileName) {
        char[] fileNameInChars = fileName.toCharArray();
        String extension = "";
        boolean isExtensionStarted = false;
        for (char fileNameInChar : fileNameInChars) {
            if (fileNameInChar == '.') {
                extension = "";
                isExtensionStarted = true;
                continue;
            }
            if (isExtensionStarted) {
                extension += fileNameInChar;
            }
        }
        return extension;
    }
}
