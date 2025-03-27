package com.future.demo;

import org.apache.commons.compress.archivers.zip.Zip64Mode;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ZipCompressionTests {
    /**
     * 测试 zip -r x.zip x
     */
    @Test
    public void test() throws IOException {
        // zip 文件名
        String filenameZip = "archive.zip";
        String filepathZip = "/tmp/" + filenameZip;

        // zip 压缩并归档输出流
        ZipArchiveOutputStream zipArchiveOutputStream = null;
        try {
            zipArchiveOutputStream = new ZipArchiveOutputStream(new File(filepathZip));

            // 仅在需要时使用Zip64扩展。这是默认模式，它会在文件大小或条目数量超过ZIP格式的标准限制时自动启用Zip64扩展。
            zipArchiveOutputStream.setUseZip64(Zip64Mode.AsNeeded);

            // 添加文件归档entry到压缩包
            File file = new File("1.txt");
            String entryName = file.getName();
            // 文件压缩到 a 文件夹
            ZipArchiveEntry entry = new ZipArchiveEntry(file, "a/" + entryName);
            zipArchiveOutputStream.putArchiveEntry(entry);
            IOUtils.copy(Files.newInputStream(file.toPath()), zipArchiveOutputStream);
            zipArchiveOutputStream.closeArchiveEntry();

            // 添加文件归档entry到压缩包
            file = new File("2.txt");
            entryName = file.getName();
            // 文件压缩到 a 文件夹
            entry = new ZipArchiveEntry(file, "a/" + entryName);
            zipArchiveOutputStream.putArchiveEntry(entry);
            IOUtils.copy(Files.newInputStream(file.toPath()), zipArchiveOutputStream);
            zipArchiveOutputStream.closeArchiveEntry();
        } finally {
            // 压缩完毕
            if (zipArchiveOutputStream != null) {
                zipArchiveOutputStream.finish();
                zipArchiveOutputStream.close();
            }
        }
    }
}
