package com.pm.patientservice.controller;

import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @GetMapping
    public ResponseEntity<List<PatientResponseDTO>> getPatients(){
        List<PatientResponseDTO> patients =
                patientService.getAllPatients();

        return ResponseEntity.ok().body(patients);
    }
}
