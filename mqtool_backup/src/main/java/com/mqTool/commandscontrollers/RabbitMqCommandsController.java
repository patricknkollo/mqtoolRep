package com.mqTool.component;

import com.mqTool.jwt.FileUtils;
import com.mqTool.services.HelpOptionsService;
import com.mqTool.services.RabbitsMQService;
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
    private RabbitsMQService rabbitsMQService;
    @Autowired
    private HelpOptionsService helpService;
    Logger log = LoggerFactory.getLogger(RabbitMqCommandsController.class);

    @ShellMethod(key = "mqtool push", value = "to send a message on a particular queue on rabbitMQ")
    public String publishMessageOnQueue(@ShellOption(value = "-m", help = "String message") String message,
                                        @ShellOption(value = "-q", help = "Queue name") String queueName) {
        return rabbitsMQService.publishMessageOnQueue(message, queueName);
    }

    @ShellMethod(key = "mqtool create", value = "to create a queue")
    public String createQueue( @ShellOption(value = "-q", help = "Queue name") String queueName) {
        return rabbitsMQService.createQueue(queueName);
    }

    @ShellMethod(key = "mqtool ls", value = "list of names of all the queues on RabbitMQ")
    public ResponseEntity<List> getAllQueueNames() throws JSONException, IOException, URISyntaxException, InterruptedException {
        return rabbitsMQService.getAllQueueNames();
    }

    @ShellMethod(key = "mqtool pull", value = "to pull messages to a text file by copying it or remove it from queue")
    public void movingFromSrcQueueToTargetLocation(
            @ShellOption(value = "-q", help = "Queue Name") String srcQueue,
            @ShellOption(value = "-n", help = "Number of Messages to download, if not provided all messages will be downloaded", defaultValue = "0") int numberOfMessages,
            @ShellOption(value = "-c", help = "Copy") boolean copy) throws JSONException, IOException, URISyntaxException, InterruptedException {
        rabbitsMQService.pullFromParticularQueue(srcQueue, numberOfMessages, copy);
        System.out.println("Press Ctrl+C to exit.");
    }

    @ShellMethod(key = "mqtool move", value = "  to move message(s) in a target queue (original message(s) can be removed from the source queue)")
    public String moveFromSrcQueueToTargetQueue(@ShellOption(value = "-s") String srcQueue,
                                                @ShellOption(value = "-t") String targetQueue,
                                                @ShellOption(value="-c", defaultValue = "true", help = "if true, original message still in the source queue otherwise removed") boolean copy,
                                                @ShellOption(defaultValue = "-1", value = "-n", help="the first <number> messages otherwise all the messages") long number,
                                                @ShellOption(value="-r", defaultValue = ".*") String regex) throws JSONException, IOException, URISyntaxException, InterruptedException, TimeoutException {
        return rabbitsMQService.movingFromSrcQueueToTargetQueueWithRegex(srcQueue, targetQueue, copy,number,regex);
    }

    @ShellMethod(key = "mqtool push txt",  value = "to push the messages from a text file")
    public String readFileInTextFormat(@ShellOption(value = "-q", help = "Queue name") String queueName,
                                       @ShellOption(value = "-p", help = "path of the text file") String filePath) throws IOException, TimeoutException {
        return rabbitsMQService.pushDownloadedMessagesBackToQueue(queueName, filePath);
    }

    @ShellMethod(key = "mqtool --help", value = "list of help Options with all flags and tags")
    public String getHelpOptions() {
        return helpService.getHelpOptions();
    }
}
