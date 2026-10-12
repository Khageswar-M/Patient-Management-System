package com.pm.authservice.dto;

import lombok.Builder;

@Builder
public record LoginResponseDTO(
        String token
) {
}
