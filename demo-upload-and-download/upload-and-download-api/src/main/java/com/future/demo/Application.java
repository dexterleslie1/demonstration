package com.future.demo;

import com.future.demo.exception.ExceptionController;
import com.future.demo.util.Const;
import com.future.demo.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import java.io.File;

@SpringBootApplication
@Import(value = {ExceptionController.class})
@Slf4j
public class Application {
    public static void main(String []args){
        SpringApplication.run(Application.class, args);

        // 在本地临时目录下创建upload-and-download-demo目录
        File temporaryDirectory = Util.getTemporaryDirectory();
        File demoDirectory = new File(temporaryDirectory, Const.DemoDirectoryName);
        if(!demoDirectory.exists()) {
            if(!demoDirectory.mkdirs()) {
                log.error("无法创建{}目录，程序主动退出", demoDirectory.getAbsolutePath());
            } else {
                log.debug("成功创建{}目录", demoDirectory.getAbsolutePath());
            }
        } else {
            log.debug("目录{}已存在", demoDirectory.getAbsolutePath());
        }

        // 在本地临时目录下创建uploaded目录
        File directory = new File(demoDirectory, Const.DirectoryUploaded);
        if(!directory.exists()) {
            if(!directory.mkdirs()) {
                log.error("无法创建{}目录，程序主动退出", directory.getAbsolutePath());
            } else {
                log.debug("成功创建{}目录", directory.getAbsolutePath());
            }
        } else {
            log.debug("目录{}已存在", directory.getAbsolutePath());
        }

        // 在本地临时目录下创建slice目录
        directory = new File(demoDirectory, Const.DirectorySliceUploaded);
        if(!directory.exists()) {
            if(!directory.mkdirs()) {
                log.error("无法创建{}目录，程序主动退出", directory.getAbsolutePath());
            } else {
                log.debug("成功创建{}目录", directory.getAbsolutePath());
            }
        } else {
            log.debug("目录{}已存在", directory.getAbsolutePath());
        }
    }
}
