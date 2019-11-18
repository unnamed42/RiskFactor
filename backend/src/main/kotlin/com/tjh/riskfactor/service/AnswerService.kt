package com.tjh.riskfactor.service

import org.apache.poi.ss.usermodel.Cell

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import com.tjh.riskfactor.entity.User
import com.tjh.riskfactor.entity.form.*
import com.tjh.riskfactor.error.notFound
import com.tjh.riskfactor.repo.AnswerEntryRepository
import com.tjh.riskfactor.repo.AnswerRepository
import com.tjh.riskfactor.util.ExcelReader

import mu.KotlinLogging

import java.io.InputStream
import java.lang.RuntimeException

private val logger = KotlinLogging.logger { }

@Service
class AnswerService(
    private val groups: GroupService,
    private val ansEntries: AnswerEntryRepository,
    override val repo: AnswerRepository
): IDBService<Answer>("answer") {

    /**
     * 获取回答的内容（不包含信息）
     * @param id 回答id
     * @return 回答的内容
     */
    fun answerBody(id: Int) = ansEntries.valueViewsOf(id).map {
        it.qid.toString() to it.value
    }.toMap()

    /**
     * 更新回答内容
     * @param id 回答id
     * @param body 有更新的内容
     */
    @Transactional
    fun updateAnswer(id: Int, body: Map<String, String>) {
        if(body.isEmpty())
            return
        body.entries.forEach{ (qid, value) ->
            ansEntries.putValue(id, qid.toInt(), value)
        }
    }

    fun export(id: Int) {}

    fun answersOfTask(taskId: Int) = repo.findAllByTaskId(taskId)

    fun answersOfTask(taskId: Int, groupId: Int) = repo.findAllByTaskIdCreatedBy(taskId, groups.memberNames(groupId))

    fun answersCreatedBy(taskId: Int, userId: Int) = repo.findTaskAnswersCreatedBy(taskId, userId)

    internal fun saveEntries(entries: Iterable<AnswerEntry>) = ansEntries.saveAll(entries)

    /**
     * 自Excel（xls，xlsx）格式导入回答（多个）
     * @param task 项目实体
     * @param creator 导入动作执行者
     * @param istream 上传文件的输入流
     * @return 导入完成的数据库实体
     */
    @Transactional
    fun importExcel(task: Task, creator: User, istream: InputStream) {
        // 问卷的总体结构 查找表
        // 问题所属大纲标题(String) -> Pair<大纲id, 问题标签(String) -> 问题(Question)>
        val layout = task.list.map {
            // TODO: error message
            val label = it.label ?: throw RuntimeException("")
            trim(label) to Pair(it, it.list.map {
                q -> q.label to q
            }.toMap())
        }.toMap()

        val answer = repo.save(Answer(task = task, creator = creator))

        ExcelReader(istream).use { reader ->
            val answers = reader.cells().map { dataCell ->
                val (h1, h2, h3, cell) = dataCell
                val title = "${trim(h1)}/${if (h2 == null) "" else trim(h2)}"
                val question = layout[title]?.second?.get(h3) ?: throw notFound("question", title)
                AnswerEntry(answer = answer, question = question,
                    value = formatValue(cell, question))
            }
            ansEntries.saveAll(answers.asIterable())
        }
    }

    @Transactional(readOnly = true)
    protected fun formatValue(cell: Cell?, question: Question): String? {
        if(cell == null)
            return null
        val content = cell.stringCellValue
        return when(question.type) {
            QuestionType.TEXT, QuestionType.NUMBER, QuestionType.SINGLE_CHOICE -> content
            QuestionType.DATE -> formatDate(content)
            else -> ""
        }
    }
}

/**
 * 删除问题标题中的（单位），比如将"年龄（岁）"变成"年龄"
 * 同时去除前置/后置空格
 */
private fun trim(str: String) =
    str.replace(Regex("（[^）]+）"), "").trim()

/**
 * 重新格式化医院提供数据中的日期。认为输入内容已经是合法日期，不对数字加以验证。
 * @param str 日期字符串。为 年.月.日 格式，但月和日可能0填充至两位数，也可能不是
 * @return 解析完成后的日期，格式为 yyyy-MM-dd
 */
private fun formatDate(str: String): String {
    var (y, m, d) = str.split('.')
    m = if (m.length == 1) "0$m" else m
    d = if (d.length == 1) "0$d" else d
    return "$y-$m-$d"
}
