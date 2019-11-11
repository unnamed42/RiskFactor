package com.tjh.riskfactor.service

import org.springframework.stereotype.Service

import com.tjh.riskfactor.entity.Group
import com.tjh.riskfactor.repo.GroupRepository

@Service
class GroupService: IDBService<Group> {

    private lateinit var repo: GroupRepository

    /**
     * 获取用户组的所有用户的用户名
     * @param gid 用户组id
     * @return 用户组成员的用户名
     */
    fun memberNames(gid: Int) = repo.findMemberNames(gid)

    /**
     * 根据用户组名获取用户组
     * @param groupName 用户组名
     * @return 用户组
     */
    fun find(groupName: String) = repo.findByName(groupName)

    override val entityName = "group"
    override fun getRepo() = repo

}
