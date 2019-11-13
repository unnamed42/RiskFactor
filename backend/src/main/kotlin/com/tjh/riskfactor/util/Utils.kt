package com.tjh.riskfactor.util

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

import com.tjh.riskfactor.error.invalidArg

private val mapper = jacksonObjectMapper()

fun <V> require(map: Map<String, V>, key: String) =
    map[key] ?: throw invalidArg(key, "null")

fun toJson(obj: Any): String = mapper.writeValueAsString(obj)

fun fromJson(body: String): Map<String, Any> {
    val type = object: TypeReference<Map<String, Any>>() {}
    return mapper.readValue(body, type)
}
