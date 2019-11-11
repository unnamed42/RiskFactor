package com.tjh.riskfactor.error

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

fun notFound(field: String, vararg names: String) =
    ResponseStatusException(HttpStatus.NOT_FOUND,
        "requested ${field}(s) [${names.joinToString(", ")}] not found")

fun forbidden(message: String) =
    ResponseStatusException(HttpStatus.FORBIDDEN, message)

fun conflict(field: String, vararg names: String) =
    ResponseStatusException(HttpStatus.CONFLICT,
        "$field [${names.joinToString(", ")}}] already exists")

fun invalidArg(message: String) =
    ResponseStatusException(HttpStatus.BAD_REQUEST, message)

fun invalidArg(field: String, vararg values: String) =
    invalidArg("argument [$field] has invalid value(s) [${values.joinToString(", ")}}]")
