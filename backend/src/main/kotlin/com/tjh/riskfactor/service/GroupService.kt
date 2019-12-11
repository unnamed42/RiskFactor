package com.tjh.riskfactor.service

import org.springframework.stereotype.Service

import com.tjh.riskfactor.entity.Group
import com.tjh.riskfactor.error.notFound
import com.tjh.riskfactor.repo.GroupRepository

@Service
class GroupService(override val repo: GroupRepository): IDBService<Group>("group") {
    fun find(groupName: String) = repo.findByName(groupName)
    fun findChecked(groupName: String) = find(groupName) ?: throw notFound("group", groupName)
}
