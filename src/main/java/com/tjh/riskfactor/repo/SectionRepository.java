package com.tjh.riskfactor.repo;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tjh.riskfactor.entity.form.Section;

import java.util.Optional;

@Repository
public interface SectionRepository extends JpaRepository<Section, Integer> {

    Optional<Section> findByTitle(String title);

}
