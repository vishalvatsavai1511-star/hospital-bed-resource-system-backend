package com.hospital.controller;

import com.hospital.model.Alert;
import com.hospital.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {
    private final AlertRepository alerts;

    @GetMapping
    public List<Alert> all() { return alerts.findAll(Sort.by(Sort.Direction.DESC, "id")); }

    @PatchMapping("/{id}/ack")
    public Alert acknowledge(@PathVariable Long id) {
        Alert a = alerts.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alert not found"));
        a.setAcknowledged(true);
        return alerts.save(a);
    }
}
