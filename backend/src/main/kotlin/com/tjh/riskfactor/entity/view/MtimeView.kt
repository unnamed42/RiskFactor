package com.tjh.riskfactor.entity.view

import com.fasterxml.jackson.annotation.JsonFormat

import java.util.Date

interface MtimeView {

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    fun getMtime(): Date

}
