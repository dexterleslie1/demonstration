package com.future.demo.test.client;

import org.apache.commons.cli.*;

import java.io.IOException;

public class Application {
    public static void main(String args[]) throws ParseException, IOException {
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

        // TODO 基于socket编写模拟程序
    }
}
