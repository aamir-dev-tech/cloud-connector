package com.dev.cloud_connect.controller;

import com.dev.cloud_connect.model.DeviceDetails;
import com.dev.cloud_connect.service.IotService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.iot.model.ResourceNotFoundException;

@RestController
@RequestMapping("/v1/device")
@Slf4j
@AllArgsConstructor
public class DeviceController {

    private IotService iotService;


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
}
