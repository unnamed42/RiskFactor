package com.tjh.riskfactor.repo;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tjh.riskfactor.entity.form.Sections;

import java.util.Optional;

@Repository
public interface SectionsRepository extends JpaRepository<Sections, Integer> {

    Optional<Sections> findByName(String name);

}
