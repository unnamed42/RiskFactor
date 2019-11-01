package com.tjh.riskfactor.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tjh.riskfactor.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    @Query("select u.id from User u where u.username = :username")
    Optional<Integer> findIdByUsername(String username);

    @Query("select g.name from User u inner join u.group g on u.id = :uid")
    Optional<String> findGroupName(Integer uid);

    @Query(nativeQuery = true, value =
        "select u.group_id from users u where u.id = :uid"
    )
    Optional<Integer> findManagedGroupId(Integer uid);

}
