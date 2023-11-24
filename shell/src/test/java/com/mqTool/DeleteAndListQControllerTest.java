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

import java.io.IOException;
import java.util.List;

@ExtendWith(MockitoExtension.class)
 class DeleteAndListQControllerTest {

    @InjectMocks
    private RabbitMqCommandsController rabbitMqCommandsController;
    @Mock
    private RabbitMQService rabbitMQService;

    private String sQueueName = "sQueueName";
    private String tQueueName = "tQueueName";

    @Test
    void test_publishMessageOnQueue(){
        Mockito.when(rabbitMQService.publishMessageOnQueue(sQueueName, tQueueName)).thenReturn("response");
        String result = rabbitMqCommandsController.publishMessageOnQueue(sQueueName, tQueueName);
        Assertions.assertEquals("response", result);
    }

    @Test
    void test_deleteOneQueue() throws IOException {
        Mockito.when(rabbitMqCommandsController.deleteOneQueue(sQueueName)).thenReturn("queue deleted");
        String result = rabbitMqCommandsController.deleteOneQueue(sQueueName);
        Assertions.assertEquals("queue deleted", result);
    }

    @Test
    void test_deleteSeveralQueue()  {
        List<String>queues = List.of(sQueueName);
        Mockito.when(rabbitMQService.deleteSeveralQueues(queues)).thenReturn("queue deleted");
        String result = rabbitMqCommandsController.deleteSeveralQueue(queues);
        Assertions.assertEquals("queue deleted", result);
    }
}
