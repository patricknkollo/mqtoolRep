package com.mqTool;

import com.mqTool.component.DeleteAndListQController;
import com.mqTool.services.DeleteAndListQService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@ExtendWith(MockitoExtension.class)
 class DeleteAndListQControllerTest {

    @InjectMocks
    private DeleteAndListQController deleteAndListQController;
    @Mock
    private DeleteAndListQService deleteAndListQService;

    private String sQueueName = "sQueueName";
    private String tQueueName = "tQueueName";

    @Test
    void test_publishMessageOnQueue() throws JSONException, IOException, URISyntaxException, InterruptedException {
        List<String> queues = List.of(sQueueName, tQueueName);
        ResponseEntity<List> responseEntity = ResponseEntity.ok(queues);
        Mockito.when(deleteAndListQService.getQueuesWithExchangeAndMessageNumber()).thenReturn(responseEntity);
        ResponseEntity<List> result = deleteAndListQController.getQueuesWithExchangeAndMessageNumber();
        Assertions.assertEquals(responseEntity, result);
    }

    @Test
    void test_deleteOneQueue() throws IOException {
        Mockito.when(deleteAndListQService.deleteOneQueue(sQueueName)).thenReturn("queue deleted");
        String result = deleteAndListQController.deleteOneQueue(sQueueName);
        Assertions.assertEquals("queue deleted", result);
    }

    @Test
    void test_deleteSeveralQueue() throws IOException {
        List<String>queues = List.of(sQueueName);
        Mockito.when(deleteAndListQService.deleteSeveralQueues(queues)).thenReturn("queue deleted");
        String result = deleteAndListQController.deleteSeveralQueue(queues);
        Assertions.assertEquals("queue deleted", result);
    }
}
