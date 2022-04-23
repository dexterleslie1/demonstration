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

        // 正常请求
        Map<String, String> parameters = new HashMap<>();
        String varStr = api.limitDefault512k(parameters);
        Assert.assertEquals("接口默认512k限制\n", varStr);

        // 模拟很大的body数据
        parameters = new HashMap<>();
        byte []datumBytes = new byte[1024*512];
        Random varRandom = new Random();
        varRandom.nextBytes(datumBytes);
        parameters.put("datum", Base64.encodeBase64String(datumBytes));
        try {
            api.limitDefault512k(parameters);
            Assert.fail("默认512k限制预期异常没有抛出");
        } catch (FeignException ex) {
            Assert.assertEquals(413, ex.status());
        }

        parameters = new HashMap<>();
        varStr = api.limit128k(parameters);
        Assert.assertEquals("接口128k限制\n", varStr);

        parameters = new HashMap<>();
        datumBytes = new byte[1024*128];
        varRandom.nextBytes(datumBytes);
        parameters.put("datum", Base64.encodeBase64String(datumBytes));
        try {
            api.limit128k(parameters);
            Assert.fail("128k限制预期异常没有抛出");
        } catch (FeignException ex) {
            Assert.assertEquals(413, ex.status());
        }

        parameters = new HashMap<>();
        varStr = api.limit1m(parameters);
        Assert.assertEquals("接口1m限制\n", varStr);

        parameters = new HashMap<>();
        datumBytes = new byte[1024*1024];
        varRandom.nextBytes(datumBytes);
        parameters.put("datum", Base64.encodeBase64String(datumBytes));
        try {
            api.limit1m(parameters);
            Assert.fail("1m限制预期异常没有抛出");
        } catch (FeignException ex) {
            Assert.assertEquals(413, ex.status());
        }
    }
}
