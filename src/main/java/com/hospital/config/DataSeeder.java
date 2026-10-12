package com.hospital.config;

import com.hospital.model.*;
import com.hospital.model.Enums.*;
import com.hospital.repository.*;
import com.hospital.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/** Adds demo users, beds and doctors on first start (only when the tables are empty). */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final UserRepository users;
    private final BedRepository beds;
    private final DoctorRepository doctors;
    private final StaffRepository staff;
    private final AuthService auth;

    @Override
    public void run(String... args) {
        if (users.count() == 0) {
            addUser("admin", "admin123", Role.ADMIN);
            addUser("doctor1", "doctor123", Role.DOCTOR);
            addUser("nurse1", "nurse123", Role.NURSE);
            addUser("reception1", "reception123", Role.RECEPTIONIST);
        }
        if (beds.count() == 0) {
            for (int i = 1; i <= 10; i++) addBed("G-" + String.format("%02d", i), BedType.GENERAL, "General Ward");
            for (int i = 1; i <= 4; i++) addBed("ICU-" + String.format("%02d", i), BedType.ICU, "ICU Wing");
            for (int i = 1; i <= 3; i++) addBed("SP-" + String.format("%02d", i), BedType.SPECIAL, "Special Care");
        }
        if (doctors.count() == 0) {
            addDoctor("Dr. Ananya Rao", "Emergency Medicine", "9000000001");
            addDoctor("Dr. Rahul Verma", "Cardiology", "9000000002");
            addDoctor("Dr. Meera Iyer", "Critical Care", "9000000003");
        }
        if (staff.count() == 0) {
            Staff s = new Staff();
            s.setName("Sunita Devi"); s.setRole("Head Nurse"); s.setPhone("9000000010");
            staff.save(s);
        }
    }

    private void addUser(String name, String pw, Role role) {
        User u = new User();
        u.setUsername(name); u.setPasswordHash(auth.hash(pw)); u.setRole(role);
        users.save(u);
    }

    private void addBed(String no, BedType type, String ward) {
        Bed b = new Bed();
        b.setBedNumber(no); b.setType(type); b.setWard(ward); b.setStatus(BedStatus.AVAILABLE);
        beds.save(b);
    }

    private void addDoctor(String name, String spec, String phone) {
        Doctor d = new Doctor();
        d.setName(name); d.setSpecialization(spec); d.setPhone(phone); d.setAvailable(true);
        doctors.save(d);
    }
}
