package com.tjh.riskfactor.repo;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tjh.riskfactor.entity.form.Task;
import com.tjh.riskfactor.entity.view.TaskBrief;
import com.tjh.riskfactor.entity.view.SectionBrief;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

    static String TASK_BRIEF_ALL = "select t.id as id, t.name as name, t.mtime as mtime, g.name as center from Task t join t.group g ";

    @Query(TASK_BRIEF_ALL + "on t.id = :id")
    Optional<TaskBrief> findTaskInfoById(Integer id);

    @Query(TASK_BRIEF_ALL)
    List<TaskBrief> findAllTasks();

    @Query("select s.id as id, s.title as title from Task t join t.sections s on t.id = :id")
    List<SectionBrief> findSectionNamesById(Integer id);

}
