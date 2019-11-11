package com.tjh.riskfactor.entity.form;

import com.tjh.riskfactor.entity.IEntity

import javax.persistence.*

@Entity @Table(name = "answer_entry")
class AnswerEntry(
    /**
     * null：无答案
     * 有内容：根据question-type来实际解析
     */
    var value: String? = null
): IEntity() {
    @ManyToOne
    @JoinColumn(nullable = false)
    lateinit var question: Question

    @ManyToOne
    @JoinColumn(nullable = false)
    lateinit var answer: Answer
}
