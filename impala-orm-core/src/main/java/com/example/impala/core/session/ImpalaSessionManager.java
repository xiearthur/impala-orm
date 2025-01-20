package com.example.impala.core.session;

import com.example.impala.core.exception.ImpalaException;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Impala会话管理器
 */
public class ImpalaSessionManager {
    private static final Logger log = LoggerFactory.getLogger(ImpalaSessionManager.class);

    private final SqlSessionFactory sqlSessionFactory;

    public ImpalaSessionManager(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    /**
     * 验证连接是否有效
     */
    public boolean validateConnection() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            session.selectOne("SELECT 1");
            return true;
        } catch (Exception e) {
            log.error("Failed to validate connection", e);
            return false;
        }
    }

    /**
     * 获取数据库版本信息
     */
    public String getDatabaseInfo() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            return session.selectOne("SELECT version()");
        } catch (Exception e) {
            log.error("Failed to get database info", e);
            throw new ImpalaException("Failed to get database info", e);
        }
    }
}
