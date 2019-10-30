package com.tjh.riskfactor.repo;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.tjh.riskfactor.entity.form.Task;

import java.util.Date;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

    interface TaskBrief {
        Integer getId();
        String getName();
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        Date getMtime();
        String getCenter();
    }

    interface SectionBrief {
        Integer getId();
        String getTitle();
    }

    @Query("select t.id as id, t.name as name, t.mtime as mtime, g.name as center from Task t join t.group g")
    List<TaskBrief> findAllTasks();

    @Query("select s.id as id, s.title as title from Task t join t.sections s where t.id = :id")
    List<SectionBrief> findSectionNamesById(Integer id);

}
