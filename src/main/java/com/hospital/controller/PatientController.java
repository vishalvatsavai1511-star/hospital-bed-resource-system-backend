package com.hospital.controller;

import com.hospital.model.Patient;
import com.hospital.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController extends CrudController<Patient> {
    private final PatientRepository repo;
    @Override protected JpaRepository<Patient, Long> repo() { return repo; }
    @Override protected String[] ignoredOnUpdate() { return new String[]{"id", "createdAt"}; }
}
