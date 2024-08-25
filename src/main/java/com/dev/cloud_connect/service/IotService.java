package com.dev.cloud_connect.service;

import com.dev.cloud_connect.model.DeviceDetails;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.iot.IotClient;
import software.amazon.awssdk.services.iot.model.*;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
@Slf4j
public class IotService {

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


    public DeviceDetails getDeviceDetails(String deviceName){

        DescribeThingResponse response = iotClient.describeThing(DescribeThingRequest.builder()
                        .thingName(deviceName)
                .build());

        DescribeEndpointResponse endpointResponse = iotClient.describeEndpoint(DescribeEndpointRequest.builder().endpointType("iot:Data-ATS").build());



        DeviceDetails deviceDetails = DeviceDetails.builder()
                .deviceId(response.thingId())
                .deviceName(response.thingName())
                .deviceRegion(response.thingArn().split(":")[3])
                .deviceAttributes(response.attributes())
                .build();

        if (endpointResponse != null) {
            String endpointUrl = endpointResponse.endpointAddress();
            String exString = getValue(endpointUrl);
            String fullEndpoint = "https://" + exString + ".iot.ap-south-1.amazonaws.com";
            deviceDetails.setEndpointURL(fullEndpoint);
        }

       return deviceDetails;
    }

    private static String getValue(String input) {
        log.debug("Endpoint input : {}", input);
        // Define a regular expression pattern for extracting the subdomain.
        Pattern pattern = Pattern.compile("^(.*?)\\.iot\\.ap-south-1\\.amazonaws\\.com");

        // Match the pattern against the input string.
        Matcher matcher = pattern.matcher(input);

        // Check if a match is found.
        if (matcher.find()) {
            // Extract the subdomain from the first capturing group.
            String subdomain = matcher.group(1);
            log.debug("Extracted subdomain: {}", subdomain);
            return subdomain ;
        } else {
            log.debug("No match found");
        }
        return "" ;
    }
}
