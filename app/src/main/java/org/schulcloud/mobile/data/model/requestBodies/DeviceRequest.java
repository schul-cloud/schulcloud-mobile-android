package org.schulcloud.mobile.data.model.requestBodies;

public class DeviceRequest {
    public String service;
    public String type;
    public String name;
    public String token;
    public String device_token;
    public String OS;

    public DeviceRequest(String service, String type, String name, String token, String device_token, String OS) {
        this.service = service;
        this.type = type;
        this.name = name;
        this.token = token;
        this.device_token = device_token;
        this.OS = OS;
    }
}
