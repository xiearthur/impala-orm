package com.example.impala.core.support;


import com.cloudera.dsi.core.utilities.SqlType;
import com.example.impala.core.constants.ImpalaConstants;

/**
 * Impala工具类
 */
public class ImpalaUtils {

    /**
     * 构建JDBC URL
     */
    public static String buildJdbcUrl(String host, int port, String database) {
        StringBuilder url = new StringBuilder(ImpalaConstants.URL_PREFIX)
                .append(host)
                .append(":")
                .append(port);

        if (database != null && !database.isEmpty()) {
            url.append("/").append(database);
        }

        return url.toString();
    }


}