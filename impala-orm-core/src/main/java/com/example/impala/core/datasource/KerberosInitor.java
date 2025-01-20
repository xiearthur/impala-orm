package com.example.impala.core.datasource;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class KerberosInitor {
    private static final Logger log = LoggerFactory.getLogger(KerberosInitor.class);
    private static ScheduledExecutorService scheduler;

    public static void initKerberosEnv(Configuration conf,
                                       String principalName,
                                       String keytabPath,
                                       String krb5ConfPath,
                                       String loginConfigPath) throws Exception {
        System.setProperty("java.security.krb5.conf", krb5ConfPath);
        if (loginConfigPath != null) {
            System.setProperty("java.security.auth.login.config", loginConfigPath);
        }
        conf.set("hadoop.security.authentication", "Kerberos");

        // linux 环境会默认读取/etc/krb5.conf文件，win不指定会默认读取C:/Windows/krb5.ini
        UserGroupInformation.setConfiguration(conf);
        UserGroupInformation.loginUserFromKeytab(principalName, keytabPath);

        // 添加定时认证
        startPeriodicKinit(principalName, keytabPath);

        log.info("Kerberos initialized with principal: {}", principalName);
        log.info("Using keytab: {}", keytabPath);
        log.info("Using krb5.conf: {}", krb5ConfPath);
    }

    private static void startPeriodicKinit(String principal, String keytab) {
        if (scheduler != null) {
            return;  // 避免重复初始化
        }

        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            t.setName("Kerberos-Kinit-Thread");
            return t;
        });

        scheduler.scheduleAtFixedRate(() -> {
            try {
                String cmd = String.format("kinit -kt %s %s", keytab, principal);
                Process process = Runtime.getRuntime().exec(cmd);
                int exitCode = process.waitFor();

                if (exitCode == 0) {
                    log.info("Kinit refresh successful");
                } else {
                    log.error("Kinit refresh failed with exit code: {}", exitCode);
                }
            } catch (Exception e) {
                log.error("Failed to refresh kinit", e);
            }
        }, 6, 6, TimeUnit.HOURS);  // 6小时执行一次
    }
}