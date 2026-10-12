package com.hospital.model;

import com.hospital.model.Enums.*;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity @Table(name = "emergency_requests") @Data
public class EmergencyRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false) private Patient patient;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private Urgency urgency;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private BedType requiredBedType;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private RequestStatus status = RequestStatus.PENDING;
    @ManyToOne private Bed allocatedBed;
    private String notes;
    private LocalDateTime createdAt;
}
