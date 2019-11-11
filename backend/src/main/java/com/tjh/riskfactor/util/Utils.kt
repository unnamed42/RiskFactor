package com.tjh.riskfactor.util

import com.tjh.riskfactor.error.ResponseErrors.invalidArg

fun <V> require(map: Map<String, V>, key: String) =
    map[key] ?: throw invalidArg(key, "null")
