package com.dev.cloud_connect.model;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class DeviceDetails {

    private String deviceId;
    private String deviceName;
    private String deviceRegion;
    private Map<String, String> deviceAttributes;
    private String endpointURL;
}
