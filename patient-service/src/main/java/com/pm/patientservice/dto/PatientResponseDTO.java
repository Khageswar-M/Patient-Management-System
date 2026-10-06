package com.pm.patientservice.dto;

import lombok.Builder;

@Builder
public record PatientResponseDTO(

        String id,

        String name,

        String email,

        String address,

        String dateOfBirth
) {
}
