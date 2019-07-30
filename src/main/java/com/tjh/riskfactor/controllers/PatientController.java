package com.tjh.riskfactor.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.tjh.riskfactor.entities.Patient;
import com.tjh.riskfactor.repos.PatientRepository;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientRepository repo;

    @RequestMapping(value = "", method = RequestMethod.GET)
    List<Patient> getPatients() {
        return repo.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    Patient getPatient(@PathVariable Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("patient record with id [%d] not found", id)));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    void deletePatient(@PathVariable Integer id) {
        repo.deleteById(id);
    }

}
