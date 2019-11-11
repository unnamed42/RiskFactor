package com.tjh.riskfactor.service

import com.fasterxml.jackson.core.TreeNode
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory

import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

import com.tjh.riskfactor.entity.*
import com.tjh.riskfactor.entity.form.*
import com.tjh.riskfactor.error.ResponseErrors.notFound
import com.tjh.riskfactor.repo.SaveGuardRepository

@Service
class DataService {

    @Autowired private lateinit var tasks: TaskService
    @Autowired private lateinit var users: UserService
    @Autowired private lateinit var groups: GroupService
    @Autowired private lateinit var guards: SaveGuardRepository

    companion object {
        private val mapper = ObjectMapper(YAMLFactory())

        private fun <T> readTreeAs(tree: TreeNode, type: TypeReference<T>): T = mapper.readValue(
            mapper.treeAsTokens(tree), mapper.typeFactory.constructType(type)
        )
    }

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
        val type = object: TypeReference<Task>() {}
        TypeReference::class.java.getResourceAsStream("/data/task.yml").use { stream ->
            val task = mapper.readValue(stream, type)
            val group = groups.find(task.center) ?: throw notFound("group", task.center)
            val sections = tasks.saveSections(task.sections.map { prepareSection(it) })
            tasks.save(task.apply {
                this.group = group; this.sections = sections
            })
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
                users.encoded(it.apply { group = groups[groupName] })
            })
        }
    }

    private fun prepareQuestion(q: Question): Question = q.apply {
        q.list = q.list?.let {
            tasks.saveQuestions(q.list.map { prepareQuestion(it) })
        }
    }

    private fun prepareSection(s: Section): Section = s.apply {
        s.questions = s.questions?.let {
            tasks.saveQuestions(s.questions.map { prepareQuestion(it) })
        }
    }

    private fun guarded(func: () -> Unit, guard: Int) {
        if(!guards.existsById(guard)) {
            func(); guards.insert(guard)
        }
    }

}
