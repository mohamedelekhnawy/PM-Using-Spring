package org.elekhnawy.patient_service.Service;

import org.elekhnawy.patient_service.Dto.PatientResponseDto;
import org.elekhnawy.patient_service.Mapper.PatientMapper;
import org.elekhnawy.patient_service.Model.Patient;
import org.elekhnawy.patient_service.Repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {
    private PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository){
        this.patientRepository =patientRepository;
    }
    public List<PatientResponseDto> getPatient(){
        List<Patient> patients = patientRepository.findAll();
        List<PatientResponseDto> patientResponseDtos = patients.stream()
                .map(patient -> PatientMapper.ToDto(patient)).toList();
        return patientResponseDtos;
    }
}
