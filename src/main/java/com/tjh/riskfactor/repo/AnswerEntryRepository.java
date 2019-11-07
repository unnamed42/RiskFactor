package com.tjh.riskfactor.repo;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tjh.riskfactor.entity.form.AnswerEntry;

@Repository
public interface AnswerEntryRepository extends JpaRepository<AnswerEntry, Integer> {
}
