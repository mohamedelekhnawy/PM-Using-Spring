package org.elekhnawy.patient_service.Dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.*;
import org.elekhnawy.patient_service.Validation.OnCreate;
import org.elekhnawy.patient_service.Validation.OnUpdate;

@JsonPropertyOrder({"name", "email", "phoneNumber", "dateOfBirth", "address"})
public class PatientRequestDto {
    
    @NotBlank(message = "Name is required", groups = OnCreate.class)
    private String name;

    @NotBlank(message = "Email is required", groups = OnCreate.class)
    @Email(message = "Invalid email format", groups = {OnCreate.class, OnUpdate.class})
    private String email;

    @NotBlank(message = "Phone number is required", groups = OnCreate.class)
    private String phoneNumber;

    @NotBlank(message = "Date of birth is required", groups = OnCreate.class)
    private String dateOfBirth;

    @NotBlank(message = "Address is required", groups = OnCreate.class)
    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
