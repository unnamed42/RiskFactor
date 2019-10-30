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

    @Query(nativeQuery = true,
        value = "select a.id as id, u.username as creator, a.mtime as mtime from answer a " +
                "left join task t on a.owner_task_id = t.id and t.id = :taskId " +
                "left join users u on a.creator_id = u.id"
    )
    List<AnswerBrief> findByOwnerTaskId(Integer taskId);

    @Query(nativeQuery = true,
        value = "select a.id as id, u.username as creator, a.mtime as mtime from answer a " +
                "left join task t on a.owner_task_id = t.id and t.id = :taskId " +
                "left join users u on a.creator_id = u.id and u.username = :username"
    )
    List<AnswerBrief> findAnswersInTaskCreatedBy(Integer taskId, String username);

    @Query(nativeQuery = true,
        value = "select a.id as id, u.username as creator, a.mtime as mtime from answer a " +
                "left join task t on a.owner_task_id = t.id and t.id = :taskId " +
                "left join users u on a.creator_id = u.id and u.username in :usernames"
    )
    List<AnswerBrief> findAnswersInTaskCreatedBy(Integer taskId, Collection<String> usernames);

}
