package com.dev.cloud_connect.service;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.Map;

@Service
@Slf4j
public class MQTTService {

    private static final String BROKER_URL = "ssl://a1hzus1y3s3x5-ats.iot.ap-south-1.amazonaws.com:8883";
    private static final String CLIENT_ID = "client_dev_aamir_device";
    private static final String TOPIC = "your/topic";

    private MqttClient client;

    public void connect() throws Exception {
        try{
            client = new MqttClient(BROKER_URL, CLIENT_ID);
            MqttConnectOptions connOpts = new MqttConnectOptions();

            // Set up the certificates
            connOpts.setSocketFactory(createSslSocketFactory());

            connOpts.setCleanSession(true);
            connOpts.setKeepAliveInterval(30);
            client.connect(connOpts);
        } catch (Exception e){
            log.error("Exception in connection : {}", e.getCause().getMessage());
            throw e;
        }

    }

    private javax.net.ssl.SSLSocketFactory createSslSocketFactory() throws Exception {
        // Load certificates and keys
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        FileInputStream keyStoreInput = new FileInputStream("dev-aamir-device-combined.p12");
        keyStore.load(keyStoreInput, "".toCharArray());

        javax.net.ssl.KeyManagerFactory kmf = javax.net.ssl.KeyManagerFactory.getInstance(javax.net.ssl.KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, "".toCharArray());

        javax.net.ssl.TrustManagerFactory tmf = javax.net.ssl.TrustManagerFactory.getInstance(javax.net.ssl.TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);

        javax.net.ssl.SSLContext sslContext = javax.net.ssl.SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new java.security.SecureRandom());

        return sslContext.getSocketFactory();
    }

    public void updateThingAttribute(String attributeName, String attributeValue) throws MqttException {
        String payload = String.format("{\"%s\":\"%s\"}", attributeName, attributeValue);
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(1);
        client.publish(TOPIC, message);
    }
}
