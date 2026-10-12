package com.hospital.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity @Table(name = "staff") @Data
public class Staff {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String name;
    private String role;
    private String phone;
}
