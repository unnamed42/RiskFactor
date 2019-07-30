package com.tjh.riskfactor.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.tjh.riskfactor.entities.Patient;
import com.tjh.riskfactor.services.PatientService;

import java.util.List;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService service;

    @RequestMapping(value = "", method = RequestMethod.GET)
    List<Patient> getPatients() {
        return service.getAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    Patient getPatient(@PathVariable Integer id) {
        return service.getOne(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    void deletePatient(@PathVariable Integer id) {
        service.deleteOne(id);
    }

}
