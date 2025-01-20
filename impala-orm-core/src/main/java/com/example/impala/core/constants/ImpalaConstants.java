package com.example.impala.core.constants;

/**
 * Impala常量定义
 */
public class ImpalaConstants {
    // 驱动相关
    public static final String DRIVER_CLASS = "com.cloudera.impala.jdbc41.Driver";
    public static final String URL_PREFIX = "jdbc:impala://";

    // 默认配置
    public static final int DEFAULT_PORT = 21050;
    public static final String DEFAULT_DATABASE = "default";
    public static final String DEFAULT_CHARSET = "UTF-8";

    // SQL相关
    public static final String VALIDATION_QUERY = "SELECT 1";
    public static final long SLOW_QUERY_THRESHOLD = 3000L;

    // 连接池配置
    public static final int DEFAULT_INITIAL_SIZE = 5;
    public static final int DEFAULT_MIN_IDLE = 5;
    public static final int DEFAULT_MAX_ACTIVE = 20;

    private ImpalaConstants() {
        // 防止实例化
    }
}