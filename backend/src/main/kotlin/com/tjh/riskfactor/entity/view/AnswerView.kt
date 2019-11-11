package com.tjh.riskfactor.entity.view

import com.fasterxml.jackson.annotation.JsonFormat

import java.util.Date

interface AnswerView {

    fun getId(): Int

    fun getCreator(): String

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    fun getMtime(): Date

}
