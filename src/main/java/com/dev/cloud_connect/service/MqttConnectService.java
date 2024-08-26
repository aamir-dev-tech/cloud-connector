package com.dev.cloud_connect.service;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.auth.Credentials;
import com.amazonaws.services.iot.client.auth.StaticCredentialsProvider;
import com.dev.cloud_connect.model.DataMessage;
import org.springframework.stereotype.Service;

@Service
public class MqttConnectService {

    private static final String CLIENT_ENDPOINT = "a1hzus1y3s3x5-ats.iot.ap-south-1.amazonaws.com";
    private static final String REGION = "ap-south-1";

    public String connect(String clientId) throws AWSIotException {
        Credentials credentials = new Credentials(System.getenv("aws_access_key_id"), System.getenv("aws_secret_access_key"));
        AWSIotMqttClient client = new AWSIotMqttClient(CLIENT_ENDPOINT, clientId, new StaticCredentialsProvider(credentials), REGION);

        client.connect();
        return "Connection Success";
    }

    public String disconnect(String clientId) throws AWSIotException {
        Credentials credentials = new Credentials(System.getenv("aws_access_key_id"), System.getenv("aws_secret_access_key"));
        AWSIotMqttClient client = new AWSIotMqttClient(CLIENT_ENDPOINT, clientId, new StaticCredentialsProvider(credentials), REGION);

        client.disconnect();
        return "Connection Terminated";
    }

    public String publishMessage(String deviceId , String payload) throws AWSIotException {
        String topic = "data_topic";
        AWSIotQos qos = AWSIotQos.QOS0;
        long timeout = 3000;

        DataMessage message = new DataMessage(topic, qos, payload);

        Credentials credentials = new Credentials(System.getenv("aws_access_key_id"), System.getenv("aws_secret_access_key"));
        AWSIotMqttClient client = new AWSIotMqttClient(CLIENT_ENDPOINT, deviceId, new StaticCredentialsProvider(credentials), REGION);

        client.connect();
        client.publish(message, timeout);
        return "Message Published";
    }
}
