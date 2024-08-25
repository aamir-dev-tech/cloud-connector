package com.dev.cloud_connect.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/device")
@Slf4j
public class DeviceController {

    private static final Logger log = LoggerFactory.getLogger(DeviceController.class);

    @GetMapping("/register/{deviceName}")
    public String registerDevice(@PathVariable String deviceName) {
        log.info("Device Registration call >>>>");
        return String.format("Device [%s] registered successfully!", deviceName);
    }
}
