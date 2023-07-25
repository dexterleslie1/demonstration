package com.future.demo.ip;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.*;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.io.UrlResource;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Geoip2Tests {
    // 全局静态常量，GeoIP2 离线库文件名
//    private static final String geoip2DB = "GeoLite2-City.mmdb";

    // 全局静态变量，保证类加载时加载一次，避免反复读取 GeoIP2 离线库
    private static DatabaseReader reader;

    @BeforeClass
    public static void setup() throws IOException {
        UrlResource urlResource = new UrlResource("https://bucketxyh.oss-cn-hongkong.aliyuncs.com/ip/GeoLite2-City.mmdb");
        File temporaryFile = File.createTempFile("GeoLite2-City.mmdb", ".tmp");
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(temporaryFile);
            StreamUtils.copy(urlResource.getInputStream(), outputStream);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if(outputStream!=null) {
                outputStream.close();
                outputStream = null;
            }
        }

        String dbPath = temporaryFile.getAbsolutePath();

        // 读取 GeoIP2 离线库，jar 包中的资源文件无法以 File 方式读取，需要用 InputStream 流式读取
        FileInputStream inputStream = new FileInputStream(dbPath);
        reader = new DatabaseReader.Builder(inputStream).build();
    }

    @AfterClass
    public static void teardown() throws IOException {
        if(reader != null) {
            reader.close();
        }
    }

    @Test
    public void test() throws IOException, GeoIp2Exception {
        // 这个是广州ip，geoip2不能解析：119.143.73.28
        String ip = "121.8.215.106";
        // 根据 IP 地址查询结果
        InetAddress ipAddress = InetAddress.getByName(ip);
        CityResponse response = reader.city(ipAddress);
        // 从查询结果中提取信息（国家，省份，城市，邮编，定位）
        Country country = response.getCountry();
        Subdivision subdivision = response.getMostSpecificSubdivision();
        City city = response.getCity();
//        Postal postal = response.getPostal();
//        Location location = response.getLocation();
        // 获取信息的中文名称
        Assert.assertEquals("中国", country.getNames().get("zh-CN"));
        Assert.assertEquals("CN", country.getIsoCode());
        Assert.assertEquals("广东", subdivision.getNames().get("zh-CN"));
        Assert.assertEquals("GD", subdivision.getIsoCode());
        Assert.assertEquals("广州市", city.getNames().get("zh-CN"));
    }
}
