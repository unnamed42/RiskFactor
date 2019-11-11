package com.tjh.riskfactor.repo

import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.JpaRepository

import com.tjh.riskfactor.entity.Group

@Repository
interface GroupRepository: JpaRepository<Group, Int> {

    fun findByName(name: String): Group?

    @Query("select m.username from Group g inner join g.members m on g.id = :gid")
    fun findMemberNames(gid: Int): List<String>

}
