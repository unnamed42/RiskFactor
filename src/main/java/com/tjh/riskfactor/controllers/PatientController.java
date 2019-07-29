package com.tjh.riskfactor.controllers;


import com.tjh.riskfactor.services.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.tjh.riskfactor.entities.Patient;

import java.util.List;

@RestController
@RequestMapping("/patients")
public class PatientController {

    private final PatientService service;

    @Autowired
    public PatientController(PatientService service) {
        this.service = service;
    }

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
