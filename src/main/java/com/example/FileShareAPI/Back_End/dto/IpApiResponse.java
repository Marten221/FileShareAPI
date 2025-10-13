package com.example.FileShareAPI.Back_End.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class IpApiResponse {
    private String status;
    private String Country;
    private String regionName;
    private String city;
    private float lat;
    private float lon;
    private String isp;
    private String org;
    private boolean mobile;
    private boolean proxy;
    private boolean hosting;
}