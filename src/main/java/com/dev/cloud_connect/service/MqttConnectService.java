package com.dev.cloud_connect.service;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.auth.Credentials;
import com.amazonaws.services.iot.client.auth.StaticCredentialsProvider;
import com.dev.cloud_connect.model.DataMessage;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MqttConnectService {


    @Value("${aws.mqtt.topic}")
    private String mqttTopicName;

    @Value("${aws.client.endpoint}")
    private String clientEndpoint;

    @Value("${aws.region}")
    private String region;

    public String connect(String clientId) throws AWSIotException {
        Credentials credentials = new Credentials(System.getenv("aws_access_key_id"), System.getenv("aws_secret_access_key"));
        AWSIotMqttClient client = new AWSIotMqttClient(clientEndpoint, clientId, new StaticCredentialsProvider(credentials), region);

        client.connect();
        return "Connection Success";
    }

    public String disconnect(String clientId) throws AWSIotException {
        Credentials credentials = new Credentials(System.getenv("aws_access_key_id"), System.getenv("aws_secret_access_key"));
        AWSIotMqttClient client = new AWSIotMqttClient(clientEndpoint, clientId, new StaticCredentialsProvider(credentials), region);

        client.disconnect();
        return "Connection Terminated";
    }

    public String publishMessage(String deviceId , String payload) throws AWSIotException {
        AWSIotQos qos = AWSIotQos.QOS0;
        long timeout = 3000;

        DataMessage message = new DataMessage(mqttTopicName, qos, payload);

        Credentials credentials = new Credentials(System.getenv("aws_access_key_id"), System.getenv("aws_secret_access_key"));
        AWSIotMqttClient client = new AWSIotMqttClient(clientEndpoint, deviceId, new StaticCredentialsProvider(credentials), region);

        client.connect();
        client.publish(message, timeout);
        return "Message Published";
    }


    public String receiveMessage(String deviceId) {

        try(MqttClient mqttClient = new MqttClient("ws://"+clientEndpoint, deviceId)){
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    log.info("Connection Lost");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    log.info("Message arrived from topic " + topic + ": " + new String(message.getPayload()));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                    // No need to handle delivery complete in a subscriber
                }
            });

            mqttClient.connect();
            mqttClient.subscribe(mqttTopicName);

            return "Message Received";
        }catch (Exception e){
            log.error("Exception in receiving message : {}", e.getMessage());
            return "Message Receiving Failed";
        }

    }



}
