package com.tjh.riskfactor.entity.view

import com.fasterxml.jackson.annotation.JsonFormat

import java.util.Date

interface TaskView {
    val id: Int
    val name: String
    val center: String
    @get:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val mtime: Date
}
