package com.hospital.repository;

import com.hospital.model.*;
import com.hospital.model.Enums.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface AdmissionRepository extends JpaRepository<Admission, Long> {
    long countByStatus(AdmissionStatus status);
}
