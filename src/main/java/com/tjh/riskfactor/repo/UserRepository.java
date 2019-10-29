package com.tjh.riskfactor.repo;

import com.tjh.riskfactor.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tjh.riskfactor.entity.User;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query(nativeQuery = true,
        value = "select g from `group_members` m, `group` g where m.uid = :id and m.gid = g.id"
    )
    Optional<Group> findGroupById(Integer id);

}
