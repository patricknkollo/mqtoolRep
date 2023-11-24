package com.mqTool.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mqTool.core.jwt.JwtUtil;
import com.rabbitmq.client.*;
import com.mqTool.core.pojo.Queue;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class RabbitMQService {
    private final Logger LOGGER = LoggerFactory.getLogger(RabbitMQService.class);
    @Autowired
    RabbitProperties rabbitProperties;
    @Autowired
    ConnectionService connectionService;

    private static final String Authorization_header = "Authorization";

    public String publishMessageOnQueue(String message, String queueName) {

        Connection connection = connectionService.tryConnection();
        try (Channel channel = connection.createChannel()) {
            channel.queueDeclare(queueName, true, false, false, null);
            publishMessageToQueue(channel, message, queueName, null, connectionService.factory.getUsername());
            LOGGER.info("Message published to queue : {}", message);
            return "Message published to queue : " + message;

        } catch (IOException | TimeoutException e) {
            LOGGER.error("Publishing the message to the queue failed: %s", e);
            return "Publishing the message to the queue failed: " + e;
        }
    }

    public String createQueue(String queueName) {
        Connection connection = connectionService.tryConnection();
        try (Channel channel = connection.createChannel()) {
            channel.queueDeclare(queueName, true, false, false, null);
            LOGGER.info("Queue {} created on the rabbitMQ server", queueName);
            return "new queue created !";
        } catch (IOException | TimeoutException e) {
            LOGGER.error("Creating a queue failed: %s", e);
            return "Creating a queue failed: " + e;

        }
    }

    public String movingFromSrcQueueToTargetQueueWithRegex(String srcQueue, String targetQueue, boolean copy, long number, String regex) throws IOException, TimeoutException, JSONException {

        if (!checkIfQueueExists(srcQueue)) {
            return "The queue " + srcQueue + " does not exist";
        }

        if (!checkIfQueueExists(targetQueue)) {
            return "The queue " + targetQueue + " does not exist";
        }

        if( srcQueue.equals(targetQueue)){
            return  "Please make sure that the source and target queues are different";
        }

        if( number == 0 ) {
            return "Please enter a valid number greater than 0";
        }
        try {
            Connection connection = connectionService.tryConnection();
            Channel channel = connection.createChannel();
            long totalMessages = channel.messageCount(srcQueue);
            if(totalMessages==0){
                return "No Messages on source queue";
            }
            if (number > totalMessages) {
                return "Please enter a valid number <=" + totalMessages;
            }

            if (number == -1) {
                number = totalMessages;
            }

            List<GetResponse> responses = new ArrayList<>();
            int i = 0;
            while (i < number) {
                GetResponse response = channel.basicGet(srcQueue, !copy);
                if (response != null && matchesRegex(regex, new String(response.getBody()))) {
                    responses.add(response);
                    i++;
                } else if (i + 1 == number) {
                    break;
                }
            }


            if ( responses.size() == 0) {
                return "No message has been found and can be moved.";
            }
            if (number >  responses.size() ) {
                return "Only " +  responses.size()  + " message(s) have been found." + number + " message(s) cannot be moved. Please  enter a number <=" +  responses.size() ;
            }

            for (GetResponse response :  responses) {
                channel.queueDeclare(targetQueue, true, false, false, null);
                channel.basicPublish("", targetQueue, response.getProps(), response.getBody());
                LOGGER.info("Message copied into the queue {} :  ->  {}", targetQueue, new String(response.getBody(), "UTF-8"));
                if (copy == false) {
                    channel.queueDeclare(srcQueue, true, false, false, null);
                    LOGGER.info("Message removed from the queue {} :  ->  {}", srcQueue, new String(response.getBody(), "UTF-8"));
                }
            }
            channel.close();
            return "Message(s) have been moved";
        }catch (Error e){
            LOGGER.error(e.getLocalizedMessage());
            return "Failed moving messages";}
    }

    public boolean checkIfQueueExists(String queue) throws JSONException {
        List<String> queueList = getAllQueueNames();
        return queueList.contains(queue);
    }

    public List<String> getAllQueueNames() throws JSONException {
        List<String> queueNames = new ArrayList<>();
        HttpClient client = HttpClient.newBuilder()
                .authenticator(new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(rabbitProperties.getUsername(),
                                rabbitProperties.getPassword().toCharArray());
                    }
                }).build();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://" + rabbitProperties.getHost() + ":" + rabbitProperties.getApiPort() + "/api/queues"))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONArray queues = new JSONArray(response.body());

            for (int i = 0; i < queues.length(); i++) {
                queueNames.add(queues.getJSONObject(i).getString("name"));
            }
            return queueNames;
        } catch (IOException | URISyntaxException e) {
            LOGGER.error("Cant get queue names, bad request", e);
            return Collections.singletonList("Error getting queue names");
        } catch (InterruptedException e) {
            LOGGER.error("Thread was interrupted", e);
            Thread.currentThread().interrupt();
            return Collections.singletonList("Thread was interrupted");
        }


    }

    public String pullFromParticularQueue(String queueName, String fileName, Long numberOfMessages, boolean copy) throws JSONException, IOException, URISyntaxException, InterruptedException {
        if (!checkIfQueueExists(queueName)) {
            LOGGER.info("The queue " + queueName + " does not exist");
            return "The queue " + queueName + " does not exist";
        }
        Connection connection = connectionService.tryConnection();
        try (Channel channel = connection.createChannel()) {
            long messagesCount = channel.messageCount(queueName);
            if(numberOfMessages == null) {
                numberOfMessages = messagesCount;
            }
            System.out.println("Queue '" + queueName + "' has " + messagesCount + " messages.");

            if (numberOfMessages > messagesCount) {
                return "Please enter a valid number <= " + messagesCount;
            }

            int i = 1;
            while (true) {
                boolean autoAck = !copy;
                GetResponse response = channel.basicGet(queueName, autoAck);
                if (response == null) {
                    if (i > numberOfMessages && numberOfMessages > 0) {
                        break;
                    }
                    // synchronized is used to lock the thread, so that only one thread accesses the wait function
                    synchronized (this) {
                        try {
                            wait(2000);
                            continue;
                        } catch (InterruptedException e) {
                            // if the wait is interrupted manually this error is thrown.
                            LOGGER.error("connection intercepted: " + e.getLocalizedMessage());
                        }
                    }
                } else {
                    String payloadMessage = new String(response.getBody(), StandardCharsets.UTF_8);

                    if (response.getProps() == null) {
                        return null;
                    }
                    Map<String, Object> headers = response.getProps().getHeaders();
                    Map<String,String> mappedHeaders = headers.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> String.valueOf(e.getValue())));

                    JSONObject json = new JSONObject();
                    json.put("payload", payloadMessage);
                    json.put("headers", new JSONObject(mappedHeaders));
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

                    String givenFileName = null;
                    if(fileName == null || fileName.isEmpty()) {
                        givenFileName = path + queueName+ "_" + LocalDate.now() + "_msgs.txt";
                    } else {
                        givenFileName = path + fileName + "_" + queueName + "_msgs.txt";
                    }

                    File directory = new File(path);
                    if (!directory.exists()) {
                        directory.mkdir();
                    }
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(givenFileName, true));
                    bos.write(message.getBytes());
                    bos.write("\n".getBytes());
                    bos.flush();
                    bos.close();
                    i++;
                }
                if (i > numberOfMessages && numberOfMessages > 0) {
                    break;
                }
            }
            return "Messages pull successful.";
        } catch (IOException | TimeoutException e) {
            LOGGER.error("Error when consuming from the queue ", e);
            return "Error when consuming from the queue";
        }
    }

    public String pushDownloadedMessagesBackToQueue(String queueName, String filePath) throws IOException,
            TimeoutException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        return pushMessageFromReader(queueName, reader);
    }

    public String pushMessagesBackToQueue(String queueName, MultipartFile multipartFile) throws IOException,
            TimeoutException {
        InputStream inputStream = multipartFile.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        return pushMessageFromReader(queueName, reader);
    }

    private String pushMessageFromReader(String queueName, BufferedReader reader) throws IOException, TimeoutException {
        Connection connection = connectionService.tryConnection();
        try (Channel channel = connection.createChannel()) {
            String message = reader.readLine();
            if (message == null) {
                throw new IllegalArgumentException("File provided is empty");
            } else {
                while (message != null) {
                    JSONObject jsonObject = new JSONObject(message);
                    String payload = jsonObject.getString("payload");
                    AMQP.BasicProperties.Builder basicProperties = extractProperties(jsonObject);

                    channel.queueDeclare(queueName, true, false, false, null);
                    AMQP.BasicProperties basicPropertiesBuild = basicProperties.build();
                    channel.basicPublish("", queueName, basicPropertiesBuild, payload.getBytes(StandardCharsets.UTF_8));
                    message = reader.readLine();
                }
                return "Message(s) published!";
            }
        } catch (IOException | JSONException e) {
            LOGGER.info(e.getLocalizedMessage());
            return "Message(s) publish failed!";
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private AMQP.BasicProperties.Builder extractProperties(JSONObject jsonObject) throws JSONException, IOException,
            InvalidKeySpecException, NoSuchAlgorithmException {
        Object headers = jsonObject.getJSONObject("headers");
        String head = headers.toString();
        HashMap headerMapping = new ObjectMapper().readValue(head, HashMap.class);

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
            try {
                basicProperties.timestamp(new SimpleDateFormat("dd/MM/yyyy").parse(jsonObject.getString("timestamp")));
            } catch (ParseException e) {
                LOGGER.warn(jsonObject.getString("timestamp") + " can not be parsed as a Date");
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
        return basicProperties;
    }

    public List<Queue> getQueuesWithExchangeAndMessageNumber()  {
        List<Queue> queueList = new ArrayList<>();
        try {
            HttpResponse<String> response = getResponse();

            JSONArray jsonQueues = new JSONArray(response.body());
            for (int i = 0; i < jsonQueues.length(); i++) {
                queueList.add(new Queue(jsonQueues.getJSONObject(i).getString("name"),
                        String.valueOf(jsonQueues.getJSONObject(i).getInt("messages")),
                        "exchange"));
            }
        } catch (JSONException e) {
            LOGGER.error("Bad JSON", e);
        }
        catch (IOException| URISyntaxException | InterruptedException e) {
            LOGGER.error("Bad response", e);
        }finally {
            return queueList;
        }
    }

    public String deleteSeveralQueues(List<String> queueNamesToDelete)  {
        Connection connection = connectionService.tryConnection();
        try (Channel channel = connection.createChannel()) {
            for (String queueName : queueNamesToDelete) {
                channel.queueDelete(queueName);
                System.out.println("queues deleted : " + queueName);
            }
        } catch (IOException | TimeoutException e) {
            LOGGER.error("Deletion of Queues failed" ,e);
            return "Deletion of Queues failed";
        }
        return queueNamesToDelete + "   deleted!";
    }

    public String deleteOneQueue(String queueNamesToDelete)  {

        Connection connection = connectionService.tryConnection();
        try (Channel channel = connection.createChannel()) {
            channel.queueDelete(queueNamesToDelete);
            System.out.println("the queue was deleted : " + queueNamesToDelete);
        } catch (IOException | TimeoutException e) {
            LOGGER.error("Deletion of Queue failed" ,e);
            return "Deletion of Queue failed";
        }
        return "queue " + queueNamesToDelete + " deleted!";
    }

    private static boolean matchesRegex(String regex, String message) {
        Pattern pattern = Pattern.compile(".*" + regex + ".*");
        Matcher matcher = pattern.matcher(message);
        return matcher.matches();
    }

    private void publishMessageToQueue(Channel channel, String message, String queueName, String token,
                                       String clientProvidedName) {
        try {
            Map<String, Object> headers = new HashMap<>();
            headers.put(Authorization_header, JwtUtil.getOrCreateAccessToken(token, clientProvidedName));
            AMQP.BasicProperties.Builder basicProperties = new AMQP.BasicProperties.Builder();
            basicProperties.headers(headers);
            channel.basicPublish("", queueName, basicProperties.build(), message.getBytes());
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            LOGGER.error("Error when creating the access token " + e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            LOGGER.error("Publishing the message to the queue failed" + e);
            throw new RuntimeException(e);
        }
    }

    private HttpResponse<String> getResponse() throws IOException, InterruptedException, URISyntaxException {

        HttpClient client = HttpClient.newBuilder()
                .authenticator(new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(rabbitProperties.getUsername(), rabbitProperties.getPassword().toCharArray());

                    }
                }).build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://" + rabbitProperties.getHost() + ":" + rabbitProperties.getApiPort() + "/api/queues"))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }


}