package com.hospital.service;

import com.hospital.model.Enums.*;
import com.hospital.model.Report;
import com.hospital.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final BedRepository beds;
    private final AdmissionRepository admissions;
    private final EmergencyRequestRepository requests;
    private final AlertRepository alerts;
    private final ReportRepository reports;

    /** Live numbers used by the dashboard. */
    public Map<String, Object> summary() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("totalBeds", beds.count());
        m.put("availableBeds", beds.countByStatus(BedStatus.AVAILABLE));
        m.put("occupiedBeds", beds.countByStatus(BedStatus.OCCUPIED));
        m.put("maintenanceBeds", beds.countByStatus(BedStatus.MAINTENANCE));
        Map<String, Map<String, Long>> byType = new LinkedHashMap<>();
        for (BedType t : BedType.values()) {
            byType.put(t.name(), Map.of(
                    "total", beds.countByType(t),
                    "available", beds.countByTypeAndStatus(t, BedStatus.AVAILABLE)));
        }
        m.put("byType", byType);
        m.put("totalAdmissions", admissions.count());
        m.put("activeAdmissions", admissions.countByStatus(AdmissionStatus.ADMITTED));
        m.put("emergencyTotal", requests.count());
        m.put("emergencyPending", requests.countByStatus(RequestStatus.PENDING));
        m.put("emergencyAllocated", requests.countByStatus(RequestStatus.ALLOCATED));
        m.put("emergencyShortage", requests.countByStatus(RequestStatus.SHORTAGE));
        m.put("openAlerts", alerts.countByAcknowledgedFalse());
        return m;
    }

    /** Save a snapshot of the current numbers as a report row. */
    public Report generate(String username) {
        Report r = new Report();
        r.setGeneratedAt(LocalDateTime.now());
        r.setGeneratedBy(username);
        r.setTotalBeds(beds.count());
        r.setAvailableBeds(beds.countByStatus(BedStatus.AVAILABLE));
        r.setOccupiedBeds(beds.countByStatus(BedStatus.OCCUPIED));
        r.setTotalAdmissions(admissions.count());
        r.setActiveAdmissions(admissions.countByStatus(AdmissionStatus.ADMITTED));
        r.setEmergencyTotal(requests.count());
        r.setEmergencyAllocated(requests.countByStatus(RequestStatus.ALLOCATED));
        r.setEmergencyShortage(requests.countByStatus(RequestStatus.SHORTAGE));
        r.setEmergencyPending(requests.countByStatus(RequestStatus.PENDING));
        return reports.save(r);
    }
}
