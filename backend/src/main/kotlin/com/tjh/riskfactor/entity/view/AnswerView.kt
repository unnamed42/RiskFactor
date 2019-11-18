package com.tjh.riskfactor.entity.view

import com.fasterxml.jackson.annotation.JsonFormat

import java.util.Date

interface AnswerView {
    val id: Int
    val creator: String
    @get:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val mtime: Date
}
