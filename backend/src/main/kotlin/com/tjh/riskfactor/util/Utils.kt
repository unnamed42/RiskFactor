package com.tjh.riskfactor.util

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

import com.tjh.riskfactor.error.invalidArg

var mapper = jacksonObjectMapper()

fun <V> require(map: Map<String, V>, key: String) =
    map[key] ?: throw invalidArg(key, "null")

fun toJson(obj: Any): String = mapper.writeValueAsString(obj)

fun fromJson(body: String): Map<String, Any> {
    val type = object: TypeReference<Map<String, Any>>() {}
    return mapper.readValue(body, type)
}

/**
 * 触发Hibernate中lazy fetch集合的获取过程
 */
fun <T: Collection<*>> T.fetchEager(): T {
    @Suppress("UNUSED_VARIABLE")
    val unused = this.size
    return this
}
