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

    @Query(nativeQuery = true,
        value = "select m.gid from group_members m where m.uid = :uid"
    )
    Optional<Integer> findIdByMemberId(Integer uid);

    @Query("select g.id from Group g join g.admins a on a.username = :username")
    Optional<Integer> findIdByAdminName(String username);

    @Query("select m.username from Group g join g.members m on g.id = :gid")
    List<String> findMemberNamesById(Integer gid);

    @Query("select g.name from Group g join g.members m on m.username = :name")
    Optional<String> findNameByMemberName(String name);

}
