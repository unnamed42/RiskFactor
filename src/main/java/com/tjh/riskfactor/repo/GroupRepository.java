package com.tjh.riskfactor.repo;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tjh.riskfactor.entity.Group;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface GroupRepository extends JpaRepository<Group, Integer> {

    Optional<Group> findByName(String name);

    Set<Group> findByNameIn(Collection<String> names);

    @Query("select g.name from Group g")
    List<String> getAllGroupNames();

    boolean existsByName(String name);

}
