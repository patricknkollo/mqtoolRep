package com.mqTool.core.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitProperties {

    private String host;
    private String port;
    private String username;
    private String password;
    private String apiPort;

        public RabbitProperties(@Value("${spring.rabbitmq.host}") String initHost,
                                @Value("${spring.rabbitmq.port}") String initPort,
                                @Value("${spring.rabbitmq.username}")String initUsername,
                                @Value("${spring.rabbitmq.password}")String initPassword,
                                @Value("${spring.rabbitmq.api.port}")String initApiPort
                                ) {
            this.host = initHost;
            this.port=initPort;
            this.username=initUsername;
            this.password=initPassword;
            this.apiPort=initApiPort;
        }


    public  String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUsername(){
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getApiPort() {
        return apiPort;
    }

    public void setApiPort(String apiPort)  {
        this.apiPort = apiPort;
    }
}