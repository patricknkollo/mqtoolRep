package com.mqTool.config;


import com.mqTool.core.service.RabbitMQService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqShellAppConfig {
    @Bean
    public RabbitMQService rabbitMQService() {
        return new RabbitMQService();
    }

}