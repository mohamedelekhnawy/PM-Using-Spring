package org.elekhnawy.patient_service.Service;

import billing.BillingServiceGrpc;
import org.elekhnawy.patient_service.Dto.PatientRequestDto;
import org.elekhnawy.patient_service.Dto.PatientResponseDto;
import org.elekhnawy.patient_service.Exception.EmailAlreadyExistsException;
import org.elekhnawy.patient_service.Exception.PatientNotFoundException;
import org.elekhnawy.patient_service.Mapper.PatientMapper;
import org.elekhnawy.patient_service.Model.Patient;
import org.elekhnawy.patient_service.Repository.PatientRepository;
import org.elekhnawy.patient_service.grpc.BillingServiceGrpcClient;
import org.elekhnawy.patient_service.kafka.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PatientService {
    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);
    private final PatientRepository patientRepository;
    private final BillingServiceGrpcClient billingServiceGrpcClient;
    private final KafkaProducer kafkaProducer;

    public PatientService(PatientRepository patientRepository,
                          BillingServiceGrpcClient billingServiceGrpcClient,
                          KafkaProducer kafkaProducer){

        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient=billingServiceGrpcClient;
        this.kafkaProducer=kafkaProducer;
    }
    
    public List<PatientResponseDto> getAllPatients(){
        List<Patient> patients = patientRepository.findAll();
        return patients.stream()
                .map(PatientMapper::ToDto)
                .collect(Collectors.toList());
    }
    
    public PatientResponseDto getPatientById(UUID id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + id));
        return PatientMapper.ToDto(patient);
    }

    @Transactional
    public PatientResponseDto createPatient(PatientRequestDto requestDto) {
        // 1. Check existing email
        if (patientRepository.existsByEmail(requestDto.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists: " + requestDto.getEmail());
        }

        // 2. Map & Save Patient
        Patient patient = PatientMapper.ToEntity(requestDto);
        Patient savedPatient = patientRepository.save(patient);

        // 3. Create Billing Account (gRPC)
        billingServiceGrpcClient.createBillingAccount(
                savedPatient.getId().toString(),
                savedPatient.getName(),
                savedPatient.getEmail()
        );

        // 4. Send Kafka Event (Send savedPatient NOT patient)
        try {
            kafkaProducer.sendEvent(savedPatient);
        } catch (Exception e) {
            log.error("Failed to send Kafka event for patient ID: {}", savedPatient.getId(), e);
        }
        return PatientMapper.ToDto(savedPatient);
    }
    @Transactional
    public PatientResponseDto updatePatient(UUID id, PatientRequestDto requestDto) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + id));
        
        if (requestDto.getEmail() != null && !requestDto.getEmail().trim().isEmpty()) {
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
