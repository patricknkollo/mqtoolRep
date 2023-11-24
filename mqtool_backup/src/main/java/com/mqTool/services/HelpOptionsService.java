package com.mqTool.services;

import org.springframework.stereotype.Service;

@Service
public class HelpOptionsService {
    public String getHelpOptions() {
        return "the most popular commands are \n" +
                "mqtool push       to send a message on a particular queue on rabbitMQ \n" +
                "\n" +
                "e.g.:             mqtool -text push -q <Queue-name> -p <C:\\filepath\\textfile.txt>\n" +
                "                  mqtool -csv  push -q <Queue-name> -p <C:\\filepath\\textfile.csv>\n" +
                "\n" +
                "mqtool pull       to consume a message from one particular queue \n" +
                "\n" +
                "e.g.:             mqtool pull -q <Queue-name> [-n] <number messages to Consume> [-c] to copy without removing from queue" +
                "\n" +
                "mqtool -m         to precise the content of the message \n" +
                "mqtool -q         to precise the name of the queue \n" +
                "mqtool create     to create a particular queue \n" +
                "mqtool move       to move  message(s) of one queue \n" +
                "-s                for the source queue \n" +
                "-t                for the target queue \n" +
                "-c                if true, original message stays in the source queue otherwise it is removed \n" +
                "-n                for the number of messages to work on\n" +
                "mqtool ls         to have the list of names of all the queues on rabbitMQ \n" +
                "                  CTRL+C + anykey -> to close the connection\n" +
                "[]                Optional flags \n" +
                "\n" +
                "\n"

                ;
    }
}

