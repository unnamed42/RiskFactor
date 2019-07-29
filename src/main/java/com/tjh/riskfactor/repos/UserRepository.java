package com.tjh.riskfactor.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tjh.riskfactor.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
}
