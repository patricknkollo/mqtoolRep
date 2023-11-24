package com.mqTool.component;

import com.mqTool.core.service.RabbitMQService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.ResponseEntity;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.TimeoutException;

@ShellComponent
public class RabbitMqCommandsController {

    @Autowired
    private RabbitMQService rabbitMQService;
    Logger log = LoggerFactory.getLogger(RabbitMqCommandsController.class);

    @ShellMethod(key = "mqtool push", value = "to send a message on a particular queue on rabbitMQ")
    public String publishMessageOnQueue(@ShellOption(value = "-m", help = "String message") String message,
                                        @ShellOption(value = "-q", help = "Queue name") String queueName) {
        return rabbitMQService.publishMessageOnQueue(message, queueName);
    }

    @ShellMethod(key = "mqtool create", value = "to create a queue")
    public String createQueue( @ShellOption(value = "-q", help = "Queue name") String queueName)  {
        return rabbitMQService.createQueue(queueName);
    }

    @ShellMethod(key = "mqtool ls", value = "list of names of all the queues on RabbitMQ")
    public ResponseEntity<List> getAllQueueNames() throws JSONException {
        return ResponseEntity.ok(rabbitMQService.getAllQueueNames());
    }

    @ShellMethod(key = "mqtool pull", value = "to pull messages to a text file by copying it or remove it from queue")
    public String movingFromSrcQueueToTargetLocation(
            @ShellOption(value = "-q", help = "Queue Name") String srcQueue,
            @ShellOption(value = "-f", help = "Filename to save the file") String filename,
            @ShellOption(value = "-n", help = "Number of Messages to download, if not provided all messages will be downloaded", defaultValue = "0") Long numberOfMessages,
            @ShellOption(value = "-c", help = "Copy") boolean copy) throws IOException, URISyntaxException, InterruptedException, JSONException {
        return rabbitMQService.pullFromParticularQueue(srcQueue, filename, numberOfMessages, copy);
    }

    @ShellMethod(key = "mqtool move", value = "  to move message(s) in a target queue (original message(s) can be removed from the source queue)")
    public String moveFromSrcQueueToTargetQueue(@ShellOption(value = "-s") String srcQueue,
                                                @ShellOption(value = "-t") String targetQueue,
                                                @ShellOption(value="-c", defaultValue = "false", help = "if value is true, copy of the message will be moved to the target queue.") boolean copy,
                                                @ShellOption(defaultValue = "-1", value = "-n", help = "the first <number> messages otherwise all the messages") long number,
                                                @ShellOption(value="-r", defaultValue = ".*") String regex) throws IOException, TimeoutException, JSONException {
        return rabbitMQService.movingFromSrcQueueToTargetQueueWithRegex(srcQueue, targetQueue, copy,number,regex);
    }

    @ShellMethod(key = "mqtool push txt",  value = "to push the messages from a text file")
    public String readFileInTextFormat(@ShellOption(value = "-q", help = "Queue name") String queueName,
                                       @ShellOption(value = "-p", help = "path of the text file") String filePath) throws IOException, TimeoutException {
        return rabbitMQService.pushDownloadedMessagesBackToQueue(queueName, filePath);
    }

    @ShellMethod(key = "mqtool qs", value = "to get all the queues on rabbitmq and the details")
    public ResponseEntity<List> getQueuesWithExchangeAndMessageNumber()  {
        return ResponseEntity.ok(rabbitMQService.getQueuesWithExchangeAndMessageNumber());
    }

    @ShellMethod(key = "mqtool delete q", value = "to get all the queues on rabbitmq and the details")
    public String deleteOneQueue(@ShellOption(value = "-q", help = "queueName") String queueName) {
        return rabbitMQService.deleteOneQueue(queueName);
    }

    @ShellMethod(key = "mqtool delete qs", value = "to delete one or list of Queues from RabbitMQ")
    public String deleteSeveralQueue(@ShellOption(value = "-q", help = "queueName") List <String> queueNames){
        return rabbitMQService.deleteSeveralQueues(queueNames);
    }
}
