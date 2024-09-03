package com.dev.cloud_connect.controller;

import com.dev.cloud_connect.model.DeviceDetails;
import com.dev.cloud_connect.model.MessageModel;
import com.dev.cloud_connect.service.IotService;
import com.dev.cloud_connect.service.MqttConnectService;
import com.dev.cloud_connect.service.SQSService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.iot.model.ResourceNotFoundException;

import java.util.Map;

@RestController
@RequestMapping("/v1/device")
@Slf4j
@AllArgsConstructor
public class DeviceController {

    private IotService iotService;

    private MqttConnectService mqttConnectService;

    private SQSService sqsService;

    @PostMapping(value = "/register/{deviceName}",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> registerDevice(@PathVariable String deviceName,
                                                @RequestParam("certificate") MultipartFile certificateFile,
                                                @RequestParam("privateKey") MultipartFile privateKeyFile) {
        log.debug("Device Registration Started >>>>");

        try {
            String certificatePem = new String(certificateFile.getBytes());
            String privateKeyPem = new String(privateKeyFile.getBytes());

            String result = iotService.registerDevice(deviceName, certificatePem, privateKeyPem);

            log.debug("Device Registration Completed >>>> {}", result);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Exception while creation of device - ERR0R");
            return ResponseEntity.status(500).body("Error processing files: " + e.getMessage());
        }
    }


    @GetMapping("/details/{deviceName}")
    public ResponseEntity<DeviceDetails> getDeviceDetails(@PathVariable String deviceName){
        try{
            return ResponseEntity.ok(iotService.getDeviceDetails(deviceName));
        }catch (ResourceNotFoundException re){
            log.error("Exception while getting details of device - ERR0R");
            return ResponseEntity.status(404).body(null);
        }
    }

    @GetMapping("/connect/{deviceName}")
    public ResponseEntity<String> connectDevice(@PathVariable String deviceName) {
        try{
            String status = mqttConnectService.connect(deviceName);
            return ResponseEntity.status(200).body("Device "+deviceName+" connected to AWS Cloud. "+status);
        }catch (Exception e){
            log.error("Exception while getting connected - ERR0R");
            return ResponseEntity.status(500).body("Device "+deviceName+" failed to connect to AWS Cloud.");
        }
    }


    @GetMapping("/disconnect/{deviceName}")
    public ResponseEntity<String> disconnectDevice(@PathVariable String deviceName) {
        try{
            String status = mqttConnectService.disconnect(deviceName);
            return ResponseEntity.status(200).body("Device "+deviceName+" disconnected from AWS Cloud. "+status);
        }catch (Exception e){
            log.error("Exception while getting disconnected - ERR0R");
            return ResponseEntity.status(500).body("Device "+deviceName+" failed to disconnect from AWS Cloud.");
        }
    }

    @PostMapping("/publish")
    public ResponseEntity<String> publishMessage(@RequestBody MessageModel messageModel) {
        try{
            String status = mqttConnectService.publishMessage(messageModel.getDeviceId(), messageModel.getPayload());
            return ResponseEntity.status(200).body(status);
        }catch (Exception e){
            log.error("Exception while publishing message - ERR0R");
            return ResponseEntity.status(500).body("Device "+messageModel.getDeviceId()+" failed to publish message on AWS Cloud.");
        }

    }


    @PutMapping("/update/props/{deviceId}")
    public ResponseEntity<String> updateThing(@PathVariable String deviceId, @RequestBody Map<String, String> attributes) {
        try{
            iotService.updateThingAttributes(deviceId, attributes);
            return ResponseEntity.status(200).body("Properties updated successfully");
        }catch (Exception ex){
            log.error("Exception while updating things attributes {}", ex.getMessage());
            return ResponseEntity.status(500).body("Error updating things attributes for device : "+deviceId);
        }
    }

    @PostMapping("/cloud/publish")
    public void publishMessage(@RequestParam String message) {
        sqsService.sendMessage(message);
    }

    @GetMapping("/receive/message/{deviceId}")
    public ResponseEntity<String> receiveMessage(@PathVariable String deviceId) {
        return ResponseEntity.status(200).body(mqttConnectService.receiveMessage(deviceId));
    }
}
