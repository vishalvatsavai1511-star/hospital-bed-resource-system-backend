package com.hospital.model;

import com.hospital.model.Enums.*;
import jakarta.persistence.*;
import lombok.Data;

@Entity @Table(name = "beds") @Data
public class Bed {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(unique = true, nullable = false) private String bedNumber;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private BedType type;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private BedStatus status = BedStatus.AVAILABLE;
    private String ward;
}
