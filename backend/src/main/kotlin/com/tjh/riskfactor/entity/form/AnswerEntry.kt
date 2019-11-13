package com.tjh.riskfactor.entity.form;

import com.tjh.riskfactor.entity.IEntity

import javax.persistence.*

@Entity @Table(name = "answer_entry")
class AnswerEntry(
    /**
     * null：无答案
     * 有内容：根据question-type来实际解析
     */
    @get:Column
    var value: String? = null
): IEntity() {
    @get:ManyToOne
    @get:JoinColumn(nullable = false)
    lateinit var question: Question

    @get:ManyToOne
    @get:JoinColumn(nullable = false)
    lateinit var answer: Answer
}
