package com.tjh.riskfactor.extension

import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

import com.tjh.riskfactor.common.parse
import com.tjh.riskfactor.common.toJson

fun MockMvc.login(username: String, password: String): ResultActions =
    this.perform(MockMvcRequestBuilders.post("/token").contentType(MediaType.APPLICATION_JSON)
        .content(LoginRequest(username, password).toJson()))

fun MockMvc.token(username: String, password: String) =
    this.login(username, password).andExpect(MockMvcResultMatchers.status().isOk).andReturn()
        .response.contentAsString.parse<LoginToken>()

data class LoginRequest(
    val username: String, val password: String
)

data class LoginToken(
    val token: String
)
