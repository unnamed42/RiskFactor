package com.tjh.riskfactor.entity.form

import com.fasterxml.jackson.annotation.JsonFormat

import com.tjh.riskfactor.entity.IEntity
import com.tjh.riskfactor.entity.User

import java.util.Date
import javax.persistence.*

@Entity @Table(name = "answer")
class Answer(
    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    var mtime: Date = Date()
): IEntity() {
    @ManyToOne
    @JoinColumn(nullable = false)
    lateinit var creator: User

    @ManyToOne
    @JoinColumn(nullable = false)
    lateinit var task: Task

    @OneToMany(mappedBy = "answer", cascade = [CascadeType.REMOVE])
    lateinit var answers: MutableSet<AnswerEntry>
}
