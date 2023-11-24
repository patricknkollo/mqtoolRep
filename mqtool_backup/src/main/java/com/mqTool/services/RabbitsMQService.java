package com.mqTool.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mqTool.jwt.JwtUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RabbitsMQService {

    private final Logger logger = LoggerFactory.getLogger(RabbitsMQService.class);
    ReadProperties rbmqConnectionservice = new ReadProperties();
    private static final String Authorization_header = "Authorization";

    public String publishMessageOnQueue(String message, String queueName) {
        try {
            Channel channel = rbmqConnectionservice.tryConnection();
            channel.queueDeclare(queueName, true, false, false, null);
            publishMessageToQueue(channel, message, queueName, null, rbmqConnectionservice.factory.getUsername());
            logger.info("Message published to queue : {}", message);
            return "message published!";
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            logger.error("Error when creating the access token " + e.getLocalizedMessage());
            throw new RuntimeException(e);
        } catch (IOException | TimeoutException e) {
            logger.error("Publishing the message to the queue failed " + e.getLocalizedMessage());
            return "Message publish failed";
        }
    }

    public String createQueue(String queueName) {
        try {
            rbmqConnectionservice.tryConnection().queueDeclare(queueName, true, false, false, null);
            logger.info("Queue {} created on the rabbitMQ server", queueName);
            return "new queue created !";
        } catch (IOException | TimeoutException e) {
            logger.error("Creating a queue failed " + e.getLocalizedMessage());
            return "Creating a new queue failed";
        }
    }

    public String movingFromSrcQueueToTargetQueueWithRegex( String srcQueue, String targetQueue, boolean copy,
                                                            long number, String regex) throws IOException,
            TimeoutException, JSONException, URISyntaxException, InterruptedException {

        if( !checkIfQueueExists(srcQueue)){
            logger.error("The queue doesn't exist", srcQueue);
            return "The queue "+srcQueue+ " does not exist";
        }

        if (!checkIfQueueExists(targetQueue)) {
            logger.error("The queue doesn't exist", targetQueue);
            return "The queue "+targetQueue+ " does not exist";
        }

        if( srcQueue.equals(targetQueue)){
            logger.warn("Source and target queues are the same");
            return  "Please make sure that the source and target queues are different";
        }

        if( number == 0 ) {
            logger.warn("Number of messages specified is 0");
            return "Please enter a valid number greater than 0";
        }

        Channel channel = rbmqConnectionservice.tryConnection();
        long totalMessages = channel.messageCount(srcQueue);
        if( number > totalMessages) {
            logger.error("Number of messages to be moved exceeds amount of messages in the source queue");
            return "Please enter a valid number <="+ totalMessages;
        }

        List<GetResponse> responses = getResponsesFromQueueWithRegex(channel,srcQueue, regex, number, copy,totalMessages);
        if (responses == null || responses.isEmpty()) {
            logger.info("No messages found that match criteria");
            return "No message has been found and can be moved.";
        }
        if (number > responses.size()) {
            logger.warn("Number of messages specified to move is bigger than total messages found");
            return "Only " + responses.size() + " message(s) have been found." + number + " message(s) cannot be moved. Please  enter a number <=" + responses.size();
        }

        for (GetResponse response : responses) {
           channel.queueDeclare(targetQueue, true, false, false, null);
           channel.basicPublish("", targetQueue, (AMQP.BasicProperties) response.getProps(), response.getBody());
            logger.info("Message copied into the queue {} :  ->  {}", targetQueue, new String(response.getBody(),"UTF-8") );
            if (copy == false) {
                channel.queueDeclare(srcQueue, true, false, false, null);
                logger.info("Message removed from the queue {} :  ->  {}", srcQueue, new String(response.getBody(),"UTF-8"));
            }
        }
        return responses.size() + " message(s) have been moved";
    }

    public List<GetResponse> getResponsesFromQueueWithRegex(Channel channel,String queue, String regex, long number, boolean copy, long totalMessages) throws IOException {
        List<GetResponse> responses = new ArrayList<>();
        if(number == -1){
            number = totalMessages;
        }
        int i= 0;
        while(i <number) {
            GetResponse response = channel.basicGet(queue, !copy);
            if (response != null &&  matchesRegex( regex, new String(response.getBody()))) {
                responses.add(response);
                i++;
            } else if( i+1 == number) {
                return responses;
            }
        }
        return responses;
    }

    public boolean checkIfQueueExists(String queue) throws IOException, JSONException, URISyntaxException, InterruptedException {
         List queueList = getAllQueueNames().getBody();
         return queueList.contains(queue);
    }

    public ResponseEntity<List> getAllQueueNames() throws IOException, InterruptedException, URISyntaxException, JSONException {
        HttpClient client = HttpClient.newBuilder()
                .authenticator(new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        try {
                            return new PasswordAuthentication(rbmqConnectionservice.getUsername(),
                                    rbmqConnectionservice.getPassword().toCharArray());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://" + rbmqConnectionservice.getHost() + ":" + rbmqConnectionservice.getApiPort() + "/api/queues"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JSONArray queues = new JSONArray(response.body());
        List<String> queueNames=new ArrayList<>();
        for( int i=0;i<queues.length();i++){
            queueNames.add(queues.getJSONObject(i).getString("name"));
        }
        return ResponseEntity.ok(queueNames);
    }

    public String pullFromParticularQueue (String queueName, int numberOfMessages, boolean copy) throws JSONException, IOException, URISyntaxException, InterruptedException {
        if( !checkIfQueueExists(queueName)){
            logger.info("The queue "+ queueName + " does not exist");
            return "The queue "+ queueName + " does not exist";
        }
        try {
            Channel channel = rbmqConnectionservice.tryConnection();
            long messagesCount = channel.messageCount(queueName);
            System.out.println("Queue '" + queueName + "' has " + messagesCount + " messages.");

            if( numberOfMessages > messagesCount) {
                return "Please enter a valid number <= " + messagesCount;
            }

            int i = 1;
            LocalDateTime startTime = LocalDateTime.now();
            while (true) {
                boolean autoAck = !copy;
                GetResponse response = channel.basicGet(queueName, autoAck);
                if (response == null) {
                    wait(2000);
                    continue;
                } else {
                    String payloadMessage = new String(response.getBody(), StandardCharsets. UTF_8);

                    if (response.getProps() == null ) {
                        return null;
                    }
                    String headers = response.getProps().getHeaders().toString();

                    JSONObject json = new JSONObject();
                    json.put("payload", payloadMessage);
                    json.put("headers", new JSONObject(headers));
                    json.put("content_type", response.getProps().getContentType());
                    json.put("message_id",response.getProps().getMessageId());
                    json.put("priority",response.getProps().getPriority());
                    json.put("content_encoding",response.getProps().getContentEncoding());
                    json.put("delivery_mode",response.getProps().getDeliveryMode());
                    json.put("correlation_id",response.getProps().getCorrelationId());
                    json.put("reply_to",response.getProps().getReplyTo());
                    json.put("expiration",response.getProps().getExpiration());
                    json.put("timestamp",response.getProps().getTimestamp());
                    json.put("type",response.getProps().getType());
                    json.put("user_id",response.getProps().getUserId());
                    json.put("app_id",response.getProps().getAppId());
                    json.put("cluster_id",response.getProps().getClusterId());

                    String message = String.valueOf(json);

                    String path = "downloadedMessages/";
                    String fileName = path + "msg" + i + ".txt";
                    File directory = new File(path);
                    if (!directory.exists()) {
                        directory.mkdir();
                    }
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileName, true));
                    bos.write(message.getBytes());
                    bos.flush();
                    bos.close();
                    i++;
                }
                if (ChronoUnit.MINUTES.between(startTime, LocalDateTime.now()) >= 2 || (i > numberOfMessages && numberOfMessages > 0)) {
                    rbmqConnectionservice.closeConnection();
                    break;
                }
            }
        } catch (IOException | TimeoutException | InterruptedException e) {
            logger.error("Error when consuming from the queue " + e.getLocalizedMessage());
        } catch (IllegalMonitorStateException e) {
            //TODO error handling for 'current thread is not owner' needs to be researched
            logger.info(e.getLocalizedMessage());
        }
        return  "Messages pull successful";
        //TODO connection closing needs to be researched
    }

    public String pushDownloadedMessagesBackToQueue (String queueName, String filePath) throws IOException, TimeoutException {
        Channel channel = rbmqConnectionservice.tryConnection();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(filePath));
            String message = reader.readLine();
            while (message != null) {
                JSONObject jsonObject = new JSONObject(message);
                String payload = jsonObject.getString("payload");
                String headers = jsonObject.getString("headers");
                HashMap headerMapping = new ObjectMapper().readValue(headers, HashMap.class);

                String currentJWT = (String) headerMapping.get("Authorization");
                headerMapping.put("Authorization", JwtUtil.getOrCreateAccessToken(currentJWT, null));

                AMQP.BasicProperties.Builder basicProperties = new AMQP.BasicProperties.Builder();
                basicProperties.headers(headerMapping);

                if (jsonObject.isNull("content_type")) {
                    basicProperties.contentType(null);
                } else {
                    basicProperties.contentType(jsonObject.getString("content_type"));
                }
                if (jsonObject.isNull("message_id")) {
                    basicProperties.messageId(null);
                } else {
                    basicProperties.messageId(jsonObject.getString("message_id"));
                }
                if (jsonObject.isNull("priority")) {
                    basicProperties.priority(null);
                } else {
                    basicProperties.priority(jsonObject.getInt("priority"));
                }
                if (jsonObject.isNull("content_encoding")) {
                    basicProperties.contentEncoding(null);
                } else {
                    basicProperties.contentEncoding(jsonObject.getString("content_encoding"));
                }
                if (jsonObject.isNull("delivery_mode")) {
                    basicProperties.deliveryMode(null);
                } else {
                    basicProperties.deliveryMode(jsonObject.getInt("delivery_mode"));
                }
                if (jsonObject.isNull("correlation_id")) {
                    basicProperties.correlationId(null);
                } else {
                    basicProperties.correlationId(jsonObject.getString("correlation_id"));
                }
                if (jsonObject.isNull("reply_to")) {
                    basicProperties.replyTo(null);
                } else {
                    basicProperties.replyTo(jsonObject.getString("reply_to"));
                }
                if (jsonObject.isNull("expiration")) {
                    basicProperties.expiration(null);
                } else {
                    basicProperties.expiration(jsonObject.getString("expiration"));
                }
                if (jsonObject.isNull("timestamp")) {
                    basicProperties.timestamp(null);
                } else {
                    try{
                        basicProperties.timestamp(new SimpleDateFormat("dd/MM/yyyy").parse(jsonObject.getString("timestamp")));
                    } catch (ParseException e) {
                        logger.warn(jsonObject.getString("timestamp") + " can not be parsed as a Date");
                    }
                }
                if (jsonObject.isNull("type")) {
                    basicProperties.type(null);
                } else {
                    basicProperties.type(jsonObject.getString("type"));
                }
                if (jsonObject.isNull("user_id")) {
                    basicProperties.userId(null);
                } else {
                    basicProperties.userId(jsonObject.getString("user_id"));
                }
                if (jsonObject.isNull("app_id")) {
                    basicProperties.appId(null);
                } else {
                    basicProperties.appId(jsonObject.getString("app_id"));
                }
                if (jsonObject.isNull("cluster_id")) {
                    basicProperties.clusterId(null);
                } else {
                    basicProperties.clusterId(jsonObject.getString("cluster_id"));
                }
                channel.queueDeclare(queueName, true, false, false, null);
                AMQP.BasicProperties basicPropertiesBuild = basicProperties.build();
                channel.basicPublish("", queueName, basicPropertiesBuild, payload.getBytes(StandardCharsets.UTF_8));
                message = reader.readLine();
            }
            return "Message(s) published!";
        } catch (IOException | JSONException e) {
            logger.info(e.getLocalizedMessage());
            return "Message(s) publish failed!";
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean matchesRegex(String regex, String message) {
        Pattern pattern = Pattern.compile(".*"+regex+".*");
        Matcher matcher = pattern.matcher(message);
        return  matcher.matches();
    }

    private void publishMessageToQueue(Channel channel, String message, String queueName, String token,
                                       String clientProvidedName) throws IOException, InvalidKeySpecException,
            NoSuchAlgorithmException {
        Map<String, Object> headers = new HashMap<>();
        headers.put(Authorization_header, JwtUtil.getOrCreateAccessToken(token, clientProvidedName));
        AMQP.BasicProperties.Builder basicProperties = new AMQP.BasicProperties.Builder();
        basicProperties.headers(headers);
        channel.basicPublish("", queueName, basicProperties.build(), message.getBytes());
    }
}