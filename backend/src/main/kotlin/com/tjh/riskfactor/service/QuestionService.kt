package com.tjh.riskfactor.service

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.stereotype.Service

import com.tjh.riskfactor.entity.form.Question
import com.tjh.riskfactor.entity.form.QuestionType
import com.tjh.riskfactor.error.notFound
import com.tjh.riskfactor.repo.QuestionRepository

@Service
class QuestionService(override val repo: QuestionRepository): IDBService<Question>("question") {

    fun typeOf(id: Int) = repo.typeOf(id)

    /**
     * 描述Section（type为header的Question）的简略情况，即给出名称（label）
     * 如果其下属list还有Section，返回对象的list中也包含下属Section的简略情况
     * 如果不是Section，返回null
     * @param id Question的id
     */
    fun describeSection(id: Int): SectionResponse? {
        val view = repo.questionView(id) ?: throw notFound(entityName, id.toString())
        if(view.type != QuestionType.HEADER)
            return null
        val ids = repo.listIdsOf(id)
        val ret = SectionResponse(view.label ?: "")
        return if(ids.isEmpty() || repo.typeOf(ids.first()) != QuestionType.HEADER)
            ret
        else
            ret.apply { list = ids.mapNotNull { describeSection(it) } }
    }

    fun listIdsOfTask(taskId: Int) = repo.listIdsOfTask(taskId)

    class SectionResponse(
        val name: String
    ) {
        @get:JsonInclude(JsonInclude.Include.NON_EMPTY)
        var list: List<SectionResponse> = emptyList()
    }

}
