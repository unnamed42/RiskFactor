package com.tjh.riskfactor.entity.view

import com.fasterxml.jackson.annotation.JsonFormat

import java.util.Date

interface TaskView {
    fun getId(): Int
    fun getName(): String
    fun getCenter(): String
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    fun getMtime(): Date
}
