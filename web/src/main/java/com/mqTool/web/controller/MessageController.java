package com.mqTool.web.controller;

import com.mqTool.core.service.RabbitMQService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/")
@Validated
public class MessageController {

    @Autowired
    RabbitMQService rabbitMQService;

    private final Logger LOGGER = LoggerFactory.getLogger(MessageController.class);

    @PostMapping("/pushMessage")
    public String publishMessageOnQueue(@RequestParam @NotBlank String message, @RequestParam @NotBlank String queueName) {
        return rabbitMQService.publishMessageOnQueue(message, queueName);
    }

    @PostMapping("/pushTextFile")
    public String pushTextFileMessageToQueue(@RequestParam (value = "queuename") String queuename, @RequestParam MultipartFile textfile) throws IOException, TimeoutException {
        return rabbitMQService.pushMessagesBackToQueue(queuename, textfile);
    }

    @PostMapping("/pullMessage")
        public String pullFromParticularQueue(@RequestParam (value = "queuename") String queueName, @RequestParam(value = "filename", required = false) String fileName, @RequestParam(value = "number", required = false) Long numberOfMessages, @RequestParam(value = "copy", required = false) boolean copy) throws JSONException, IOException, URISyntaxException, InterruptedException {
            return rabbitMQService.pullFromParticularQueue(queueName, fileName, numberOfMessages, copy);
        }

    @PostMapping("/moveMessage")
    public String moveMessagesFromSourceToTargetQueue(@RequestParam (value = "source_queuename") String srcQueue, @RequestParam (value = "target_queuename") String targetQueue, @RequestParam(value = "copy", required = false) boolean copy, @RequestParam(value = "number", required = false) long number, String regex) throws JSONException, IOException, URISyntaxException, InterruptedException, TimeoutException {
        return rabbitMQService.movingFromSrcQueueToTargetQueueWithRegex(srcQueue, targetQueue, copy, number, regex);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public String blankParameter(ConstraintViolationException e){
        LOGGER.info("Requestparameter(s) blank",e);
        return "Requestparameter(s) blank";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public String missingParameter(MissingServletRequestParameterException e){
        LOGGER.info("Requestparameter(s) missing",e);
        return "Requestparameter(s) missing";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public String wrongType(MethodArgumentTypeMismatchException e){
        LOGGER.info("Wrong type or missing argument",e);
        return "Wrong type or missing argument";
    }
}