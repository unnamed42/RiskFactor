package com.tjh.riskfactor.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tjh.riskfactor.entity.form.Section;

import java.util.List;

@Repository
public interface SectionRepository extends JpaRepository<Section, Integer> {

    @Query(nativeQuery = true, value =
        "select s.* from section s inner join task_sections ts " +
            "on s.id = ts.sid and ts.tid = :taskId " +
            "order by ts.seq"
    )
    List<Section> findAllByOwnerTaskId(Integer taskId);

}
