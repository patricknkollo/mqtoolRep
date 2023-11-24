package com.mqTool.core.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class RabbitPropertiesTest {

    private RabbitProperties connectionService = mock(RabbitProperties.class);
    @Test
    public void shouldReturnUsernameWhenUsernameExists() {
        doReturn("guest").when(connectionService).getUsername();
        String result = connectionService.getUsername();
        assertEquals("guest", result);
    }

    @Test
    public void shouldReturnPasswordWhenPasswordExists()  {
        doReturn("guest").when(connectionService).getPassword();
        String result = connectionService.getPassword();
        assertEquals("guest", result);
    }

    @Test
    public void shouldReturnHostWhenHostExists() {
        doReturn("localhost").when(connectionService).getHost();
        String result = connectionService.getHost();
        assertEquals("localhost", result);
    }

    @Test
    public void shouldReturnPortWhenPortdExists() {
        doReturn("5672").when(connectionService).getPort();
        String result = connectionService.getPort();
        assertEquals("5672", result);
    }

    @Test
    public void shouldReturnApiPortWhenApiPortdExists() {
        doReturn("15672").when(connectionService).getApiPort();
        String result = connectionService.getApiPort();
        assertEquals("15672", result);
    }
}


