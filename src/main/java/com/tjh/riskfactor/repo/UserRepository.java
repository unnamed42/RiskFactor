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

}
