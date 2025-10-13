package com.example.FileShareAPI.Back_End.service;

import com.example.FileShareAPI.Back_End.dto.IpApiResponse;
import com.example.FileShareAPI.Back_End.model.VisitorLog;
import com.example.FileShareAPI.Back_End.repo.VisitorLogRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VisitorLoggerService {
    private static final Logger log = LoggerFactory.getLogger(VisitorLoggerService.class);
    private final VisitorLogRepo visitorLogRepo;
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String IP_API_URL = "http://ip-api.com/batch?fields=status,message,country,regionName,city,lat,lon,isp,org,mobile,proxy,hosting";

    @Transactional
    public void logIp(String ipAddress) {
        boolean newSession = !visitorLogRepo.existsByIpAddress(ipAddress);
        if (newSession) visitorLogRepo.save(new VisitorLog(ipAddress));
    }

    @Scheduled(fixedDelay = 30 * 60 * 1000)
    @Transactional
    public void fetchGeoLoc() {
        Pageable pageable = PageRequest.of(0, 100);
        List<String> ipAddresses = visitorLogRepo.findDistinctIpsWhereGeoLocNotFetched(pageable);

        if (ipAddresses.isEmpty()) return;
        log.info("Updating {} distinct ip addresses", ipAddresses.size());

        List<IpApiResponse> response = getLocations(ipAddresses);
        updateAddressesInDb(response, ipAddresses);
    }

    private void updateAddressesInDb(List<IpApiResponse> response, List<String> ipAddresses) {
        for (int i = 0; i < response.size(); i++) {
            IpApiResponse currentResponse = response.get(i);
            String ipAddress = ipAddresses.get(i);
            List<VisitorLog> correspondingLogs = visitorLogRepo.findAllByIpAddressAndGeoLocFetchedIsFalse(ipAddress);

            for (VisitorLog log : correspondingLogs) {
                log.updateGeoLoc(currentResponse);
            }
            visitorLogRepo.saveAll(correspondingLogs); // save here rather than in the loop, to reduce db round trips
        }
    }

    public List<IpApiResponse> getLocations(List<String> ipAddresses) {
        // Send POST request and get the response as a List<IpApiResponse>
        ResponseEntity<List<IpApiResponse>> response = restTemplate.exchange(
                IP_API_URL,
                HttpMethod.POST,
                new HttpEntity<>(ipAddresses),
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }
}
