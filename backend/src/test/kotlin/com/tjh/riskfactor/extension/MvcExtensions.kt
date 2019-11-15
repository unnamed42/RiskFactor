package com.tjh.riskfactor.extension

import org.assertj.core.api.Assertions.assertThat

import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

import com.tjh.riskfactor.util.toJson
import com.tjh.riskfactor.util.fromJson

fun MockMvc.login(username: String, password: String): ResultActions =
    this.perform(MockMvcRequestBuilders.post("/login").contentType(MediaType.APPLICATION_JSON)
        .content(toJson(mapOf("username" to username, "password" to password)))
    )

fun MockMvc.token(username: String, password: String) =
    this.login(username, password).andExpect(MockMvcResultMatchers.status().isOk).andReturn()
        .let { fromJson(it.response.contentAsString) }.also { assertThat(it).containsKey("token") }
        .let { it["token"] as String }
