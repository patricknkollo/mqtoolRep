package com.mqTool.web.controller;

import com.mqTool.core.service.RabbitMQService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/")
@Validated
public class QueueController {

    @Autowired
    RabbitMQService rabbitMQService;

    private final Logger LOGGER = LoggerFactory.getLogger(QueueController.class);

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/list")
    public ResponseEntity<List<String>> getAllQueueNames() throws JSONException {
        return ResponseEntity.ok(rabbitMQService.getAllQueueNames());
    }

    @GetMapping("/listdetails")
    public ResponseEntity<List> getQueuesWithExchangeAndMessageNumber()  {
        return ResponseEntity.ok(rabbitMQService.getQueuesWithExchangeAndMessageNumber());
    }
    @GetMapping("/checkIfQueueExists")
    public ResponseEntity<Boolean> checkIfQueueExists( @RequestParam @NotBlank String queue) throws JSONException {
        return ResponseEntity.ok(rabbitMQService.checkIfQueueExists(queue));
    }

    @PostMapping("/createQueue")
    public ResponseEntity<String> createQueue(@RequestParam @NotBlank String queueName)  {
        return ResponseEntity.ok(rabbitMQService.createQueue(queueName));
    }
    @PostMapping("/deleteQueues")
    public ResponseEntity<String> deleteSeveralQueues(@RequestParam  @NotEmpty List <String> queueNamesToDelete) {
        return ResponseEntity.ok(rabbitMQService.deleteSeveralQueues(queueNamesToDelete));
    }
    @PostMapping("/deleteQueue")
    public ResponseEntity<String> deleteOneQueue(@RequestParam  @NotBlank String queueNamesToDelete)  {
        return ResponseEntity.ok(rabbitMQService.deleteOneQueue(queueNamesToDelete));
    }

    @PostMapping("/moveQueue")
    public ResponseEntity<String> movingFromSrcQueueToTargetQueueWithRegex(@RequestParam @NotBlank String srcQueue, @NotBlank String targetQueue, boolean copy, @Min(1)long  number, @NotBlank String regex) throws JSONException, IOException,TimeoutException {
        return ResponseEntity.ok(rabbitMQService.movingFromSrcQueueToTargetQueueWithRegex(srcQueue, targetQueue, copy, number, regex));
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