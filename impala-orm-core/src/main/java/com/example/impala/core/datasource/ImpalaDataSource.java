package com.example.impala.core.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import lombok.Getter;
import lombok.Setter;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.sql.SQLException;
import java.sql.Statement;

public class ImpalaDataSource extends DruidDataSource {
    private static final Logger log = LoggerFactory.getLogger(ImpalaDataSource.class);
    private final UserGroupInformation loginUser;
    private final Object reconnectLock = new Object();
    private volatile boolean reconnecting = false;

    public ImpalaDataSource(UserGroupInformation loginUser) {
        this.loginUser = loginUser;
    }

    @Override
    public DruidPooledConnection getConnection(long maxWaitMillis) throws SQLException {
        // 如果数据源已关闭，尝试重新初始化
        if (this.isClosed()) {
            synchronized (reconnectLock) {
                if (this.isClosed() && !reconnecting) {
                    try {
                        reconnecting = true;
                        log.info("Datasource is closed, trying to reinitialize...");
                        reinitialize();
                    } finally {
                        reconnecting = false;
                    }
                }
            }
        }

        try {
            return loginUser.doAs((PrivilegedExceptionAction<DruidPooledConnection>) () ->
                    superGetConnection(maxWaitMillis));
        } catch (Exception e) {
            if (e instanceof SQLException) {
                throw (SQLException) e;
            }
            throw new SQLException("Failed to get connection: " + e.getMessage(), e);
        }
    }

    private void reinitialize() throws SQLException {
        log.info("Reinitializing datasource...");
        try {
            // 重新进行 Kerberos 认证
            loginUser.checkTGTAndReloginFromKeytab();

            // 重新初始化连接池
            this.inited = false;
            this.init();

            log.info("Datasource reinitialized successfully");
        } catch (Exception e) {
            log.error("Failed to reinitialize datasource", e);
            throw new SQLException("Failed to reinitialize datasource", e);
        }
    }

    @Override
    public void init() throws SQLException {
        if (inited) {
            return;
        }

        synchronized (this) {
            if (inited) {
                return;
            }

            try {
                loginUser.doAs((PrivilegedAction<Void>) () -> {
                    try {
                        superInit();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    return null;
                });
                inited = true;
                log.info("ImpalaDataSource initialized successfully");
            } catch (Exception e) {
                log.error("Failed to initialize ImpalaDataSource", e);
                throw new SQLException("DataSource initialization failed", e);
            }
        }
    }

    protected DruidPooledConnection superGetConnection(long maxWaitMillis) throws SQLException {
        DruidPooledConnection conn = super.getConnection(maxWaitMillis);
        if (isConnectionValid(conn)) {
            return conn;
        }
        conn.close();
        throw new SQLException("Invalid connection obtained from pool");
    }

    private boolean isConnectionValid(DruidPooledConnection conn) {
        if (conn == null) {
            return false;
        }

        try {
            if (conn.isClosed()) {
                return false;
            }
            try (Statement stmt = conn.createStatement()) {
                stmt.setQueryTimeout(5);
                stmt.execute("SELECT 1");
                return true;
            }
        } catch (SQLException e) {
            log.warn("Connection validation failed", e);
            return false;
        }
    }

    public void superInit() throws SQLException {
        super.init();
    }
}