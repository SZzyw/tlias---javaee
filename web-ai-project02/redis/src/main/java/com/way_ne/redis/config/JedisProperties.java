package com.way_ne.redis.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jedis")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JedisProperties {

    private int maxTotal;
    private int maxIdle;
    private int minIdle;
    private int maxWaitMillis;
    private boolean testOnBorrow;
    private boolean testOnReturn;
    private int timeBetweenEvictionRunsMillis;
    private boolean testWhileIdle;
    private int numTestsPerEvictionRun;

    private String host;
    private String password;
    private int port;
    private int timeout;
}