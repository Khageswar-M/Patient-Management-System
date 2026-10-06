package com.pm.patientservice.service;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    public List<PatientResponseDTO> getAllPatients(){
        List<Patient> patients = patientRepository.findAll();

        return patients.stream()
                .map(
                        PatientMapper::toDTO
                )
                .toList();
    }

    public PatientResponseDTO cretePatient(
            PatientRequestDTO patientRequestDTO
    ){
        Patient newPatient = PatientMapper.toPatient(
                patientRequestDTO
        );

        Patient savedPatient = patientRepository.save(newPatient);

        return PatientResponseDTO.builder()
                .id(savedPatient.getId().toString())
                .name(savedPatient.getName())
                .email(savedPatient.getEmail())
                .address(savedPatient.getAddress())
                .dateOfBirth(savedPatient.getDateOfBirth().toString())
                .build();
    }
}
