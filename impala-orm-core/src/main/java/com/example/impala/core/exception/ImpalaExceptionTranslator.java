package com.example.impala.core.exception;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImpalaExceptionTranslator {
    private static final Logger log = LoggerFactory.getLogger(ImpalaExceptionTranslator.class);

    // Impala 错误码映射
    private static final Map<String, String> ERROR_MESSAGES = new HashMap<>();

    static {
        // 常见的Impala错误码和友好提示信息
        ERROR_MESSAGES.put("AnalysisException", "SQL语法分析错误");
        ERROR_MESSAGES.put("AuthorizationException", "权限验证失败");
        ERROR_MESSAGES.put("InternalException", "Impala内部错误");
        ERROR_MESSAGES.put("MemoryLimitExceeded", "内存限制超出");
        ERROR_MESSAGES.put("RuntimeException", "运行时错误");
        ERROR_MESSAGES.put("TimeoutException", "查询超时");
        ERROR_MESSAGES.put("ConnectionException", "连接异常");
    }

    public ImpalaException translate(String sql, Throwable ex) {
        if (ex instanceof SQLException) {
            return translateSQLException(sql, (SQLException) ex);
        }
        return new ImpalaException("执行SQL时发生未知错误", ex);
    }

    private ImpalaException translateSQLException(String sql, SQLException ex) {
        String sqlState = ex.getSQLState();
        int errorCode = ex.getErrorCode();
        String message = ex.getMessage();

        // 记录原始错误信息
        log.debug("SQL执行错误 - SQLState: {}, ErrorCode: {}, Message: {}",
                sqlState, errorCode, message);

        // 解析错误类型
        String errorType = parseErrorType(message);
        String friendlyMessage = getFriendlyMessage(errorType, message);

        // 构建详细错误信息
        StringBuilder detailMessage = new StringBuilder()
                .append(friendlyMessage)
                .append("\n执行的SQL: ").append(sql)
                .append("\n错误详情: ").append(message);

        // 添加问题修复建议
        String suggestion = getSuggestion(errorType, message);
        if (StringUtils.isNotEmpty(suggestion)) {
            detailMessage.append("\n修复建议: ").append(suggestion);
        }

        return new ImpalaException(detailMessage.toString(), ex);
    }

    private String parseErrorType(String message) {
        for (String errorType : ERROR_MESSAGES.keySet()) {
            if (message.contains(errorType)) {
                return errorType;
            }
        }
        return "UnknownError";
    }

    private String getFriendlyMessage(String errorType, String originalMessage) {
        return ERROR_MESSAGES.getOrDefault(errorType, "数据库操作失败");
    }

    private String getSuggestion(String errorType, String message) {
        switch (errorType) {
            case "AnalysisException":
                return "请检查SQL语法是否正确，表名和字段名是否存在";

            case "AuthorizationException":
                return "请确认是否有相应的操作权限，或联系管理员授权";

            case "MemoryLimitExceeded":
                return "建议优化查询语句，减少数据量，或联系管理员调整内存限制";

            case "TimeoutException":
                return "建议优化查询语句，或增加查询超时时间";

            case "ConnectionException":
                return "请检查网络连接和数据库服务是否正常";

            default:
                return "如果问题持续存在，请联系技术支持";
        }
    }

    // 解析常见的Impala错误模式
    private static final Pattern[] ERROR_PATTERNS = {
            Pattern.compile("Table not found: (\\w+)"),
            Pattern.compile("Column '([^']+)' not found"),
            Pattern.compile("Permission denied: (\\w+)"),
            Pattern.compile("Memory limit exceeded: (\\d+)"),
            // 可以添加更多错误模式
    };

    private String parseErrorDetail(String message) {
        for (Pattern pattern : ERROR_PATTERNS) {
            Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return null;
    }
}
