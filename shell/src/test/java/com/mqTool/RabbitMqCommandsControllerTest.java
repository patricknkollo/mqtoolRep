package com.mqTool;

import com.mqTool.component.RabbitMqCommandsController;
import com.mqTool.core.service.RabbitMQService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.boot.configurationprocessor.json.JSONException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.TimeoutException;

@ExtendWith(MockitoExtension.class)
 class RabbitMqCommandsControllerTest {

    @InjectMocks
    private RabbitMqCommandsController rabbitMqCommandsController;

    @Mock
    private RabbitMQService rabbitMQService;

    private String sQueueName = "sQueueName";
    private String tQueueName = "tQueueName";

    private String message = "Hello, RabbitMQ!";

    private String path = "path";

    @Test
     void test_publishMessageOnQueue() {
        Mockito.when(rabbitMQService.publishMessageOnQueue(message, sQueueName)).thenReturn("message published!");
        String result = rabbitMqCommandsController.publishMessageOnQueue(message, sQueueName);
        Assertions.assertEquals("message published!", result);
    }

    @Test
    void test_createQueue()  {
        Mockito.when(rabbitMQService.createQueue(sQueueName)).thenReturn("queue created!");
        String result = rabbitMqCommandsController.createQueue(sQueueName);
        Assertions.assertEquals("queue created!", result);
    }

    @Test
    void test_getAllQueueNames() throws JSONException {
        List<String> queues = List.of(sQueueName, tQueueName);
        ResponseEntity<List> responseEntity = ResponseEntity.ok(queues);
        Mockito.when(rabbitMQService.getAllQueueNames()).thenReturn(queues);
        ResponseEntity<List> result = rabbitMqCommandsController.getAllQueueNames();
        Assertions.assertEquals(responseEntity, result);
    }

    @Test
    void test_movingFromSrcQueueToTargetLocation() throws org.json.JSONException, IOException, URISyntaxException, InterruptedException, org.springframework.boot.configurationprocessor.json.JSONException {
        rabbitMqCommandsController.movingFromSrcQueueToTargetLocation(sQueueName, "",1L, true);
        Mockito.verify(rabbitMQService, Mockito.atLeast(1) ).pullFromParticularQueue(sQueueName, "",1L, true);
    }

    @Test
    void test_moveMessageWithRegexFromOneQueueToAnother() throws Exception {
        Mockito.when(rabbitMQService.movingFromSrcQueueToTargetQueueWithRegex(sQueueName, tQueueName, true, 1, "reg")).thenReturn("message moved!");
        String result = rabbitMqCommandsController.moveFromSrcQueueToTargetQueue(sQueueName, tQueueName, true, 1,"reg");
        Assertions.assertEquals("message moved!", result);
    }

    @Test
    void test_readFileInTextFormat() throws IOException, TimeoutException {
        Mockito.when(rabbitMQService.pushDownloadedMessagesBackToQueue(sQueueName, path)).thenReturn("queue created!");
        String result = rabbitMqCommandsController.readFileInTextFormat(sQueueName, path);
        Assertions.assertEquals("queue created!", result);
    }
}
