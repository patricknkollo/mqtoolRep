package com.rmqtool;

import com.mqTool.core.service.RabbitMQService;
import com.rmqtool.exception.CommandNotFoundException;
import com.rmqtool.exception.CommandNotValidException;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONException;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static com.rmqtool.CmdOption.options;

public class CliController {
    RabbitMQService rabbitMQService = new RabbitMQService();
    private static final Logger LOGGER = LoggerFactory.getLogger(CliController.class);
    CommandLineParser clp = new DefaultParser();
    private String[] args;
    CommandLine cmd = clp.parse(options(), args);

    public CliController(String[] args) throws ParseException {
    }

    public void push(CommandLine cmd) throws CommandNotValidException {
        if (!cmd.hasOption("m") || !cmd.hasOption("q")) {
            throw new CommandNotValidException("Input message or Input Queue is not provided");
        } else {
            String message = cmd.getOptionValue("m");
            String queueName = cmd.getOptionValue("q");
            LOGGER.info("RabbitMQService running");
            System.out.println(rabbitMQService.publishMessageOnQueue(message, queueName));
        }
    }

    public void pushTxt(CommandLine cmd) throws IOException, TimeoutException, CommandNotValidException {
        if (!cmd.hasOption("q") || !cmd.hasOption("p")) {
            throw new CommandNotValidException("Input Queue or Input Path is not provided");
        } else {
            String queueName = cmd.getOptionValue("q");
            String filepath = cmd.getOptionValue("p");
            LOGGER.info("RabbitMQService running");
            System.out.println(rabbitMQService.pushDownloadedMessagesBackToQueue(queueName, filepath));
        }
    }

    public void pull(CommandLine cmd) throws IOException, URISyntaxException, InterruptedException, CommandNotValidException, JSONException {
        if (!cmd.hasOption("q")) {
            throw new CommandNotValidException("Input Queue or Input Path is not provided");
        } else if (cmd.hasOption("q") && cmd.hasOption("n")) {
            if(cmd.hasOption("n")) {
                if(!cmd.getOptionValue("n").matches("\\d")) {
                    throw new CommandNotValidException("Value of n should be an integer");
                }
            }
        } else {
            String queueName = cmd.getOptionValue("q");
            String filename;
            if (cmd.hasOption("f")) {
                filename = cmd.getOptionValue("f");
            } else {
                throw new CommandNotValidException("Please provide a valid filename");
            }
            Long numberofMessages;
            if ((cmd.getOptionValue("n")) == null) {
                numberofMessages = 0L;
            } else {
                numberofMessages = (long) Integer.parseInt(cmd.getOptionValue("n"));
            }
            boolean copy = cmd.hasOption("c");
            LOGGER.info("RabbitMQService running");
            System.out.println(rabbitMQService.pullFromParticularQueue(queueName, filename, numberofMessages, copy));
        }
    }

    public void move(CommandLine cmd) throws IOException, URISyntaxException, InterruptedException, TimeoutException, CommandNotValidException, JSONException {
        if (!cmd.hasOption("s") || !cmd.hasOption("t")) {
            throw new CommandNotValidException("Input Queue or Input Path is not provided");
        } else if (cmd.hasOption("s") && cmd.hasOption("t") && ((cmd.hasOption("n")) || (cmd.hasOption("r")))) {
            if (cmd.hasOption("n")) {
                if(!cmd.getOptionValue("n").matches("\\d")) {
                    throw new CommandNotValidException("Value of n should be an integer");
                }
            }
            if (cmd.hasOption("r")) {
                try {
                    Pattern.compile(cmd.getOptionValue("n"));
                } catch (PatternSyntaxException exception) {
                    LOGGER.error("Regex error: " + exception);
                }
            }
        } else {
            String srcQueue = cmd.getOptionValue("s");
            String targetQueue = cmd.getOptionValue("t");
            boolean copy = false;
            int numberofMessages;
            String regex;

            if ((cmd.getOptionValue("n")) == null) {
                numberofMessages = -1;
            } else {
                numberofMessages = Integer.parseInt(cmd.getOptionValue("n"));
            }


            if ((cmd.getOptionValue("r")) == null) {
                regex = ".*";
            } else {
                regex = cmd.getOptionValue("r");
            }
            if (cmd.hasOption("c")) copy = true;

            LOGGER.info("RabbitMQService running");
            System.out.println(rabbitMQService.movingFromSrcQueueToTargetQueueWithRegex(srcQueue, targetQueue, copy, numberofMessages, regex));
        }
    }

    public void qs() throws IOException, URISyntaxException, InterruptedException, JSONException {
        LOGGER.info("RabbitMQService running");
        String queueDetails = rabbitMQService.getQueuesWithExchangeAndMessageNumber().toString();
        System.out.println(queueDetails);
    }
    public void qList() throws IOException, URISyntaxException, InterruptedException, JSONException {
        LOGGER.info("RabbitMQService running");
        String qList = rabbitMQService.getAllQueueNames().toString();
        System.out.println(qList);
    }

     void printHelp( ) {
        Options options = options();
        HelpFormatter formatter = new HelpFormatter();
        PrintWriter pw = new PrintWriter(System.out);
        pw.println("RabbitMQ CLI Tool 1.0");
        pw.println();
        formatter.printUsage(pw, 100, "java -jar mqTool.jar [options] input1 input2 input3");
        formatter.printOptions(pw, 100, options, 2, 5);
        pw.close();
    }

    public void validate(CommandLine cmd) throws CommandNotFoundException {
        throw new CommandNotFoundException("Command input is not valid.");
    }

}