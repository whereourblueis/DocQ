package com.teamB.hospitalreservation.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teamB.hospitalreservation.entity.Location;
import com.teamB.hospitalreservation.repository.LocationRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/location-db")

public class LocationDbController {

    private final LocationRepository locationRepository;

    @GetMapping
    public List<Location> getAllLocations(){
        return locationRepository.findAll();
    }

}
