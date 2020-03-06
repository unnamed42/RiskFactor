package com.tjh.riskfactor.common

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory

import org.springframework.stereotype.Component
import org.springframework.boot.CommandLineRunner
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.transaction.annotation.Transactional

import com.tjh.riskfactor.api.account.AccountService
import com.tjh.riskfactor.api.console.ConsoleService
import com.tjh.riskfactor.api.schema.SchemaService

import java.io.InputStream


@Component
class DataLoader(
    private val accounts: AccountService,
    private val schemas: SchemaService,
    private val console: ConsoleService,
    builder: Jackson2ObjectMapperBuilder
): CommandLineRunner {

    private val mapper: ObjectMapper = builder.factory(YAMLFactory()).build()

    @Transactional
    override fun run(vararg args: String?) {
        onceOnly("users") {
            accounts.loadFromSchema(mapper.parse(it))
        }
        onceOnly("rules") {
            schemas.loadFromSchema(mapper.parse(it))
        }
    }

    private inline fun onceOnly(onceKey: String, action: (InputStream) -> Unit) {
        if (console[onceKey] != null)
            return
        javaClass.getResourceAsStream("/data/$onceKey.yml").use(action)
        console[onceKey] = "1"
    }

    private inline fun <reified T> ObjectMapper.parse(input: InputStream): T {
        val type = object: TypeReference<T>() {}
        return this.readValue(input, type)
    }

}


//
//@Component
//class InitialDataLoader(
//    private val accounts: AccountService,
//    private val questions: QuestionService,
//    private val console: ConsoleService,
//    builder: Jackson2ObjectMapperBuilder
//): CommandLineRunner {
//
//    private val mapper: ObjectMapper = builder.factory(YAMLFactory()).build()
//
//    @Transactional
//    override fun run(vararg args: String?) {
//        onceOnly("users") {
//            accounts.loadFromSchema(mapper.parse(it))
//        }
//        onceOnly("rules") {
//            questions.loadFromSchema(mapper.parse(it))
//        }
//    }
//
//    private inline fun onceOnly(onceKey: String, action: (InputStream) -> Unit) {
//        if(console[onceKey] != null)
//            return
//        javaClass.getResourceAsStream("/data/$onceKey.yml").use(action)
//        console[onceKey] = "1"
//    }
//
////    @Transactional
////    fun loadTasks() {
////        val type = object: TypeReference<List<Task>>() {}
////
////        javaClass.getResourceAsStream("/data/yml").use { mapper.readValue(it, type) }.forEach { task ->
////            // 带有ref的Question的ref -> Question的id
////            val refQuestions = mutableMapOf<String, Int>()
////            // 需要将placeholder中ref替换成具体id的Question的id
////            val replaceQuestions = mutableListOf<Int>()
////            // task下所有id -> Question
////            val taskQuestions = mutableMapOf<Int, Question>()
////
////            fun traverseQuestion(q: Question): Question {
////                // 存储空对象是为了获得一个id
////                val empty = questions.save(Question())
////                val nextId = empty.id
////                taskQuestions[nextId] = q
////                // 记录带有ref的Question，之后替换成真正的id
////                q.ref?.also { refQuestions[it] = nextId }
////                // 表达式类型的placeholder，记录具有此类特征的Question的id，之后将操作数ref名替换成真正的id
////                q.options?.placeholder?.takeIf { it.startsWith("expr:") }?.also { replaceQuestions.add(nextId) }
////                // 由于存在ref的前后依赖关系不能直接在此替换option内容，当所有Question被赋予一个id之后再进行真正的替换
////                // 由于这个原因，在此忽略options值，在所有id赋予完成之后更新
////                return questions.save(empty.apply {
////                    this.list = q.list?.map { traverseQuestion(it) }?.toMutableList()
////                    this.label = q.label; this.type = q.type
////                })
////            }
////
////            task.group = groups.findByName(task.center) ?: throw groups.notFound(task.center)
////            task.list = task.list.map { traverseQuestion(it) }.toMutableList()
////
////            tasks.save(task)
////
//            // 将placeholder中的ref引用替换成真正的id。格式为 $id
//            replaceQuestions.forEach {
//                val entity = taskQuestions[it]!!
//                val re = Regex("\\$(\\w+)")
//                entity.options!!.placeholder = entity.options!!.placeholder!!.replace(re) { match ->
//                    val (ref) = match.destructured
//                    val id = refQuestions[ref] ?: throw RuntimeException("has no ref $ref")
//                    "$$id"
//                }
//            }
////            // 存储options
////            taskQuestions.entries.filter { (_, v) -> v.options != null }.forEach { (id, q) ->
////                questions.updateChecked(id) { it.options = q.options }
////            }
////        }
////    }
////
//}
//
//private inline fun <reified T> ObjectMapper.parse(input: InputStream): T {
//    val type = object: TypeReference<T>() {}
//    return this.readValue(input, type)
//}

