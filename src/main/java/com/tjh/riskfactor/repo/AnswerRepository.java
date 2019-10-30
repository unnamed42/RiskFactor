package com.tjh.riskfactor.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tjh.riskfactor.entity.form.Answer;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Integer> {

    interface AnswerBrief {
        Integer getId();
        String getCreator();
        Date getMtime();
    }

    static final String ANSWER_BRIEF = "select a.id as id, c.username as creator, a.mtime as mtime from Answer a join a.creator c ";

    @Query(ANSWER_BRIEF + "on c.username = :creatorName")
    List<AnswerBrief> findAnswersByCreatorName(String creatorName);

    @Query(ANSWER_BRIEF + "on c.username in :names")
    List<AnswerBrief> findAnswersForCreatorNames(Collection<String> names);

}
