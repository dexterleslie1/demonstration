package com.future.demo;

import com.future.demo.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

@Slf4j
public class UtilTests {
    @Test
    public void slice() throws IOException {
        String filename = "webuploader-0.1.5.zip";
        File userHomeDirectory = Util.getUserHomeDirectory();
        File file = new File(userHomeDirectory, filename);
        Util.slice(file, 512);
    }

    @Test
    public void md5() throws IOException {
        File homeDirectory = Util.getUserHomeDirectory();
        File file = new File(homeDirectory, "Downloads" + File.separator + "屏幕录制2021-06-15 上午10.27.54.mov");
        String md5 = Util.getFileMd5(file);
        log.debug("文件 {} md5:{}", file.getAbsolutePath(), md5);
    }
}
