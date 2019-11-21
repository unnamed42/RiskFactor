package com.tjh.riskfactor.service

import com.fasterxml.jackson.core.TreeNode
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

import com.tjh.riskfactor.entity.*
import com.tjh.riskfactor.entity.form.*
import com.tjh.riskfactor.repo.SaveGuardRepository

@Service
class DataService(
    private val tasks: TaskService,
    private val users: UserService,
    private val groups: GroupService,
    private val guards: SaveGuardRepository,
    builder: Jackson2ObjectMapperBuilder
) {

    private val mapper = builder.factory(YAMLFactory()).build<ObjectMapper>()

    @Transactional
    fun reloadTask() {
        tasks.drop(); guards.deleteById(0); loadTasks()
    }

    @Transactional
    fun reloadUsers() {
        groups.drop(); users.drop()
        guards.deleteById(1); loadUsers()
    }

    @Transactional
    fun init() {
        guarded(this::loadUsers, 1)
        guarded(this::loadTasks, 0)
    }

    @Transactional
    fun loadTasks() {
        val type = object: TypeReference<List<Task>>() {}
        TypeReference::class.java.getResourceAsStream("/data/task.yml").use { stream ->
            mapper.readValue(stream, type).forEach{ task ->
                val group = groups.findChecked(task.center)
                val list = tasks.saveQuestions(task.list.map { prepareQuestion(it) })
                tasks.save(task.apply {
                    this.group = group; this.list = list
                })
            }
        }
    }

    @Transactional
    fun loadUsers() {
        val userListType = object: TypeReference<List<User>>() {}
        val groupListType = object: TypeReference<List<Group>>() {}

        TypeReference::class.java.getResourceAsStream("/data/user.yml").use { stream ->
            val node = mapper.readTree(stream); assert(node.isObject)
            val root = node as ObjectNode

            val userList = readTreeAs(root.get("users"), userListType)
            val groupList = readTreeAs(root.get("groups"), groupListType)

            val groups = this.groups.saveAll(groupList).associateBy { it.name }
            this.users.saveAll(userList.map {
                users.encoded(it.apply { group = groups[groupName] ?: throw Exception("group $groupName not found") })
            })
        }
    }

    private fun prepareQuestion(q: Question): Question = q.apply {
        list = tasks.saveQuestions(list.map { prepareQuestion(it) })
    }

    private fun guarded(func: () -> Unit, guard: Int) {
        if(guards.existsById(guard))
            return
        func(); guards.insert(guard)
    }

    private fun <T> readTreeAs(tree: TreeNode, type: TypeReference<T>): T = mapper.readValue(
        mapper.treeAsTokens(tree), mapper.typeFactory.constructType(type)
    )
}
