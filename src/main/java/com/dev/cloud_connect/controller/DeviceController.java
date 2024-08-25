package com.dev.cloud_connect.controller;

import com.dev.cloud_connect.service.IotService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/device")
@Slf4j
@AllArgsConstructor
public class DeviceController {

    private static final Logger log = LoggerFactory.getLogger(DeviceController.class);

    private IotService iotService;


    @GetMapping("/register/{deviceName}")
    public String registerDevice(@PathVariable String deviceName) {
        log.info("Device Registration call >>>>");

        return iotService.registerDevice(deviceName);
    }
}
