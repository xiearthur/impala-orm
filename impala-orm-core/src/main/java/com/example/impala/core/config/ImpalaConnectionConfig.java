package com.example.impala.core.config;

import com.example.impala.core.exception.ImpalaException;
import java.util.Properties;

public class ImpalaConnectionConfig {
    private String host;
    private int port = 21050;
    private String database = "default";
    private String username;
    private String password;
    private boolean useSSL = false;

    // 连接池配置
    private int initialSize = 5;
    private int minIdle = 5;
    private int maxActive = 20;
    private long maxWait = 30000;
    private int queryTimeout = 60;

    // 连接池维护配置
    private boolean testWhileIdle = true;
    private boolean testOnBorrow = false;
    private boolean testOnReturn = false;
    private long timeBetweenEvictionRunsMillis = 60000;

    private Properties properties = new Properties();

    // JDBC URL 模板
    private String urlTemplate = "jdbc:impala://%s:%d/%s;AuthMech=1;KrbRealm=%s;KrbHostFQDN=%s;KrbServiceName=impala;SSL=0;UseNativeQuery=1";

    public void validate() throws ImpalaException {
        if (host == null || host.trim().isEmpty()) {
            throw new ImpalaException("Host cannot be empty");
        }
        if (port <= 0 || port > 65535) {
            throw new ImpalaException("Invalid port number: " + port);
        }
        if (maxActive < minIdle) {
            throw new ImpalaException("maxActive cannot be less than minIdle");
        }
        if (minIdle < 0) {
            throw new ImpalaException("minIdle cannot be negative");
        }
        if (maxWait < 0) {
            throw new ImpalaException("maxWait cannot be negative");
        }
    }

    // Getters and Setters
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    public String getDatabase() { return database; }
    public void setDatabase(String database) { this.database = database; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isUseSSL() { return useSSL; }
    public void setUseSSL(boolean useSSL) { this.useSSL = useSSL; }

    public int getInitialSize() { return initialSize; }
    public void setInitialSize(int initialSize) { this.initialSize = initialSize; }

    public int getMinIdle() { return minIdle; }
    public void setMinIdle(int minIdle) { this.minIdle = minIdle; }

    public int getMaxActive() { return maxActive; }
    public void setMaxActive(int maxActive) { this.maxActive = maxActive; }

    public long getMaxWait() { return maxWait; }
    public void setMaxWait(long maxWait) { this.maxWait = maxWait; }

    public int getQueryTimeout() { return queryTimeout; }
    public void setQueryTimeout(int queryTimeout) { this.queryTimeout = queryTimeout; }

    public boolean isTestWhileIdle() { return testWhileIdle; }
    public void setTestWhileIdle(boolean testWhileIdle) { this.testWhileIdle = testWhileIdle; }

    public boolean isTestOnBorrow() { return testOnBorrow; }
    public void setTestOnBorrow(boolean testOnBorrow) { this.testOnBorrow = testOnBorrow; }

    public boolean isTestOnReturn() { return testOnReturn; }
    public void setTestOnReturn(boolean testOnReturn) { this.testOnReturn = testOnReturn; }

    public long getTimeBetweenEvictionRunsMillis() { return timeBetweenEvictionRunsMillis; }
    public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public Properties getProperties() { return properties; }
    public void setProperties(Properties properties) { this.properties = properties; }

    public String getUrlTemplate() { return urlTemplate; }
    public void setUrlTemplate(String urlTemplate) { this.urlTemplate = urlTemplate; }
}