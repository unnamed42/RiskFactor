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

    @Query(nativeQuery = true,
        value = "select g.* from `group` g, `group_members` m, users u where u.username = :username and m.uid = u.id and m.gid = g.id"
    )
    Optional<Group> findUserGroup(String username);

}
