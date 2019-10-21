package com.tjh.riskfactor.repo;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tjh.riskfactor.entity.form.QuestionOption;

@Repository
public interface QuestionOptionRepository extends JpaRepository<QuestionOption, Integer> {
}
