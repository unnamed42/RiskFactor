package com.tjh.riskfactor.repo;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tjh.riskfactor.entity.SaveGuard;

@Repository
public interface SaveGuardRepository extends JpaRepository<SaveGuard, Integer> {

    @Modifying
    @Query(value = "insert into save_guard(id) value (:id)", nativeQuery = true)
    void insert(int id);

}
