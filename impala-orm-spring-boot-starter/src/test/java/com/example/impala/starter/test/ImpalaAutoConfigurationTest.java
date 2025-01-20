package com.example.impala.starter.test;

import com.example.impala.core.datasource.ImpalaDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ImpalaAutoConfigurationTest.TestConfig.class)
class ImpalaAutoConfigurationTest {

    @Configuration
    @EnableAutoConfiguration
    static class TestConfig {
    }

    @Autowired
    private DataSource dataSource;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Test
    void testAutoConfiguration() {
        assertNotNull(dataSource);
        assertTrue(dataSource instanceof ImpalaDataSource);
        assertNotNull(sqlSessionFactory);
    }
}