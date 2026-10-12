package com.hospital.controller;

import com.hospital.model.Report;
import com.hospital.model.User;
import com.hospital.repository.ReportRepository;
import com.hospital.service.ReportService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportRepository reports;
    private final ReportService service;

    @GetMapping("/summary")
    public Map<String, Object> summary() { return service.summary(); }

    @GetMapping
    public List<Report> all() { return reports.findAll(Sort.by(Sort.Direction.DESC, "id")); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Report generate(HttpServletRequest req) {
        User u = (User) req.getAttribute("user");
        return service.generate(u.getUsername());
    }
}
