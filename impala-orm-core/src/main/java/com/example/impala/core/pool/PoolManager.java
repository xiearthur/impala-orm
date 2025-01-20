package com.example.impala.core.pool;

import com.alibaba.druid.pool.DruidPooledConnection;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

@Slf4j
public class PoolManager {
    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> monitorTask;
    private volatile boolean running = false;
    private final Map<String, Object> poolStats = new ConcurrentHashMap<>();

    public PoolManager() {
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "pool-monitor");
            t.setDaemon(true);
            return t;
        });
    }

    public void startMonitoring(String poolName, int intervalSeconds) {
        if (running) {
            return;
        }

        running = true;
        monitorTask = scheduler.scheduleAtFixedRate(() -> {
            collectPoolStats(poolName);
        }, 0, intervalSeconds, TimeUnit.SECONDS);

        log.info("Pool monitoring started for: {}", poolName);
    }

    public void stopMonitoring() {
        if (!running) {
            return;
        }

        running = false;
        if (monitorTask != null) {
            monitorTask.cancel(false);
        }
        scheduler.shutdown();

        log.info("Pool monitoring stopped");
    }

    public boolean validateConnection(DruidPooledConnection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.setQueryTimeout(3); // 3秒超时
                    stmt.execute("SELECT 1");
                    return true;
                }
            }
        } catch (SQLException e) {
            log.warn("Connection validation failed", e);
        }
        return false;
    }

    public void warmUpPool(int initialSize) {
        log.info("Warming up connection pool with {} connections", initialSize);
        CountDownLatch latch = new CountDownLatch(initialSize);

        for (int i = 0; i < initialSize; i++) {
            CompletableFuture.runAsync(() -> {
                try {
                    validateConnection(null); // 这里需要修改为实际的连接获取逻辑
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            if (!latch.await(30, TimeUnit.SECONDS)) {
                log.warn("Pool warm-up did not complete within timeout");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Pool warm-up interrupted", e);
        }
    }

    public Map<String, Object> getPoolStats() {
        return new HashMap<>(poolStats);
    }

    private void collectPoolStats(String poolName) {
        try {
            // 这里可以收集更多的连接池统计信息
            poolStats.put("timestamp", System.currentTimeMillis());
            poolStats.put("poolName", poolName);

            log.debug("Pool stats collected: {}", poolStats);
        } catch (Exception e) {
            log.error("Failed to collect pool stats", e);
        }
    }
}