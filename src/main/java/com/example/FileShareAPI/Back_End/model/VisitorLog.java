package com.example.FileShareAPI.Back_End.model;

import com.example.FileShareAPI.Back_End.dto.IpApiResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VisitorLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String ipAddress;
    private LocalDateTime timestamp;

    // Geo loc data
    private String country;
    private String regionName;
    private String city;
    private float lat;
    private float lon;
    private String isp; //internet service provider
    private String org;
    private Boolean mobile; //cellular connection
    private Boolean proxy; //Proxy, VPN or Tor exit address
    private Boolean hosting; //Hosting, colocated or data center

    private boolean geoLocFetched; // for checking if this entity has had its ips geolocation fetched


    public VisitorLog(String ipAddress) {
        this.ipAddress = ipAddress;
        this.timestamp = LocalDateTime.now(ZoneId.of("Europe/Tallinn"));
    }

    public void updateGeoLoc(IpApiResponse geoLocData) {
        this.geoLocFetched = true;

        if (geoLocData.getStatus().equals("fail")) return;

        this.country = geoLocData.getCountry();
        this.regionName = geoLocData.getRegionName();
        this.city = geoLocData.getCity();
        this.lat = geoLocData.getLat();
        this.lon = geoLocData.getLon();
        this.isp = geoLocData.getIsp();
        this.org = geoLocData.getOrg();
        this.mobile = geoLocData.isMobile();
        this.proxy = geoLocData.isProxy();
        this.hosting = geoLocData.isHosting();
    }
}
