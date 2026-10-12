package com.hospital.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity @Table(name = "alerts") @Data
public class Alert {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String type;                       // SHORTAGE or LOW
    @Column(length = 500) private String message;
    private LocalDateTime createdAt;
    private boolean acknowledged = false;
}
