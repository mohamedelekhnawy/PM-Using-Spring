package org.elekhnawy.patient_service.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.elekhnawy.patient_service.Dto.PatientRequestDto;
import org.elekhnawy.patient_service.Dto.PatientResponseDto;
import org.elekhnawy.patient_service.Exception.ErrorResponse;
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
@Tag(name = "Patient Management", description = "APIs for managing patient records")
public class PatientController {
    
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @Operation(
        summary = "Get all patients",
        description = "Retrieve a list of all registered patients in the system"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of patients",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = PatientResponseDto.class)))
    })
    @GetMapping
    public ResponseEntity<List<PatientResponseDto>> getAllPatients() {
        List<PatientResponseDto> patients = patientService.getAllPatients();
        return ResponseEntity.ok(patients);
    }
    
    @Operation(
        summary = "Get patient by ID",
        description = "Retrieve a specific patient by their unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Patient found",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = PatientResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Patient not found",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<PatientResponseDto> getPatientById(
            @Parameter(description = "Patient UUID", required = true)
            @PathVariable UUID id) {
        PatientResponseDto patient = patientService.getPatientById(id);
        return ResponseEntity.ok(patient);
    }
    
    @Operation(
        summary = "Create new patient",
        description = "Register a new patient in the system with all required information"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Patient created successfully",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = PatientResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Email already exists",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<PatientResponseDto> createPatient(
            @Parameter(description = "Patient data", required = true)
            @Validated(OnCreate.class) @RequestBody PatientRequestDto requestDto) {
        PatientResponseDto createdPatient = patientService.createPatient(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPatient);
    }
    
    @Operation(
        summary = "Update patient",
        description = "Update existing patient information. All fields are optional except ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Patient updated successfully",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = PatientResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Patient not found",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Email already exists",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<PatientResponseDto> updatePatient(
            @Parameter(description = "Patient UUID", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Updated patient data", required = true)
            @Validated(OnUpdate.class) @RequestBody PatientRequestDto requestDto) {
        PatientResponseDto updatedPatient = patientService.updatePatient(id, requestDto);
        return ResponseEntity.ok(updatedPatient);
    }
    
    @Operation(
        summary = "Delete patient",
        description = "Remove a patient from the system permanently"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Patient deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Patient not found",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(
            @Parameter(description = "Patient UUID", required = true)
            @PathVariable UUID id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}
