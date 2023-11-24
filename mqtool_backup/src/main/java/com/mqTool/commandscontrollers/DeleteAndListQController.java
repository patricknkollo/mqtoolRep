package com.mqTool.component;

import com.mqTool.services.DeleteAndListQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.ResponseEntity;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@ShellComponent
public class DeleteAndListQController {

    @Autowired
    private DeleteAndListQService deleteAndListQService;

    /**
     *
     * @return the list of queues's name with the number of messages in each queue
     * @throws IOException
     * @throws JSONException
     * @throws URISyntaxException
     * @throws InterruptedException
     */
    @ShellMethod(key = "mqtool qs", value = "to get all the queues on rabbitmq and the details")
    public ResponseEntity<List> getQueuesWithExchangeAndMessageNumber() throws IOException, JSONException, URISyntaxException, InterruptedException {
        return deleteAndListQService.getQueuesWithExchangeAndMessageNumber();
    }

    /**
     *
     * @param queueName
     * @return deleted queue
     * @throws IOException
     *
     * command example:    mqtool delete -one -q  queue1    -> will delete queue1
     */
    @ShellMethod(key = "mqtool delete one", value = "to get all the queues on rabbitmq and the details")
    public String deleteOneQueue(@ShellOption(value = "-q") String queueName) throws IOException {
        return deleteAndListQService.deleteOneQueue(queueName);
    }

    /**
     *
     * @param queueNames
     * @return deleted queues
     * @throws IOException
     *
     * command example:    mqtool delete -more -q  queue1 queue2 queue3 ... queuen    -> will delete queue1 ... queuen
     */
    @ShellMethod(key = "mqtool delete more", value = "to delete one or list of Queues from RabbitMQ")
    public String deleteSeveralQueue(@ShellOption(value = "-q") List <String> queueNames) throws IOException {
        return deleteAndListQService.deleteSeveralQueues(queueNames);
    }
}
