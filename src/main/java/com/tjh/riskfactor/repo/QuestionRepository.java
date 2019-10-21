package com.tjh.riskfactor.repo;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tjh.riskfactor.entity.form.Question;

import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {

    Optional<Question> findByField(String fieldName);

}
