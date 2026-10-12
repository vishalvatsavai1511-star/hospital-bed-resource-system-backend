package com.hospital.service;

import com.hospital.model.*;
import com.hospital.model.Enums.*;
import com.hospital.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AllocationService {
    private final BedRepository beds;
    private final PatientRepository patients;
    private final DoctorRepository doctors;
    private final AdmissionRepository admissions;
    private final AllocationRepository allocations;
    private final EmergencyRequestRepository requests;
    private final AlertRepository alerts;

    private static ResponseStatusException notFound(String what) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, what + " not found");
    }

    /** Submit an emergency request and try to allocate a matching bed immediately. */
    @Transactional
    public EmergencyRequest submitEmergency(Long patientId, Urgency urgency, BedType type, String notes) {
        Patient p = patients.findById(patientId).orElseThrow(() -> notFound("Patient"));
        EmergencyRequest r = new EmergencyRequest();
        r.setPatient(p);
        r.setUrgency(urgency);
        r.setRequiredBedType(type);
        r.setNotes(notes);
        r.setStatus(RequestStatus.PENDING);
        r.setCreatedAt(LocalDateTime.now());
        return tryAllocate(requests.save(r));
    }

    /** Match the request with the first available bed of the required type; raise a shortage alert if none. */
    @Transactional
    public EmergencyRequest tryAllocate(EmergencyRequest r) {
        if (r.getStatus() == RequestStatus.ALLOCATED) throw new IllegalStateException("Request is already allocated");
        if (allocations.existsByPatientIdAndActiveTrue(r.getPatient().getId()))
            throw new IllegalStateException("Patient already has an active bed allocation");

        Optional<Bed> bed = beds.findFirstByTypeAndStatusOrderByIdAsc(r.getRequiredBedType(), BedStatus.AVAILABLE);
        if (bed.isEmpty()) {
            if (r.getStatus() != RequestStatus.SHORTAGE) {
                alert("SHORTAGE", "No " + r.getRequiredBedType() + " bed available for " + r.getPatient().getName()
                        + " (" + r.getUrgency() + "). Find an alternative or reallocate.");
            }
            r.setStatus(RequestStatus.SHORTAGE);
            return requests.save(r);
        }
        assign(r.getPatient(), bed.get(), null, r);
        r.setStatus(RequestStatus.ALLOCATED);
        r.setAllocatedBed(bed.get());
        return requests.save(r);
    }

    /** Receptionist / nurse picks a specific bed for a patient. */
    @Transactional
    public Allocation manualAllocate(Long patientId, Long bedId, Long doctorId) {
        Patient p = patients.findById(patientId).orElseThrow(() -> notFound("Patient"));
        Bed bed = beds.findById(bedId).orElseThrow(() -> notFound("Bed"));
        Doctor d = doctorId == null ? null : doctors.findById(doctorId).orElseThrow(() -> notFound("Doctor"));
        if (bed.getStatus() != BedStatus.AVAILABLE) throw new IllegalStateException("Bed " + bed.getBedNumber() + " is not available");
        if (allocations.existsByPatientIdAndActiveTrue(patientId)) throw new IllegalStateException("Patient already has an active bed allocation");
        return assign(p, bed, d, null);
    }

    private Allocation assign(Patient p, Bed bed, Doctor d, EmergencyRequest r) {
        bed.setStatus(BedStatus.OCCUPIED);
        beds.save(bed);

        Admission ad = new Admission();
        ad.setPatient(p);
        ad.setBed(bed);
        ad.setDoctor(d);
        ad.setAdmittedAt(LocalDateTime.now());
        ad.setStatus(AdmissionStatus.ADMITTED);
        admissions.save(ad);

        Allocation a = new Allocation();
        a.setPatient(p);
        a.setBed(bed);
        a.setEmergencyRequest(r);
        a.setAdmission(ad);
        a.setAllocatedAt(LocalDateTime.now());
        a.setActive(true);
        allocations.save(a);

        long left = beds.countByTypeAndStatus(bed.getType(), BedStatus.AVAILABLE);
        if (left <= 1) alert("LOW", "Only " + left + " " + bed.getType() + " bed(s) left available.");
        return a;
    }

    /** Discharge / release a bed, then serve the most urgent waiting request for that bed type. */
    @Transactional
    public Allocation release(Long allocationId) {
        Allocation a = allocations.findById(allocationId).orElseThrow(() -> notFound("Allocation"));
        if (!a.isActive()) throw new IllegalStateException("Allocation already released");
        LocalDateTime now = LocalDateTime.now();

        a.setActive(false);
        a.setReleasedAt(now);
        allocations.save(a);

        Admission ad = a.getAdmission();
        if (ad != null) {
            ad.setStatus(AdmissionStatus.DISCHARGED);
            ad.setDischargedAt(now);
            admissions.save(ad);
        }
        Bed bed = a.getBed();
        bed.setStatus(BedStatus.AVAILABLE);
        beds.save(bed);

        requests.findByStatusAndRequiredBedType(RequestStatus.SHORTAGE, bed.getType()).stream()
                .filter(q -> !allocations.existsByPatientIdAndActiveTrue(q.getPatient().getId()))
                .min(Comparator.comparing(EmergencyRequest::getUrgency, Comparator.reverseOrder())
                        .thenComparing(EmergencyRequest::getId))
                .ifPresent(this::tryAllocate);
        return a;
    }

    private void alert(String type, String message) {
        Alert al = new Alert();
        al.setType(type);
        al.setMessage(message);
        al.setCreatedAt(LocalDateTime.now());
        alerts.save(al);
    }
}
