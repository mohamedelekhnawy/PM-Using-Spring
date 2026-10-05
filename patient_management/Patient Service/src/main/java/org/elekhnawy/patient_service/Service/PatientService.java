package org.elekhnawy.patient_service.Service;

import org.elekhnawy.patient_service.Dto.PatientRequestDto;
import org.elekhnawy.patient_service.Dto.PatientResponseDto;
import org.elekhnawy.patient_service.Exception.EmailAlreadyExistsException;
import org.elekhnawy.patient_service.Exception.PatientNotFoundException;
import org.elekhnawy.patient_service.Mapper.PatientMapper;
import org.elekhnawy.patient_service.Model.Patient;
import org.elekhnawy.patient_service.Repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PatientService {
    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository){
        this.patientRepository = patientRepository;
    }
    
    public List<PatientResponseDto> getAllPatients(){
        List<Patient> patients = patientRepository.findAll();
        return patients.stream()
                .map(PatientMapper::ToDto)
                .toList();
    }
    
    public PatientResponseDto getPatientById(UUID id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + id));
        return PatientMapper.ToDto(patient);
    }
    
    @Transactional
    public PatientResponseDto createPatient(PatientRequestDto requestDto) {
        if (patientRepository.existsByEmail(requestDto.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists: " + requestDto.getEmail());
        }
        
        Patient patient = PatientMapper.ToEntity(requestDto);
        Patient savedPatient = patientRepository.save(patient);
        return PatientMapper.ToDto(savedPatient);
    }
    
    @Transactional
    public PatientResponseDto updatePatient(UUID id, PatientRequestDto requestDto) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + id));
        
        if (requestDto.getEmail() != null && !requestDto.getEmail().isBlank()) {
            if (patientRepository.existsByEmailAndIdNot(requestDto.getEmail(), id)) {
                throw new EmailAlreadyExistsException("Email already exists: " + requestDto.getEmail());
            }
        }
        
        PatientMapper.updateEntity(patient, requestDto);
        Patient updatedPatient = patientRepository.save(patient);
        return PatientMapper.ToDto(updatedPatient);
    }
    
    @Transactional
    public void deletePatient(UUID id) {
        if (!patientRepository.existsById(id)) {
            throw new PatientNotFoundException("Patient not found with id: " + id);
        }
        patientRepository.deleteById(id);
    }
}
