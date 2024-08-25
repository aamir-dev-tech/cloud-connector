package com.dev.cloud_connect.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.iot.IotClient;
import software.amazon.awssdk.services.iot.model.CreateKeysAndCertificateRequest;
import software.amazon.awssdk.services.iot.model.CreateKeysAndCertificateResponse;
import software.amazon.awssdk.services.iot.model.CreateThingRequest;
import software.amazon.awssdk.services.iot.model.CreateThingResponse;

@Service
@AllArgsConstructor
public class IotService {

    private IotClient iotClient;


    public String registerDevice(String deviceName) {
        // Create a new thing
        CreateThingRequest createThingRequest = CreateThingRequest.builder()
                .thingName(deviceName)
                .build();
        CreateThingResponse createThingResponse = iotClient.createThing(createThingRequest);

        // Create keys and certificate
        CreateKeysAndCertificateRequest createKeysAndCertificateRequest = CreateKeysAndCertificateRequest.builder()
                .setAsActive(true)
                .build();
        CreateKeysAndCertificateResponse createKeysAndCertificateResponse = iotClient.createKeysAndCertificate(createKeysAndCertificateRequest);

        // Return the certificate ARN or any other relevant information
        String response  = createThingResponse.thingArn() + " " + createKeysAndCertificateResponse.certificateArn();

        return String.format("Device [%s] registered successfully, with details [%s]", deviceName, response);
    }
}
