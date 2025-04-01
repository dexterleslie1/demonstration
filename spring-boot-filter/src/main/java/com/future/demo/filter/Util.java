package com.future.demo.filter;

import org.springframework.util.DigestUtils;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Util {
    // 返回user.home目录
    public static File getUserHomeDirectory() {
        String userHome = System.getProperty("user.home");
        File userHomeDirectory = new File(userHome);
        return userHomeDirectory;
    }

    // 返回临时目录
    public static File getTemporaryDirectory() {
        String temporaryDirectory = System.getProperty("java.io.tmpdir");
        File file = new File(temporaryDirectory);
        return file;
    }

    // 返回文件md5值
    public static String getFileMd5(File file) throws IOException {
        String path = file.getAbsolutePath();
        InputStream inputStream = null;
        try {
            inputStream = Files.newInputStream(Paths.get(path));
            return DigestUtils.md5DigestAsHex(inputStream);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if(inputStream!=null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    //
                }
                inputStream = null;
            }
        }
    }

    // 对指定文件进行分片
    public static void slice(File file, long sliceSizeInKB) throws IOException {
        if(!file.exists()) {
            String message = String.format("文件 %s 不存在", file.getAbsolutePath());
            throw new IllegalArgumentException(message);
        }

        long fileSize = file.length();
        long sliceSizeInBytes = sliceSizeInKB*1024;
        int sliceCount = fileSize%sliceSizeInBytes==0?(int)(fileSize/sliceSizeInBytes):(int)((fileSize+sliceSizeInBytes)/sliceSizeInBytes);
        String filename = file.getName();
        for(int i=0; i<sliceCount; i++) {
            String sliceFilename = filename + ".slice." + (i+1);
            FileInputStream inputStream = null;
            FileOutputStream outputStream = null;
            try {
                inputStream = new FileInputStream(file);

                File sliceFile = new File(file.getParentFile(), sliceFilename);
                outputStream = new FileOutputStream(sliceFile);
                long start = i*sliceSizeInBytes;
                long end = Math.min(start + sliceSizeInBytes, fileSize)-1;
                StreamUtils.copyRange(inputStream, outputStream, start, end);
            } catch (Exception ex) {
                throw ex;
            } finally {
                if(inputStream!=null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        //
                    }
                    inputStream = null;
                }
            }
        }
    }
}
