package org.elekhnawy.patient_service.Controller;

import org.elekhnawy.patient_service.Dto.PatientRequestDto;
import org.elekhnawy.patient_service.Dto.PatientResponseDto;
import org.elekhnawy.patient_service.Service.PatientService;
import org.elekhnawy.patient_service.Validation.OnCreate;
import org.elekhnawy.patient_service.Validation.OnUpdate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/patients")
public class PatientController {
    
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public ResponseEntity<List<PatientResponseDto>> getAllPatients() {
        List<PatientResponseDto> patients = patientService.getAllPatients();
        return ResponseEntity.ok(patients);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PatientResponseDto> getPatientById(@PathVariable UUID id) {
        PatientResponseDto patient = patientService.getPatientById(id);
        return ResponseEntity.ok(patient);
    }
    
    @PostMapping
    public ResponseEntity<PatientResponseDto> createPatient(
            @Validated(OnCreate.class) @RequestBody PatientRequestDto requestDto) {
        PatientResponseDto createdPatient = patientService.createPatient(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPatient);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PatientResponseDto> updatePatient(
            @PathVariable UUID id,
            @Validated(OnUpdate.class) @RequestBody PatientRequestDto requestDto) {
        PatientResponseDto updatedPatient = patientService.updatePatient(id, requestDto);
        return ResponseEntity.ok(updatedPatient);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable UUID id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}
