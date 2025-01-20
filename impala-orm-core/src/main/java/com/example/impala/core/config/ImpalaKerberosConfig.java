package com.example.impala.core.config;

import com.example.impala.core.exception.ImpalaException;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ImpalaKerberosConfig {
    private boolean enabled = false;
    private String principal;
    private String keytabPath;
    private String krb5ConfPath;
    private long ticketRenewalInterval = 43200000; // 12小时
    private int retryAttempts = 3;
    private long retryInterval = 60000; // 1分钟

    public void validate() throws ImpalaException {
        if (!enabled) {
            return;
        }

        List<String> errors = new ArrayList<>();

        if (StringUtils.isBlank(principal)) {
            errors.add("Kerberos principal cannot be empty");
        } else if (!principal.contains("@")) {
            errors.add("Invalid Kerberos principal format, missing realm");
        }

        if (StringUtils.isBlank(keytabPath)) {
            errors.add("Keytab path cannot be empty");
        }

        if (StringUtils.isBlank(krb5ConfPath)) {
            errors.add("krb5.conf path cannot be empty");
        }

        if (!errors.isEmpty()) {
            throw new ImpalaException("Kerberos configuration validation failed: "
                    + String.join(", ", errors));
        }
    }

    // Getters and Setters
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getKeytabPath() {
        return keytabPath;
    }

    public void setKeytabPath(String keytabPath) {
        this.keytabPath = keytabPath;
    }

    public String getKrb5ConfPath() {
        return krb5ConfPath;
    }

    public void setKrb5ConfPath(String krb5ConfPath) {
        this.krb5ConfPath = krb5ConfPath;
    }

    public long getTicketRenewalInterval() {
        return ticketRenewalInterval;
    }

    public void setTicketRenewalInterval(long ticketRenewalInterval) {
        this.ticketRenewalInterval = ticketRenewalInterval;
    }

    public int getRetryAttempts() {
        return retryAttempts;
    }

    public void setRetryAttempts(int retryAttempts) {
        this.retryAttempts = retryAttempts;
    }

    public long getRetryInterval() {
        return retryInterval;
    }

    public void setRetryInterval(long retryInterval) {
        this.retryInterval = retryInterval;
    }
}