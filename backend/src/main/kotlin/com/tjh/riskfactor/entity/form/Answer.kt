package com.tjh.riskfactor.entity.form

import com.fasterxml.jackson.annotation.JsonFormat

import com.tjh.riskfactor.entity.IEntity
import com.tjh.riskfactor.entity.User

import java.util.Date
import javax.persistence.*

@Entity @Table(name = "answer")
class Answer(
    @get:Column(nullable = false)
    @get:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    var mtime: Date = Date()
): IEntity() {
    @get:ManyToOne
    @get:JoinColumn(nullable = false)
    lateinit var creator: User

    @get:ManyToOne
    @get:JoinColumn(nullable = false)
    lateinit var task: Task

    @get:OneToMany(mappedBy = "answer", cascade = [CascadeType.REMOVE])
    lateinit var answers: MutableSet<AnswerEntry>
}
