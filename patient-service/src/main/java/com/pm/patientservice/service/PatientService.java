package com.pm.patientservice.service;

import billing.BillingResponse;
import billing.BillingServiceGrpc;
import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.exception.EmailAlreadyExistsException;
import com.pm.patientservice.exception.PatientNotFoundException;
import com.pm.patientservice.grpc.BillingServiceGrpcClient;
import com.pm.patientservice.kafka.KafkaProducer;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class PatientService {

    private final PatientRepository patientRepository;
    private final BillingServiceGrpcClient billingServiceGrpcClient;
    private final KafkaProducer kafkaProducer;

    public PatientService(
            PatientRepository patientRepository,
            BillingServiceGrpcClient billingServiceGrpcClient,
            KafkaProducer kafkaProducer
    ){
        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
        this.kafkaProducer = kafkaProducer;
    }

    public List<PatientResponseDTO> getAllPatients(){
        List<Patient> patients = patientRepository.findAll();

        log.info("All users fetched successfully");

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

        log.info("Creating patient with email: {}", patientRequestDTO.email());

        Patient newPatient = PatientMapper.toPatient(patientRequestDTO);

        Patient savedPatient = patientRepository.save(newPatient);

        log.info(
                "Patient saved successfully. ID = {}. Calling Billing Service...",
                savedPatient.getId()
        );

        BillingResponse billingResponse =
                billingServiceGrpcClient.createBillingAccount(
                        savedPatient.getId().toString(),
                        savedPatient.getName(),
                        savedPatient.getEmail()
                );

        log.info("Billing response received: {}", billingResponse);

        kafkaProducer.sendEvent(savedPatient);

        log.info("Kafka event has been sent to the Kafka producer");

        log.info("Patient created successfully {}" , savedPatient.getId());



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

        log.info("Patient updated successfully {}", updatedPatient.getId());

        return PatientMapper.toDTO(updatedPatient);

    }

    public void deletePatient(UUID id){
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with id " + id));

        patientRepository.deleteById(id);

        log.info("Patient with id {} deleted successfully!", id);
    }
}
