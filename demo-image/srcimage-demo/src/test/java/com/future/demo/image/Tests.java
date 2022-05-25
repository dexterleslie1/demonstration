package com.future.demo.image;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.nio.JpegWriter;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.*;

public class Tests {
    @Test
    public void test() throws IOException {
        String directory = System.getProperty("user.home") + "/Documents";
        String filename = "test.webp";
        String filenameSmall = "1-small.jpeg";

        JpegWriter jpegWriter = new JpegWriter();
        InputStream inputStream = Tests.class.getClassLoader().getResourceAsStream(filename);
        ImmutableImage.loader().fromStream(inputStream).scaleToWidth(100).scaleToHeight(200).output(jpegWriter, new File(directory + "/" + filenameSmall));
        IOUtils.close(inputStream);
    }
}
