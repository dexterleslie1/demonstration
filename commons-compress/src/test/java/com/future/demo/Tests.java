package com.future.demo;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.junit.Test;

import java.io.*;

// https://stackoverflow.com/questions/13461393/compress-directory-to-tar-gz-with-commons-compress
public class Tests {
    @Test
    public void test() throws IOException {
        FileOutputStream fOut = null;
        BufferedOutputStream bOut = null;
        GzipCompressorOutputStream gzOut = null;
        TarArchiveOutputStream tOut = null;
        try {
            String tarGzPath = "/tmp/archive.tar.gz";
            fOut = new FileOutputStream(new File(tarGzPath));
            bOut = new BufferedOutputStream(fOut);
            gzOut = new GzipCompressorOutputStream(bOut);
            tOut = new TarArchiveOutputStream(gzOut);

            File f = new File("1.txt");
            String entryName = f.getName();
            TarArchiveEntry tarEntry = new TarArchiveEntry(f, entryName);
            tOut.putArchiveEntry(tarEntry);
            IOUtils.copy(new FileInputStream(f), tOut);
            tOut.closeArchiveEntry();

            f = new File("2.txt");
            entryName = f.getName();
            tarEntry = new TarArchiveEntry(f, entryName);
            tOut.putArchiveEntry(tarEntry);
            IOUtils.copy(new FileInputStream(f), tOut);
            tOut.closeArchiveEntry();
        } finally {
            if (tOut != null) {
                tOut.finish();
                tOut.close();
            }
            if (gzOut != null)
                gzOut.close();
            if (bOut != null)
                bOut.close();
            if (fOut != null)
                fOut.close();
        }
    }
}
