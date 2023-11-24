package com.mqTool.core.service;

import com.rabbitmq.client.Channel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.configurationprocessor.json.JSONException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RabbitMQServiceTest {

    private final Channel mockChannel = mock(Channel.class);

    private RabbitProperties rabbitProperties = mock(RabbitProperties.class);

    private ConnectionService connectionService= mock(ConnectionService.class);

    private HttpClient mockHttpClient = mock(HttpClient.class);
    private HttpRequest mockHttpRequest = mock(HttpRequest.class);


    private RabbitMQService mockService = mock(RabbitMQService.class);
    @Spy
    @InjectMocks
    private RabbitMQService service;


    @Test
    public void shouldReturnErrorWhenSrcQueueNotExist() throws IOException, JSONException,TimeoutException {
        doReturn(false).when(service).checkIfQueueExists("source");
        String result = service.movingFromSrcQueueToTargetQueueWithRegex("source",
                "targetQueue", false, 10, "regex");
        assertEquals("The queue source does not exist", result);
    }

    @Test
    public void shouldReturnErrorWhenTargetQueueNotExist() throws IOException, JSONException,TimeoutException {
        doReturn(true).when(service).checkIfQueueExists("source");
        doReturn(false).when(service).checkIfQueueExists("targetQueue");
        String result = service.movingFromSrcQueueToTargetQueueWithRegex("source",
                "targetQueue", false, 10, "regex");
        assertEquals("The queue targetQueue does not exist", result);
    }

    @Test
    public void shouldReturnErrorWhenSourceAndTargetQueuesTheSame() throws Exception {
        doReturn(true).when(service).checkIfQueueExists("src");

        String result = service.movingFromSrcQueueToTargetQueueWithRegex("src",
                "src", false, 10, "regex");
        assertEquals("Please make sure that the source and target queues are different", result);
    }

    @Test
    public void shouldReturnErrorWhenNumberIsZero() throws Exception {
        doReturn(true).when(service).checkIfQueueExists("srcQueue");
        doReturn(true).when(service).checkIfQueueExists("targetQueue");
        String result = service.movingFromSrcQueueToTargetQueueWithRegex("srcQueue",
                "targetQueue", false, 0, "regex");
        assertEquals("Please enter a valid number greater than 0", result);
    }

    @Test
    public void shouldReturnErrorWhenNumberIsBiggerThanTotalMessages() throws Exception {
        when(connectionService.tryConnection()).thenReturn(mockChannel.getConnection());
        doReturn(true).when(service).checkIfQueueExists(anyString());
        when(mockChannel.messageCount("srcQueue")).thenReturn(5L);
        String result = service.movingFromSrcQueueToTargetQueueWithRegex("srcQueue",
                "targetQueue", false, 10, "regex");
        assertEquals("Please enter a valid number <=5", result);
    }

    @Test
    public void shouldCreateQueueSuccessfully() throws Exception {
        when(connectionService.tryConnection()).thenReturn(mockChannel.getConnection());
        String result = service.createQueue("test Queue");
        assertEquals("new queue created !", result);
        verify(mockChannel).queueDeclare(anyString(), anyBoolean(), anyBoolean(), anyBoolean(), isNull());

    }

    @Test
    public void shouldFailToCreateQueueException() throws Exception {
        when(connectionService.tryConnection()).thenThrow(new IOException("failed to connect"));
        String result = service.createQueue("test Queue");
        assertEquals("Creating a new queue failed", result);
    }

    @Test
    public void shouldReturnQueueNames() throws IOException, InterruptedException, URISyntaxException, JSONException {
        //stubbing getHost() and getApiPort() of connectionService to return non-null values
        when(rabbitProperties.getHost()).thenReturn("localhost");
        when(rabbitProperties.getApiPort()).thenReturn("8080");

        when(rabbitProperties.getUsername()).thenReturn("guest");
        when(rabbitProperties.getPassword()).thenReturn("guest");

        HttpResponse<List<String>> mockHttpResponse = mock(HttpResponse.class);
        List<String> mockResponseBody = new ArrayList<>();
        mockResponseBody.add("queue1");
        mockResponseBody.add("queue2");
        when(mockHttpResponse.body()).thenReturn(mockResponseBody);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockHttpResponse);

        List<String> queues = mockService.getAllQueueNames();
        assertEquals(2, queues.size());
        assertTrue(queues.contains("queue1"));
        assertTrue(queues.contains("queue2"));

        verify(mockHttpClient).send(any(HttpRequest.class), any());

    }

    @Test
    public void checkCaseWhenQueueExists() throws Exception {
        when(rabbitProperties.getHost()).thenReturn("localhost");
        when(rabbitProperties.getApiPort()).thenReturn("8080");

       // MockitoAnnotations.openMocks(this);
        List<String> mockQueues = new ArrayList<>();
        mockQueues.add("queue1");
        mockQueues.add("queue2");
        mockQueues.add("queue3");

        when(mockService.getAllQueueNames()).thenReturn(mockQueues);

        boolean result = mockService.checkIfQueueExists("queue2");

        assertTrue(result);
    }

    @Test
    public void checkCaseWhenQueueDoesNotExists() throws Exception {
        when(rabbitProperties.getHost()).thenReturn("localhost");
        when(rabbitProperties.getApiPort()).thenReturn("8080");

        MockitoAnnotations.openMocks(this);
        List<String> mockQueues = Arrays.asList("queue1", "queue2", "queue3");
        when(mockService.getAllQueueNames()).thenReturn(mockQueues);

        boolean result = mockService.checkIfQueueExists("queue4");

        assertFalse(result);
    }


}

