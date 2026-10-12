package com.hospital.controller;

import com.hospital.model.EmergencyRequest;
import com.hospital.model.Enums.*;
import com.hospital.repository.EmergencyRequestRepository;
import com.hospital.service.AllocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/emergency-requests")
@RequiredArgsConstructor
public class EmergencyController {
    private final EmergencyRequestRepository requests;
    private final AllocationService service;

    public record EmergencyDto(Long patientId, Urgency urgency, BedType requiredBedType, String notes) {}

    @GetMapping
    public List<EmergencyRequest> all() { return requests.findAll(Sort.by(Sort.Direction.DESC, "id")); }

    /** Creates the request and auto-allocates a bed (or raises a shortage alert). */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmergencyRequest submit(@RequestBody EmergencyDto d) {
        if (d.patientId() == null || d.urgency() == null || d.requiredBedType() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Patient, urgency and bed type are required");
        return service.submitEmergency(d.patientId(), d.urgency(), d.requiredBedType(), d.notes());
    }

    @PostMapping("/{id}/retry")
    public EmergencyRequest retry(@PathVariable Long id) {
        EmergencyRequest r = requests.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found"));
        return service.tryAllocate(r);
    }
}
