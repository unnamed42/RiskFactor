package com.tjh.riskfactor.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

import com.tjh.riskfactor.entity.SaveGuard

@Repository
interface SaveGuardRepository : JpaRepository<SaveGuard, Int> {
    @Modifying
    @Query(value = "insert into save_guard(id) value (:id)", nativeQuery = true)
    fun insert(id: Int)
}
