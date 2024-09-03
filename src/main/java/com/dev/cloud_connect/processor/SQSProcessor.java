package com.dev.cloud_connect.processor;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.dev.cloud_connect.service.MqttConnectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SQSProcessor {

    private final AmazonSQS sqs;
    private final MqttConnectService mqttConnectService;

    @Value("${aws.sqs.cloud-to-device.queue}")
    private String cloudToDeviceQueue;

    public SQSProcessor(MqttConnectService mqttConnectService) {
        this.mqttConnectService = mqttConnectService;
        this.sqs = AmazonSQSClientBuilder.standard().build();
    }

    @Scheduled(fixedRate = 5000) // Poll every 5 seconds
    public void processMessages() throws AWSIotException {
        log.debug("Processor Started >>>>");
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest()
                .withQueueUrl(cloudToDeviceQueue)
                .withMaxNumberOfMessages(10);

        ReceiveMessageResult receiveMessageResult = sqs.receiveMessage(receiveMessageRequest);
        List<Message> messages = receiveMessageResult.getMessages();
        log.debug("Messages received : {}", messages);

        for (com.amazonaws.services.sqs.model.Message message : messages) {
            String body = message.getBody();
            mqttConnectService.publishMessage("device-aamir-dev2", body);
            log.debug("Message sent to device");

            // Delete message from SQS after processing
            sqs.deleteMessage(cloudToDeviceQueue, message.getReceiptHandle());
        }
    }
}
