package com.mqTool.services;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

public class ReadProperties {
    public Properties readPropertiesFile(String filename) throws IOException {
        FileInputStream fileInputStream = null;
        Properties props = null;
        try {
            fileInputStream = new FileInputStream(filename);
            props = new Properties();
            props.load(fileInputStream);
        } catch (FileNotFoundException filenotfoundex) {
            filenotfoundex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fileInputStream.close();
        }
        return props;
    }

    String fileName = "application.properties";

    Connection connection;
    Channel channel;
    ConnectionFactory factory;
    public Channel tryConnection() throws IOException, TimeoutException {
        String host = getHost();
        factory = new ConnectionFactory();
        factory.setHost(host);

        if(connection!= null && connection.isOpen()) {
            closeConnection();
        }

        connection = factory.newConnection();
        channel = connection.createChannel();
        return channel;
    }

    public void closeConnection() throws IOException, TimeoutException {
        channel.close();
        connection.close();
    }

    public String getHost() throws IOException {
        return readPropertiesFile(fileName).getProperty("spring.rabbitmq.host");
    }

    public String getPort() throws IOException {
        return readPropertiesFile(fileName).getProperty("spring.rabbitmq.port");
    }
    public String getUsername() throws IOException {
        return readPropertiesFile(fileName).getProperty("spring.rabbitmq.username");
    }
    public String getPassword() throws IOException {
        return readPropertiesFile(fileName).getProperty("spring.rabbitmq.password");
    }
    public String getApiPort() throws IOException {
        return readPropertiesFile(fileName).getProperty("spring.rabbitmq.api.port");
    }
    public String getWebPort() throws IOException {
        return readPropertiesFile(fileName).getProperty("server.port");
    }
}