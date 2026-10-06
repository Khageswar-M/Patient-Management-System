package com.pm.patientservice.service;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.exception.EmailAlreadyExistsException;
import com.pm.patientservice.exception.PatientNotFoundException;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

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

        if(patientRepository.existsByEmail(patientRequestDTO.email())){
            throw new EmailAlreadyExistsException(
                    "A patient with this email already exists."
                    + patientRequestDTO.email()
            );
        }

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

    public PatientResponseDTO updatePatient(
            UUID id,
            PatientRequestDTO patientRequestDTO
    ){

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patent not found with id " + id));


        patient.setName(patientRequestDTO.name());
        patient.setEmail(patientRequestDTO.email());
        patient.setAddress(patientRequestDTO.address());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.dateOfBirth()));

        Patient updatedPatient = patientRepository.save(patient);

        return PatientMapper.toDTO(updatedPatient);

    }
}
