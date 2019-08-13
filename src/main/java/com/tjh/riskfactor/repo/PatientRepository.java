package com.tjh.riskfactor.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tjh.riskfactor.entity.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Integer> {
}