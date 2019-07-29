package com.tjh.riskfactor.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tjh.riskfactor.entities.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Integer> {
}