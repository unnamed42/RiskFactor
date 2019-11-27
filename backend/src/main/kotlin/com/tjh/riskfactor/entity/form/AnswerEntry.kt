package com.tjh.riskfactor.entity.form;

import com.tjh.riskfactor.entity.IEntity

import javax.persistence.*

@Entity
@Table(name = "answer_entry",
    uniqueConstraints = [UniqueConstraint(columnNames = ["question_id", "answer_id"])])
class AnswerEntry(
    /**
     * null：无答案
     * 有内容：根据question-type来实际解析
     */
    @get:Column
    var value: String,

    /**
     * 可重复回答的问题的排号index，不一定连续，组成数组时需要排序
     */
    @get:Column
    var index: Int? = null,

    /**
     * 回答的问题的id
     */
    @get:ManyToOne
    @get:JoinColumn(nullable = false, name = "question_id")
    var question: Question,

    @get:ManyToOne
    @get:JoinColumn(nullable = false, name = "answer_id")
    var answer: Answer
): IEntity()
