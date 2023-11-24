package com.mqTool;

import com.mqTool.component.RabbitMqCommandsController;
import com.mqTool.services.HelpOptionsService;
import com.mqTool.services.RabbitsMQService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.TimeoutException;

//@RunWith(SpringRunner.class)
//@SpringBootTest  -->  more for integration Tests
@ExtendWith(MockitoExtension.class)  // --> for pure Mocking
 class RabbitMqCommandsControllerTest {

    @InjectMocks
    private RabbitMqCommandsController rabbitMqCommandsController;

    @Mock
    private RabbitsMQService rabbitsMQService;


    @Mock
    private HelpOptionsService helpService;

    private String sQueueName = "sQueueName";
    private String tQueueName = "tQueueName";

    private String message = "Hello, RabbitMQ!";

    private String path = "path";

    @Test
     void test_publishMessageOnQueue() {
        Mockito.when(rabbitsMQService.publishMessageOnQueue(message, sQueueName)).thenReturn("message published!");
        String result = rabbitMqCommandsController.publishMessageOnQueue(message, sQueueName);
        Assertions.assertEquals("message published!", result);
    }

    @Test
    void test_createQueue() {
        Mockito.when(rabbitsMQService.createQueue(sQueueName)).thenReturn("queue created!");
        String result = rabbitMqCommandsController.createQueue(sQueueName);
        Assertions.assertEquals("queue created!", result);
    }


    @Test
    void test_getAllQueueNames() throws JSONException, IOException, URISyntaxException, InterruptedException {
        List<String> queues = List.of(sQueueName, tQueueName);
        ResponseEntity<List> responseEntity = ResponseEntity.ok(queues);
        Mockito.when(rabbitsMQService.getAllQueueNames()).thenReturn(responseEntity);
        ResponseEntity<List> result = rabbitMqCommandsController.getAllQueueNames();
        Assertions.assertEquals(responseEntity, result);
    }

    @Test
    void test_movingFromSrcQueueToTargetLocation() throws JSONException, IOException, URISyntaxException, InterruptedException {
        rabbitMqCommandsController.movingFromSrcQueueToTargetLocation(sQueueName, 1, true);
        Mockito.verify(rabbitsMQService, Mockito.atLeast(1) ).pullFromParticularQueue(sQueueName, 1, true);
    }

    @Test
    void test_moveMessageWithRegexFromOneQueueToAnother() throws Exception {
        Mockito.when(rabbitsMQService.movingFromSrcQueueToTargetQueueWithRegex(sQueueName, tQueueName, true, 1, "reg")).thenReturn("message moved!");
        String result = rabbitMqCommandsController.moveFromSrcQueueToTargetQueue(sQueueName, tQueueName, true, 1,"reg");
        Assertions.assertEquals("message moved!", result);
    }

    @Test
    void test_readFileInTextFormat() throws IOException, TimeoutException {
        Mockito.when(rabbitsMQService.pushDownloadedMessagesBackToQueue(sQueueName, path)).thenReturn("queue created!");
        String result = rabbitMqCommandsController.readFileInTextFormat(sQueueName, path);
        Assertions.assertEquals("queue created!", result);
    }

    @Test
    void test_getHelpOptions() throws Exception {
        Mockito.when(helpService.getHelpOptions()).thenReturn("help docu");
        String result = rabbitMqCommandsController.getHelpOptions();
        Assertions.assertEquals("help docu", result);
    }
}
