// ImpalaSqlInterceptor.java
package com.example.impala.core.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.sql.Connection;
import java.util.Properties;

@Slf4j
@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare",
                args = {Connection.class, Integer.class})
})
public class ImpalaSqlInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler handler = (StatementHandler) invocation.getTarget();
        MetaObject metaObject = SystemMetaObject.forObject(handler);

        // 获取SQL
        BoundSql boundSql = handler.getBoundSql();
        String sql = boundSql.getSql();
        long start = System.currentTimeMillis();

        log.debug("Original SQL: {}", sql);

        // SQL优化处理
        String optimizedSql = optimizeSql(sql);
        if (!sql.equals(optimizedSql)) {
            metaObject.setValue("delegate.boundSql.sql", optimizedSql);
            log.debug("Optimized SQL: {}", optimizedSql);
        }

        try {
            return invocation.proceed();
        } finally {
            long end = System.currentTimeMillis();
            log.debug("SQL execution time: {}ms", (end - start));
        }
    }

    private String optimizeSql(String sql) {
        // 这里可以添加SQL优化逻辑
        // 例如: COMPUTE STATS, 分区裁剪等
        return sql;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // 可以从配置中读取属性
    }
}