package com.hospital.repository;

import com.hospital.model.*;
import com.hospital.model.Enums.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface BedRepository extends JpaRepository<Bed, Long> {
    Optional<Bed> findFirstByTypeAndStatusOrderByIdAsc(BedType type, BedStatus status);
    long countByStatus(BedStatus status);
    long countByType(BedType type);
    long countByTypeAndStatus(BedType type, BedStatus status);
}
