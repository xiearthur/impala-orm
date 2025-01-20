package com.example.impala.core.sql;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;

/**
 * SQL执行信息封装类
 */
public class SqlExecutionInfo {
    private final String sqlId;              // SQL标识（Mapper方法）
    private final String originalSql;        // 原始SQL
    private final String formattedSql;       // 格式化后的SQL
    private final String queryType;          // 查询类型
    private final Map<String, Object> parameters; // 参数信息
    private final long startTime;            // 开始执行时间
    private long endTime;                    // 结束执行时间
    private long rowCount;                   // 影响行数
    private boolean success;                 // 是否执行成功
    private String errorMessage;             // 错误信息
    private final boolean largeQuery;        // 是否是大查询

    public SqlExecutionInfo(String sqlId, String originalSql, String formattedSql,
                            String queryType, Map<String, Object> parameters) {
        this.sqlId = sqlId;
        this.originalSql = originalSql;
        this.formattedSql = formattedSql;
        this.queryType = queryType;
        this.parameters = new HashMap<>(parameters);
        this.startTime = System.currentTimeMillis();
        this.largeQuery = SqlProcessor.isLargeQuery(originalSql);
    }

    /**
     * 完成SQL执行
     */
    public void complete(long rowCount) {
        this.endTime = System.currentTimeMillis();
        this.rowCount = rowCount;
        this.success = true;
    }

    /**
     * 记录执行失败
     */
    public void fail(String errorMessage) {
        this.endTime = System.currentTimeMillis();
        this.success = false;
        this.errorMessage = errorMessage;
    }

    /**
     * 获取执行时间（毫秒）
     */
    public long getExecutionTime() {
        return endTime - startTime;
    }

    /**
     * 检查是否是慢查询
     */
    public boolean isSlowQuery(long threshold) {
        return getExecutionTime() > threshold;
    }

    // Getters
    public String getSqlId() { return sqlId; }
    public String getOriginalSql() { return originalSql; }
    public String getFormattedSql() { return formattedSql; }
    public String getQueryType() { return queryType; }
    public Map<String, Object> getParameters() { return new HashMap<>(parameters); }
    public long getStartTime() { return startTime; }
    public long getEndTime() { return endTime; }
    public long getRowCount() { return rowCount; }
    public boolean isSuccess() { return success; }
    public String getErrorMessage() { return errorMessage; }
    public boolean isLargeQuery() { return largeQuery; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SQL Execution Info:\n");
        sb.append("SQL ID: ").append(sqlId).append("\n");
        sb.append("Query Type: ").append(queryType).append("\n");
        sb.append("Execution Time: ").append(getExecutionTime()).append("ms\n");
        sb.append("Row Count: ").append(rowCount).append("\n");
        sb.append("Status: ").append(success ? "Success" : "Failed");
        if (!success) {
            sb.append(" - ").append(errorMessage);
        }
        sb.append("\n");
        sb.append("Large Query: ").append(largeQuery).append("\n");
        sb.append("Formatted SQL:\n").append(formattedSql).append("\n");
        sb.append("Parameters: ").append(parameters);
        return sb.toString();
    }
}