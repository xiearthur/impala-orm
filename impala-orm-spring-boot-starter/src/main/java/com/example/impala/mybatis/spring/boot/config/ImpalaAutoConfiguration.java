package com.example.impala.mybatis.spring.boot.config;

import com.example.impala.core.datasource.ImpalaDataSource;
import com.example.impala.core.datasource.KerberosInitor;
import com.example.impala.mybatis.spring.boot.properties.ImpalaProperties;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration  // 修改这里，使用全限定名
@EnableConfigurationProperties(ImpalaProperties.class)
public class ImpalaAutoConfiguration {

    @Bean(name = "impalaDataSource", initMethod = "init", destroyMethod = "close")
    @ConditionalOnMissingBean
    public ImpalaDataSource impalaDataSource(ImpalaProperties properties) throws Exception {
        // 1. Kerberos认证
        Configuration conf = new Configuration();
        KerberosInitor.initKerberosEnv(conf,
                properties.getKerberos().getPrincipal(),
                properties.getKerberos().getKeytabPath(),
                properties.getKerberos().getKrb5ConfPath(),
                properties.getKerberos().getLoginConfigPath());

        // 2. 获取认证用户
        UserGroupInformation loginUser = UserGroupInformation.getLoginUser();

        // 3. 创建数据源
        ImpalaDataSource datasource = new ImpalaDataSource(loginUser);

        // 4. 配置连接信息
        datasource.setUrl(buildJdbcUrl(properties));
        datasource.setDriverClassName(properties.getDriverClassName());

        // 5. 配置连接池
        configurePool(datasource, properties);

        return datasource;
    }

    private String buildJdbcUrl(ImpalaProperties properties) {
        StringBuilder url = new StringBuilder();
        url.append("jdbc:impala://")
                .append(properties.getHost())
                .append(":")
                .append(properties.getPort())
                .append("/")
                .append(properties.getDatabase());

        if (properties.getKerberos().isEnabled()) {
            url.append(";AuthMech=1")
                    .append(";KrbRealm=").append(extractRealm(properties.getKerberos().getPrincipal()))
                    .append(";KrbHostFQDN=").append(properties.getHost())
                    .append(";KrbServiceName=impala")
                    .append(";SSL=0")
                    .append(";UseNativeQuery=1");
        }

        return url.toString();
    }

    private String extractRealm(String principal) {
        return principal.substring(principal.lastIndexOf('@') + 1);
    }

    private void configurePool(ImpalaDataSource datasource, ImpalaProperties properties) {
        ImpalaProperties.Pool pool = properties.getPool();
        datasource.setInitialSize(pool.getInitialSize());
        datasource.setMinIdle(pool.getMinIdle());
        datasource.setMaxActive(pool.getMaxActive());
        datasource.setMaxWait(pool.getMaxWait());
        datasource.setMinEvictableIdleTimeMillis(pool.getMinEvictableIdleTimeMillis());
        datasource.setValidationQuery(pool.getValidationQuery());
        datasource.setTestWhileIdle(pool.isTestWhileIdle());
        datasource.setTestOnBorrow(pool.isTestOnBorrow());
        datasource.setTestOnReturn(pool.isTestOnReturn());
        datasource.setPoolPreparedStatements(pool.isPoolPreparedStatements());
        datasource.setMaxPoolPreparedStatementPerConnectionSize(
                pool.getMaxPoolPreparedStatementPerConnectionSize());
    }
}