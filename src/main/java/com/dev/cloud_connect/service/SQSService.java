package com.dev.cloud_connect.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SQSService {

    private final AmazonSQS sqs;

    @Value("${aws.sqs.cloud-to-device.queue}")
    private String cloudToDeviceQueue;

    public SQSService() {
        this.sqs = AmazonSQSClientBuilder.standard().build();
    }

    public void sendMessage(String message) {
        SendMessageRequest sendMessageRequest = new SendMessageRequest()
                .withQueueUrl(cloudToDeviceQueue)
                .withMessageBody(message);

        sqs.sendMessage(sendMessageRequest);
    }
}
