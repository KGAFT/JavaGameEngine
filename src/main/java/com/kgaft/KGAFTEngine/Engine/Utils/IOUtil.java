package com.kgaft.KGAFTEngine.Engine.Utils;

import java.io.*;
import java.nio.ByteBuffer;

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
    public static ByteBuffer readBinaryFile(String fileName) throws IOException {
        File file = new File(fileName);
        long fileSize = file.length();
        ByteBuffer result = ByteBuffer.allocateDirect((int) fileSize);
        byte[] tempBuffer = new byte[4*1024];
        int read;
        FileInputStream fis = new FileInputStream(file);
        while((read=fis.read(tempBuffer, 0, tempBuffer.length))!=-1){
            result.put(tempBuffer, 0, read);
        }
        result.rewind();
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
