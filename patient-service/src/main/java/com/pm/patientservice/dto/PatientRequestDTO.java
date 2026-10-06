package com.pm.patientservice.dto;

import com.pm.patientservice.dto.validators.CreatePatientValidationGroup;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record PatientRequestDTO(

        // Patient Name
        @NotBlank
        @Size(
                max = 100,
                message = "Name cannot exeeds 100 characters"
        )
        String name,

        // patient email
        @NotBlank(
                message = "Email is required"
        )
        @Email(
                message = "Email should be valid"
        )
        String email,

        // patient address
        @NotBlank(
                message = "Address is required"
        )
        String address,

        // patient date of birth
        @NotBlank(
                message = "Date of birth is required"
        )
        String dateOfBirth,

        // patient registered date
        @NotBlank(
                groups = CreatePatientValidationGroup.class,
                message = "Registered date is required"
        )
        String registeredDate
){
}
