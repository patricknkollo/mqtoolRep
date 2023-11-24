package com.mqTool.web.config;

import com.mqTool.core.service.RabbitMQService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebAppConfig {
    @Bean
    public RabbitMQService rabbitMQService() {
        return new RabbitMQService();
    }
}