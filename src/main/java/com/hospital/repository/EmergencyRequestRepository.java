package com.hospital.repository;

import com.hospital.model.*;
import com.hospital.model.Enums.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface EmergencyRequestRepository extends JpaRepository<EmergencyRequest, Long> {
    long countByStatus(RequestStatus status);
    List<EmergencyRequest> findByStatusAndRequiredBedType(RequestStatus status, BedType type);
}
