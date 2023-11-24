package com.rmqtool;

import com.rmqtool.exception.CommandNotFoundException;
import com.rmqtool.exception.CommandNotValidException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.TimeoutException;

import static com.rmqtool.CmdOption.options;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) throws ParseException, IOException, URISyntaxException, InterruptedException, TimeoutException, CommandNotValidException, CommandNotFoundException, JSONException {
        LOGGER.info("Running...");

        CommandLineParser clp = new DefaultParser();
        CommandLine cmd = clp.parse(options(), args);
        CliController controller = new CliController(args);

        if (cmd.getArgList().isEmpty()) {
            throw new CommandNotFoundException("You have not entered any input command.");
        }

        switch (cmd.getArgList().get(0)) {
            case "push" -> controller.push(cmd);
            case "pushText" -> controller.pushTxt(cmd);
            case "pull" -> controller.pull(cmd);
            case "move" -> controller.move(cmd);
            case "qs" -> controller.qs();
            case "qList" ->controller.qList();
            case "help" -> controller.printHelp();
            default -> controller.validate(cmd);
        }
        System.exit(0);
    }

}