package com.hospital.model;

import com.hospital.model.Enums.AdmissionStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity @Table(name = "admissions") @Data
public class Admission {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false) private Patient patient;
    @ManyToOne(optional = false) private Bed bed;
    @ManyToOne private Doctor doctor;
    private LocalDateTime admittedAt;
    private LocalDateTime dischargedAt;
    @Enumerated(EnumType.STRING) private AdmissionStatus status = AdmissionStatus.ADMITTED;
}
