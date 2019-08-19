package com.tjh.riskfactor.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.tjh.riskfactor.entity.Patient;
import com.tjh.riskfactor.repo.PatientRepository;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientRepository repo;

    @GetMapping
    List<Patient> getPatients() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    Patient getPatient(@PathVariable Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("patient record with id [%d] not found", id)));
    }

    @DeleteMapping("/{id}")
    void deletePatient(@PathVariable Integer id) {
        repo.deleteById(id);
    }

}
