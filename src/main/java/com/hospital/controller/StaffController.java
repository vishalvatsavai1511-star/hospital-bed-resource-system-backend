package com.hospital.controller;

import com.hospital.model.Staff;
import com.hospital.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class StaffController extends CrudController<Staff> {
    private final StaffRepository repo;
    @Override protected JpaRepository<Staff, Long> repo() { return repo; }
}
