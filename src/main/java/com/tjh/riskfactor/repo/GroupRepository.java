package com.tjh.riskfactor.repo;

import com.tjh.riskfactor.entity.User;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tjh.riskfactor.entity.Group;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Integer> {

    Optional<Group> findByName(String name);

    @Query("select m.username from Group g inner join g.members m on g.id = :gid")
    List<String> findMemberNamesById(Integer gid);

}
