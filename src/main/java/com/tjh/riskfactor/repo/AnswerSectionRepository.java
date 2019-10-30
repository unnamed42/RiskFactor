package com.tjh.riskfactor.repo;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tjh.riskfactor.entity.form.AnswerSection;

import java.util.List;

@Repository
public interface AnswerSectionRepository extends JpaRepository<AnswerSection, Integer> {

    List<AnswerSection> findByIdIn(List<Integer> ids);

}
