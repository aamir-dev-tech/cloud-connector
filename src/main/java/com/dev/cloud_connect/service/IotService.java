package com.dev.cloud_connect.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.iot.IotClient;
import software.amazon.awssdk.services.iot.model.*;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class IotService {

    private static final Logger log = LoggerFactory.getLogger(IotService.class);
    private IotClient iotClient;


    public String registerDevice(String deviceName, String certificatePem, String privateKeyPem) {

        //Map of attributes
        Map<String, String> attributesMap = new HashMap<>();
        attributesMap.put("state", "new");

        // Register the certificate
        RegisterCertificateRequest registerCertificateRequest = RegisterCertificateRequest.builder()
                .certificatePem(certificatePem)
                .caCertificatePem(privateKeyPem)
                .status(CertificateStatus.ACTIVE)
                .build();
        RegisterCertificateResponse registerCertificateResponse = iotClient.registerCertificate(registerCertificateRequest);
        String certificateArn = registerCertificateResponse.certificateArn();
        log.debug("Request [{}] : Certificate Registered. ", deviceName);


        // Create a new thing
        CreateThingRequest createThingRequest = CreateThingRequest.builder()
                .thingName(deviceName)
                .attributePayload(m -> m.attributes(attributesMap))
                .build();
        CreateThingResponse createThingResponse = iotClient.createThing(createThingRequest);
        log.debug("Request [{}] : Thing Created. ", deviceName);

        // Attach the policy to certificate
        AttachPolicyRequest attachPolicyRequest = AttachPolicyRequest.builder()
                .policyName("devicePolicy") // Replace with your policy name
                .target(certificateArn)
                .build();
        iotClient.attachPolicy(attachPolicyRequest);
        log.debug("Request [{}] : DevicePolicy Attached to Certificate. ", deviceName);

        // Attach the certificate to thing
        AttachThingPrincipalRequest principalRequest = AttachThingPrincipalRequest.builder()
                .thingName(deviceName)
                .principal(certificateArn)
                .build();
        iotClient.attachThingPrincipal(principalRequest);
        log.debug("Request [{}] : Certificate Attached to Thing. ", deviceName);

        return String.format("Device [%s] registered successfully", createThingResponse.thingName());
    }


    public String getDeviceDetails(String deviceName){

        DescribeThingResponse response = iotClient.describeThing(DescribeThingRequest.builder()
                        .thingName(deviceName)
                .build());
        return response.toString();
    }
}
