package com.example.FileShareAPI.Back_End.repo;

import com.example.FileShareAPI.Back_End.model.VisitorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VisitorLogRepo extends JpaRepository<VisitorLog, Long> {

    boolean existsByIpAddress(String ipAddress);

    @Query("SELECT DISTINCT v.ipAddress FROM VisitorLog v WHERE v.geoLocFetched = false")
    List<String> findDistinctIpsWhereGeoLocNotFetched(Pageable pageable);

    List<VisitorLog> findAllByIpAddressAndGeoLocFetchedIsFalse(String ipAddress);

}
