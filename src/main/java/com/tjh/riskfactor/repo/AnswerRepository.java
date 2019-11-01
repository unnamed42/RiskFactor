package com.tjh.riskfactor.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tjh.riskfactor.entity.form.Answer;
import com.tjh.riskfactor.entity.view.AnswerBrief;

import java.util.List;
import java.util.Collection;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Integer> {

    String ANSWER_BRIEF = "select a.id as id, c.username as creator, a.mtime as mtime from Answer a inner join a.task t on t.id = :taskId ";

    @Query(ANSWER_BRIEF + "inner join a.creator c on c.id = :uid")
    List<AnswerBrief> findTaskAnswersCreatedBy(Integer taskId, Integer uid);

    @Query(ANSWER_BRIEF + "inner join a.creator c")
    List<AnswerBrief> findAllByTaskId(Integer taskId);

    @Query("select a.id as id, c.username as creator, a.mtime as mtime from Answer a inner join a.task t on t.id = :taskId inner join a.creator c on c.username in :usernames")
    List<AnswerBrief> findAllByTaskIdCreatedBy(Integer taskId, Collection<String> usernames);

}
