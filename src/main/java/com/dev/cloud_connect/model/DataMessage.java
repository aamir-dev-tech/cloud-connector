package com.dev.cloud_connect.model;

import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DataMessage extends AWSIotMessage {


    public DataMessage(String topic, AWSIotQos qos, String payload) {
        super(topic, qos, payload);
    }

    @Override
    public void onSuccess() {
        // called when message publishing succeeded
        log.info("Message sent successfully!");
    }

    @Override
    public void onFailure() {
        // called when message publishing failed
        log.info("Message sent failed!");
    }

    @Override
    public void onTimeout() {
        // called when message publishing timed out
        log.info("Message sent timeout!");
    }
}
