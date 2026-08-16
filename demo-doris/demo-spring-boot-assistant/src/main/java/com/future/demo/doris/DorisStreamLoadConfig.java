package com.future.demo.doris;

/**
 * Doris Stream Load 连接配置（对齐 finance FlinkJobConfig.DorisConfig 精简版）。
 */
public final class DorisStreamLoadConfig {

    private final String fenodes;
    private final String username;
    private final String password;
    private final String tableIdentifier;

    public DorisStreamLoadConfig(String fenodes, String username, String password, String tableIdentifier) {
        this.fenodes = fenodes;
        this.username = username;
        this.password = password;
        this.tableIdentifier = tableIdentifier;
    }

    /** demo-doris 默认：FE HTTP 8030，库表 demot.dd */
    public static DorisStreamLoadConfig demoDefaults() {
        return new DorisStreamLoadConfig(
                "127.0.0.1:8030",
                "root",
                "123456",
                "demot.dd");
    }

    public String getFenodes() {
        return fenodes;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getTableIdentifier() {
        return tableIdentifier;
    }
}
