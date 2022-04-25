package com.future.demo.test.client;

import feign.*;
import feign.form.FormEncoder;
import feign.jackson.JacksonEncoder;
import org.apache.commons.cli.*;
import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Application {
    public static void main(String args[]) throws ParseException {
        Option optionHost = new Option("h", "host", true, "目标主机ip（例如：192.168.1.1)");
        optionHost.setRequired(true);
        Option optionPort = new Option("p", "port", true, "目标主机端口（默认值80）");
        optionPort.setRequired(false);

        Options options = new Options();
        options.addOption(optionHost);
        options.addOption(optionPort);

        CommandLine cli;
        CommandLineParser cliParser = new BasicParser();
        HelpFormatter helpFormatter = new HelpFormatter();

        String host = null;
        int port = 80;

        try {
            cli = cliParser.parse(options, args);

            if(cli.hasOption("h")) {
                host = cli.getOptionValue("h");
            }

            if(cli.hasOption("p")) {
                port = Integer.parseInt(cli.getOptionValue("p", "80"));
            }
        } catch (ParseException e) {
            // 解析失败是用 HelpFormatter 打印 帮助信息
            helpFormatter.printHelp("java -jar demo.jar -h 192.168.1.1 -p 80", options);
            throw e;
        }

        Api api = Feign.builder()
                .retryer(Retryer.NEVER_RETRY)
                .options(new Request.Options(15, TimeUnit.SECONDS, 15, TimeUnit.SECONDS, false))
                .encoder(new FormEncoder(new JacksonEncoder()))
                .target(Api.class, "http://" + host + ":" + port);

        Random random = new Random();
        byte [] datumBytes = null;

        // 测试request line超出限制
        datumBytes = new byte[1024*5];
        random.nextBytes(datumBytes);
        String requestUri = Base64.encodeBase64String(datumBytes);
        String varStr = api.testRequestUri(requestUri);
        Assert.assertEquals("Hello world!\n", varStr);

        datumBytes = new byte[1024*8];
        random.nextBytes(datumBytes);
        requestUri = Base64.encodeBase64String(datumBytes);
        try {
            api.testRequestUri(requestUri);
            Assert.fail("预期异常没有抛出");
        } catch (FeignException ex) {
            Assert.assertEquals(414, ex.status());
            Assert.assertTrue(ex.contentUTF8().contains("Request-URI Too Large"));
        }

        // 测试request header超出限制
        datumBytes = new byte[1024*5];
        random.nextBytes(datumBytes);
        String headerFieldValue = Base64.encodeBase64String(datumBytes);
        varStr = api.testHeaderField(headerFieldValue);
        Assert.assertEquals("Hello world!\n", varStr);

        datumBytes = new byte[1024*8];
        random.nextBytes(datumBytes);
        headerFieldValue = Base64.encodeBase64String(datumBytes);
        try {
            api.testHeaderField(headerFieldValue);
            Assert.fail("预期异常没有抛出");
        } catch (FeignException ex) {
            Assert.assertEquals(400, ex.status());
            Assert.assertTrue(ex.contentUTF8().contains("Request Header Or Cookie Too Large"));
        }

        // 测试request line+request header超出限制
        datumBytes = new byte[1024*5];
        random.nextBytes(datumBytes);
        String value = Base64.encodeBase64String(datumBytes);
        try {
            api.testRequestUriHeaderFieldExcceed(value, value);
            Assert.fail("预期异常没有抛出");
        } catch (FeignException ex) {
            Assert.assertEquals(400, ex.status());
            Assert.assertTrue(ex.contentUTF8().contains("Request Header Or Cookie Too Large"));
        }
    }
}
