package org.elekhnawy.patient_service.Mapper;

import org.elekhnawy.patient_service.Dto.PatientRequestDto;
import org.elekhnawy.patient_service.Dto.PatientResponseDto;
import org.elekhnawy.patient_service.Exception.InvalidDateFormatException;
import org.elekhnawy.patient_service.Model.Patient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class PatientMapper {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    public static PatientResponseDto ToDto(Patient patient) {
        PatientResponseDto patientDto = new PatientResponseDto();
        patientDto.setId(patient.getId().toString());
        patientDto.setName(patient.getName());
        patientDto.setEmail(patient.getEmail());
        patientDto.setPhoneNumber(patient.getPhoneNumber());
        patientDto.setAddress(patient.getAddress());
        patientDto.setDateOfBirth(patient.getDateOfBirth().toString());
        return patientDto;
    }
    
    public static Patient ToEntity(PatientRequestDto requestDto) {
        Patient patient = new Patient();
        patient.setName(requestDto.getName());
        patient.setEmail(requestDto.getEmail());
        patient.setPhoneNumber(requestDto.getPhoneNumber());
        patient.setAddress(requestDto.getAddress());
        
        try {
            LocalDate dateOfBirth = LocalDate.parse(requestDto.getDateOfBirth(), DATE_FORMATTER);
            patient.setDateOfBirth(dateOfBirth);
        } catch (DateTimeParseException e) {
            throw new InvalidDateFormatException("Invalid date format. Expected format: yyyy-MM-dd");
        }
        
        return patient;
    }
    
    public static void updateEntity(Patient patient, PatientRequestDto requestDto) {
        if (requestDto.getName() != null && !requestDto.getName().isBlank()) {
            patient.setName(requestDto.getName());
        }
        if (requestDto.getEmail() != null && !requestDto.getEmail().isBlank()) {
            patient.setEmail(requestDto.getEmail());
        }
        if (requestDto.getPhoneNumber() != null && !requestDto.getPhoneNumber().isBlank()) {
            patient.setPhoneNumber(requestDto.getPhoneNumber());
        }
        if (requestDto.getAddress() != null && !requestDto.getAddress().isBlank()) {
            patient.setAddress(requestDto.getAddress());
        }
        if (requestDto.getDateOfBirth() != null && !requestDto.getDateOfBirth().isBlank()) {
            try {
                LocalDate dateOfBirth = LocalDate.parse(requestDto.getDateOfBirth(), DATE_FORMATTER);
                patient.setDateOfBirth(dateOfBirth);
            } catch (DateTimeParseException e) {
                throw new InvalidDateFormatException("Invalid date format. Expected format: yyyy-MM-dd");
            }
        }
    }
}
