package com.hospital.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity @Table(name = "doctors") @Data
public class Doctor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String name;
    private String specialization;
    private String phone;
    private boolean available = true;
}
