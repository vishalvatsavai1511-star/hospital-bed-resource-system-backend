package com.hospital.model;

public final class Enums {
    private Enums() {}
    public enum Role { ADMIN, DOCTOR, NURSE, RECEPTIONIST }
    public enum BedType { GENERAL, ICU, SPECIAL }
    public enum BedStatus { AVAILABLE, OCCUPIED, MAINTENANCE }
    public enum Urgency { LOW, MEDIUM, HIGH, CRITICAL }
    public enum RequestStatus { PENDING, ALLOCATED, SHORTAGE }
    public enum AdmissionStatus { ADMITTED, DISCHARGED }
}
