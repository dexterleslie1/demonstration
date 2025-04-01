package com.future.demo;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.junit.Test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

public class GzipCompressionTests {
    /**
     * 测试 tar -cvzf x.tar.gz x
     *
     * @throws IOException
     */
    @Test
    public void test() throws IOException {
        // x.tar.gz 文件输出流
        FileOutputStream tarGzFileOutputStream = null;
        BufferedOutputStream bufferedOutputStream = null;
        // Gzip 算法输出流
        GzipCompressorOutputStream gzipCompressorOutputStream = null;
        // 归档文件输出流
        TarArchiveOutputStream tarArchiveOutputStream = null;
        try {
            String tarGzPath = "/tmp/archive.tar.gz";

            tarGzFileOutputStream = new FileOutputStream(tarGzPath);
            bufferedOutputStream = new BufferedOutputStream(tarGzFileOutputStream);
            gzipCompressorOutputStream = new GzipCompressorOutputStream(bufferedOutputStream);
            tarArchiveOutputStream = new TarArchiveOutputStream(gzipCompressorOutputStream);

            // 添加文件归档entry到压缩包
            File file = new File("1.txt");
            String entryName = file.getName();
            // 文件压缩到 a 文件夹
            TarArchiveEntry tarEntry = new TarArchiveEntry(file, "a/" + entryName);
            tarArchiveOutputStream.putArchiveEntry(tarEntry);
            IOUtils.copy(Files.newInputStream(file.toPath()), tarArchiveOutputStream);
            tarArchiveOutputStream.closeArchiveEntry();

            // 添加文件归档entry到压缩包
            file = new File("2.txt");
            entryName = file.getName();
            // 文件压缩到 a 文件夹
            tarEntry = new TarArchiveEntry(file, "a/" + entryName);
            tarArchiveOutputStream.putArchiveEntry(tarEntry);
            IOUtils.copy(Files.newInputStream(file.toPath()), tarArchiveOutputStream);
            tarArchiveOutputStream.closeArchiveEntry();
        } finally {
            IOException ioException = null;
            try {
                // 压缩完毕
                if (tarArchiveOutputStream != null) {
                    tarArchiveOutputStream.finish();
                    tarArchiveOutputStream.close();
                }
            } catch (IOException ex) {
                ioException = ex;
            }

            try {
                if (gzipCompressorOutputStream != null)
                    gzipCompressorOutputStream.close();
            } catch (IOException ex) {
                ioException = ex;
            }

            try {
                if (bufferedOutputStream != null)
                    bufferedOutputStream.close();
            } catch (IOException ex) {
                ioException = ex;
            }

            try {
                if (tarGzFileOutputStream != null)
                    tarGzFileOutputStream.close();
            } catch (IOException ex) {
                ioException = ex;
            }

            if (ioException != null) {
                throw ioException;
            }
        }
    }
}
