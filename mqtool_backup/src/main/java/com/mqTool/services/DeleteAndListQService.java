package com.mqTool.services;

import com.mqTool.pojos.QueueObject;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class DeleteAndListQService {
    ReadProperties service = new ReadProperties();
    /**
     *
     * @return the list of queues's name with the number of messages in each queue
     * @throws IOException
     * @throws JSONException
     * @throws URISyntaxException
     * @throws InterruptedException
     */
    public ResponseEntity<List> getQueuesWithExchangeAndMessageNumber() throws IOException, InterruptedException, URISyntaxException, JSONException {
        HttpClient client = HttpClient.newBuilder()
                .authenticator(new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        try {
                            return new PasswordAuthentication(service.getUsername(), service.getPassword().toCharArray());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://"+ service.getHost() + ":" + service.getApiPort() + "/api/queues"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JSONArray queues = new JSONArray(response.body());
        List<QueueObject> queueNames=new ArrayList<>();
        for( int i=0;i<queues.length();i++){
            queueNames.add(new QueueObject(queues.getJSONObject(i).getString("name"),
                    queues.getJSONObject(i).getString("messages_ready"),
                    "exchange"));
        }
        return ResponseEntity.ok(queueNames);
    }

    /**
     *
     * @param queueNamesToDelete
     * @return deleted queues
     * @throws IOException
     *
     * command example:    mqtool delete -more -q  queue1 queue2 queue3 ... queuen    -> will delete queue1 ... queuen
     */

    public  String deleteSeveralQueues (List <String> queueNamesToDelete) throws IOException {
        try {
            for (String queueName : queueNamesToDelete) {
                // Supprimer la file d'attente
                service.tryConnection().queueDelete(queueName);
                System.out.println("queues deleted : " + queueName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return queueNamesToDelete + "   deleted!";
    }

    /**
     *
     * @param queueNamesToDelete
     * @return deleted queue
     * @throws IOException
     *
     * command example:    mqtool delete -one -q  queue1    -> will delete queue1
     */
    public String deleteOneQueue (String queueNamesToDelete) throws IOException {
        try {
            // Supprimer la file d'attente
            service.tryConnection().queueDelete(queueNamesToDelete);
            System.out.println("the queue was deleted : " + queueNamesToDelete);
        } catch (Exception e) {
            e.printStackTrace();
        }return "queue "+ queueNamesToDelete + " deleted!";
    }
}
