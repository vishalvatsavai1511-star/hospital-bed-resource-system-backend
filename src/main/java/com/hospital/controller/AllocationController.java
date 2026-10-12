package com.hospital.controller;

import com.hospital.model.Admission;
import com.hospital.model.Allocation;
import com.hospital.repository.AdmissionRepository;
import com.hospital.repository.AllocationRepository;
import com.hospital.service.AllocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AllocationController {
    private final AllocationRepository allocations;
    private final AdmissionRepository admissions;
    private final AllocationService service;

    public record AllocateDto(Long patientId, Long bedId, Long doctorId) {}

    @GetMapping("/allocations")
    public List<Allocation> all() { return allocations.findAll(Sort.by(Sort.Direction.DESC, "id")); }

    @PostMapping("/allocations")
    @ResponseStatus(HttpStatus.CREATED)
    public Allocation allocate(@RequestBody AllocateDto d) {
        if (d.patientId() == null || d.bedId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Patient and bed are required");
        return service.manualAllocate(d.patientId(), d.bedId(), d.doctorId());
    }

    @PostMapping("/allocations/{id}/release")
    public Allocation release(@PathVariable Long id) { return service.release(id); }

    @GetMapping("/admissions")
    public List<Admission> admissions() { return admissions.findAll(Sort.by(Sort.Direction.DESC, "id")); }
}
