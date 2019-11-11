package com.tjh.riskfactor.error

import com.fasterxml.jackson.annotation.JsonFormat

import java.util.Date

data class ApiError(
    val status: Int
) {
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    val timestamp: Date = Date()

    var error: String = ""
    var message: String = ""
    var uri: String = ""

    var body: String? = null
    var stacktrace: String? = null

}
