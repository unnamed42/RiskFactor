package com.tjh.riskfactor.repo;

import com.tjh.riskfactor.entity.form.Section;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tjh.riskfactor.entity.form.Task;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

    @Query("select s.title from Task t join t.sections s where t.id = :id")
    List<String> findSectionNamesById(Integer id);

    @Query("select t.sections from Task t where t.id = :id")
    List<Section> findSectionsById(Integer id);

}
