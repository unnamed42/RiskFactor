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
    private val questions: QuestionService,
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

        javaClass.getResourceAsStream("/data/task.yml").use { mapper.readValue(it, type) }.forEach { task ->
            // 带有ref的Question的ref -> Question的id
            val refQuestions = mutableMapOf<String, Int>()
            // 需要将placeholder中ref替换成具体id的Question的id
            val replaceQuestions = mutableListOf<Int>()
            // task下所有id -> Question
            val taskQuestions = mutableMapOf<Int, Question>()

            fun traverseQuestion(q: Question): Question {
                val empty = questions.save(Question())
                val nextId = empty.id
                taskQuestions[nextId] = q
                q.ref?.also { refQuestions[it] = nextId }
                q.options?.placeholder?.takeIf { it.contains(':') }?.also { replaceQuestions.add(nextId) }
                // 无视options，之后再更新
                return questions.save(empty.apply {
                    this.list = q.list?.map { traverseQuestion(it) }?.toMutableList()
                    this.label = q.label; this.type = q.type
                })
            }

            task.group = groups.findChecked(task.center)
            task.list = task.list.map { traverseQuestion(it) }.toMutableList()

            tasks.save(task)

            // 将placeholder中的ref引用替换成真正的id
            replaceQuestions.forEach {
                val entity = taskQuestions[it]!!
                val re = Regex("\\$(\\w+)")
                entity.options!!.placeholder = entity.options!!.placeholder!!.replace(re) { match ->
                    val (ref) = match.destructured
                    val id = refQuestions[ref] ?: throw RuntimeException("has no ref $ref")
                    "$$id"
                }
            }
            taskQuestions.entries.filter { (_, v) -> v.options != null }.forEach { (id, q) ->
                questions.updateChecked(id) { it.options = q.options }
            }
        }
    }

    @Transactional
    fun loadUsers() {
        val userListType = object: TypeReference<List<User>>() {}
        val groupListType = object: TypeReference<List<Group>>() {}

        javaClass.getResourceAsStream("/data/user.yml").use { stream ->
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

    private fun guarded(func: () -> Unit, guard: Int) {
        if(guards.existsById(guard))
            return
        func(); guards.insert(guard)
    }

    private fun <T> readTreeAs(tree: TreeNode, type: TypeReference<T>): T = mapper.readValue(
        mapper.treeAsTokens(tree), mapper.typeFactory.constructType(type)
    )
}
