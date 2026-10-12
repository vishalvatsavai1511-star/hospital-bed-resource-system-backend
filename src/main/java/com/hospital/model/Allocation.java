package com.hospital.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity @Table(name = "allocations") @Data
public class Allocation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false) private Patient patient;
    @ManyToOne(optional = false) private Bed bed;
    @ManyToOne private EmergencyRequest emergencyRequest;
    @ManyToOne private Admission admission;
    private LocalDateTime allocatedAt;
    private LocalDateTime releasedAt;
    private boolean active = true;
}
