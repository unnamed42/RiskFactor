package com.tjh.riskfactor.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tjh.riskfactor.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);

    @Query("select u.id from User u where u.username = :username")
    Optional<Integer> findIdByUsername(String username);

    @Query(nativeQuery = true,
        value = "select g.name from `group` g where g.id = " +
                "(select m.gid from group_members m inner join users u on m.uid = u.id and u.id = :uid)"
    )
    Optional<String> findGroupNameByUserId(Integer uid);

    @Query(nativeQuery = true,
        value = "select m.gid from group_members m inner join users u on m.uid = u.id and u.id = :uid"
    )
    Optional<Integer> findGroupIdManagedByUserId(Integer uid);

}
