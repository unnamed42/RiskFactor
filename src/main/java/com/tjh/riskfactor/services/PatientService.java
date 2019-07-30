package com.tjh.riskfactor.services;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.tjh.riskfactor.entities.Patient;
import com.tjh.riskfactor.repos.PatientRepository;
import com.tjh.riskfactor.utils.CollectionUtils;
import com.tjh.riskfactor.utils.HttpUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository repo;

    public List<Patient> getAll() {
        return CollectionUtils.toList(repo.findAll());
    }

    public Patient getOne(Integer id) {
        return HttpUtils.getById(repo, id);
    }

    public void deleteOne(Integer id) {
        repo.deleteById(id);
    }
}
