package com.example.impala.mybatis.spring.boot.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Properties;

@ConfigurationProperties(prefix = "impala")
@Data
public class ImpalaProperties {
    private String host;
    private int port = 21050;
    private String database = "default";
    private String driverClassName = "com.cloudera.impala.jdbc41.Driver";
    private String username;
    private String password;
    private boolean useSSL = false;
    private String[] mapperLocations;
    private Properties connectionProperties = new Properties();

    private final Kerberos kerberos = new Kerberos();
    private final Pool pool = new Pool();

    @Data
    public static class Kerberos {
        private boolean enabled = false;
        private String principal;
        private String keytabPath;
        private String krb5ConfPath;
        private String loginConfigPath;  // 添加这个属性
        private long ticketRenewalInterval = 43200000;
    }

    @Data
    public static class Pool {
        private int initialSize = 5;
        private int minIdle = 5;
        private int maxActive = 20;
        private long maxWait = 30000;
        private int queryTimeout = 60;
        private long minEvictableIdleTimeMillis = 300000;
        private String validationQuery = "SELECT 1";
        private boolean testWhileIdle = true;
        private boolean testOnBorrow = false;
        private boolean testOnReturn = false;
        private boolean poolPreparedStatements = true;
        private int maxPoolPreparedStatementPerConnectionSize = 20;
    }
}