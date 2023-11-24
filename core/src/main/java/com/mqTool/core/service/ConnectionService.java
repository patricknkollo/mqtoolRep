package com.mqTool.core.service;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.concurrent.TimeoutException;


@Service
public class ConnectionService {

    private final Logger LOGGER = LoggerFactory.getLogger(ConnectionService.class);

    @Autowired
    RabbitProperties rabbitProperties;

    Connection connection;

    ConnectionFactory factory;

    @PostConstruct
    public Connection newConnection()  {
       try {
           factory = new ConnectionFactory();
           factory.setHost(rabbitProperties.getHost());
           factory.setUsername(rabbitProperties.getUsername());
           factory.setPort(Integer.parseInt(rabbitProperties.getPort()));
           factory.setPassword(rabbitProperties.getPassword());
           connection = factory.newConnection();
       }
       catch (IOException  | TimeoutException e) {
           LOGGER.error("Cant create connection %s", e);
           throw new RuntimeException(e);
       }
        return connection;
    }

    public Connection tryConnection()  {
        if(connection!=null && connection.isOpen()){
            return connection;
        }
        return newConnection();

    }

}


