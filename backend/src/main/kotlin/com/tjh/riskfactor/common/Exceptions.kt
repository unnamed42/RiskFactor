package com.tjh.riskfactor.common

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

fun notFound(content: Any) =
    ResponseStatusException(HttpStatus.NOT_FOUND, content.toString())

fun notFound(entityName: String, argument: Any) =
    notFound("requested $entityName [$argument] not found")

fun forbidden(content: Any) =
    ResponseStatusException(HttpStatus.FORBIDDEN, content.toString())

fun invalidArg(content: Any) =
    ResponseStatusException(HttpStatus.BAD_REQUEST, content.toString())
