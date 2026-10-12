package com.hospital.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity @Table(name = "reports") @Data
public class Report {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private LocalDateTime generatedAt;
    private String generatedBy;
    private long totalBeds, availableBeds, occupiedBeds;
    private long totalAdmissions, activeAdmissions;
    private long emergencyTotal, emergencyAllocated, emergencyShortage, emergencyPending;
}
