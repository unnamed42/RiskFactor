package com.tjh.riskfactor.common

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

val mapper = jacksonObjectMapper()

/**
 * Unicode U200B Zero-Width Space
 */
const val separator = "\u200B"

/**
 * 将bool值转换成404或200
 */
fun Boolean.to404() =
    ResponseEntity<Any>(if (this) HttpStatus.OK else HttpStatus.NOT_FOUND)

operator fun Int.plus(bool: Boolean): Int =
    this + (if(bool) 1 else 0)

inline fun <T> T.applyIf(bool: Boolean, block: T.() -> Unit): T {
    if(bool) this.block()
    return this
}
