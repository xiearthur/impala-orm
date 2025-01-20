package com.example.impala.core.sql;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class SqlProcessor {
    private static final Logger log = LoggerFactory.getLogger(SqlProcessor.class);
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 处理并格式化SQL，包括替换参数值
     */
    public static String processSql(BoundSql boundSql, Object parameterObject) {
        String sql = boundSql.getSql();

        if (parameterObject == null || boundSql.getParameterMappings() == null) {
            return formatSql(sql);
        }

        MetaObject metaObject = SystemMetaObject.forObject(parameterObject);
        Map<String, Object> parameterMap = new HashMap<>();

        // 收集参数
        for (ParameterMapping parameterMapping : boundSql.getParameterMappings()) {
            String property = parameterMapping.getProperty();
            Object value;

            if (metaObject.hasGetter(property)) {
                value = metaObject.getValue(property);
            } else if (boundSql.hasAdditionalParameter(property)) {
                value = boundSql.getAdditionalParameter(property);
            } else {
                value = null;
            }

            parameterMap.put(property, value);
        }

        // 替换参数
        return replaceSqlParameters(sql, parameterMap);
    }

    /**
     * 格式化SQL语句
     */
    public static String formatSql(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return "";
        }

        // 基本的SQL格式化
        sql = sql.replaceAll("\\s+", " ")
                .replaceAll("\\s*,\\s*", ", ")
                .replaceAll("\\s*=\\s*", " = ")
                .trim();

        // 添加换行和缩进
        sql = addLineBreaksAndIndentation(sql);

        return sql;
    }

    /**
     * 替换SQL中的参数
     */
    private static String replaceSqlParameters(String sql, Map<String, Object> params) {
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = "\\#\\{" + entry.getKey() + "\\}";
            String value = formatParameterValue(entry.getValue());
            sql = sql.replaceAll(key, value);
        }
        return formatSql(sql);
    }

    /**
     * 格式化参数值
     */
    private static String formatParameterValue(Object value) {
        if (value == null) {
            return "NULL";
        }

        if (value instanceof String) {
            return "'" + escapeString(value.toString()) + "'";
        }

        if (value instanceof Date) {
            return "'" + DATE_FORMAT.format((Date) value) + "'";
        }

        if (value instanceof Boolean) {
            return value.toString().toUpperCase();
        }

        if (value instanceof Collection) {
            return formatCollection((Collection<?>) value);
        }

        return value.toString();
    }

    /**
     * 格式化集合类型的参数
     */
    private static String formatCollection(Collection<?> collection) {
        if (collection == null || collection.isEmpty()) {
            return "()";
        }

        StringBuilder sb = new StringBuilder("(");
        Iterator<?> it = collection.iterator();
        while (it.hasNext()) {
            sb.append(formatParameterValue(it.next()));
            if (it.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * 转义字符串中的特殊字符
     */
    private static String escapeString(String str) {
        return str.replace("'", "''")
                .replace("\\", "\\\\");
    }

    /**
     * 添加换行和缩进
     */
    private static String addLineBreaksAndIndentation(String sql) {
        String[] keywords = {
                "SELECT", "FROM", "WHERE", "AND", "OR", "ORDER BY",
                "GROUP BY", "HAVING", "JOIN", "LEFT JOIN", "RIGHT JOIN",
                "INNER JOIN", "OUTER JOIN", "ON", "UNION"
        };

        for (String keyword : keywords) {
            sql = sql.replaceAll("\\s+" + keyword + "\\s+", "\n" + keyword + " ");
        }

        return sql;
    }

    /**
     * 解析查询类型
     */
    public static String getQueryType(String sql) {
        sql = sql.trim().toUpperCase();
        if (sql.startsWith("SELECT")) return "SELECT";
        if (sql.startsWith("INSERT")) return "INSERT";
        if (sql.startsWith("UPDATE")) return "UPDATE";
        if (sql.startsWith("DELETE")) return "DELETE";
        if (sql.startsWith("MERGE")) return "MERGE";
        if (sql.startsWith("TRUNCATE")) return "TRUNCATE";
        return "UNKNOWN";
    }

    /**
     * 检查是否是大查询
     */
    public static boolean isLargeQuery(String sql) {
        sql = sql.toLowerCase();
        // 检查是否包含聚合函数
        boolean hasAggregation = sql.contains("count(") ||
                sql.contains("sum(") ||
                sql.contains("avg(") ||
                sql.contains("max(") ||
                sql.contains("min(");

        // 检查是否有分组
        boolean hasGroupBy = sql.contains("group by");

        // 检查是否有排序
        boolean hasOrderBy = sql.contains("order by");

        // 检查是否有多表连接
        boolean hasJoin = sql.contains("join");

        // 根据复杂度判断是否是大查询
        return (hasAggregation && hasGroupBy) ||
                (hasJoin && (hasGroupBy || hasOrderBy)) ||
                countJoins(sql) > 2;
    }

    /**
     * 统计连接数量
     */
    private static int countJoins(String sql) {
        int count = 0;
        String[] joins = {"join", "inner join", "left join", "right join", "full join"};
        for (String join : joins) {
            int index = 0;
            while ((index = sql.indexOf(join, index)) != -1) {
                count++;
                index += join.length();
            }
        }
        return count;
    }
}