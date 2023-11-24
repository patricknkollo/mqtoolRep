package com.rmqtool;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class CmdOption {

    // non param first calls
    static final Option PULL = new Option("pull", false, "To pull messages to text file");
    static final Option PUSH = new Option("push", false, "To push messages");
    static final Option PUSHTXT = new Option("pushTxt", false, "To push messages from text file");
    static final Option MOVE = new Option("move", false, "To move messages");
    static final Option QS = new Option("qs", false, "to get Queue details");
    static final Option QLIST = new Option( "qList", false, "to get list of Queues");

    // flags for passing parameters
    static final Option MSG = Option.builder("m").longOpt("message").desc("Message content").hasArg(true).numberOfArgs(1).optionalArg(false).argName("String").build();
    static final Option QUE = Option.builder("q").longOpt("queue").desc("Queue Name").hasArg(true).numberOfArgs(1).optionalArg(false).argName("String").build();
    static final Option NUM = Option.builder("n").longOpt("number").desc("Number of messages").hasArg(true).numberOfArgs(1).optionalArg(true).argName("integer").build();
    static final Option COPY = Option.builder("c").longOpt("copy").desc("Copy messages flag").hasArg(false).numberOfArgs(1).optionalArg(true).argName("boolean").build();
    static final Option SOURCEQ = Option.builder("s").longOpt("source").desc("Source queue Name").hasArg(true).numberOfArgs(1).optionalArg(false).argName("String").build();
    static final Option TARGETQ = Option.builder("t").longOpt("target").desc("Target queue Name").hasArg(true).numberOfArgs(1).optionalArg(false).argName("String").build();
    static final Option FILEPATH = Option.builder("p").longOpt("path").desc("Path or location of a file").hasArg(true).numberOfArgs(1).optionalArg(false).argName("String").build();
    static final Option FILENAME = Option.builder("f").longOpt("filename").desc("Filename to save the file").hasArg(true).numberOfArgs(1).optionalArg(true).argName("String").build();
    static final Option REGEX = Option.builder("r").longOpt("regex").desc("Regular expression ").hasArg(true).numberOfArgs(1).optionalArg(true).argName("regex").build();


    static Options options() {
        Options options = new Options();
        options.addOption(PULL).addOption(FILENAME).addOption(NUM).addOption(COPY);
        options.addOption(PUSH).addOption(MSG).addOption(QUE);
        options.addOption(PUSHTXT).addOption(QUE).addOption(FILEPATH);
        options.addOption(MOVE).addOption(SOURCEQ).addOption(TARGETQ).addOption(COPY).addOption(NUM).addOption(REGEX);
        options.addOption(QLIST);
        options.addOption(QS);
        return options;
    }
}
