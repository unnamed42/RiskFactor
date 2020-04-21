package com.tjh.riskfactor.test.web

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.assertDoesNotThrow

import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

interface IRestApiTest {
    val mvc: MockMvc
    val mapper: ObjectMapper
}

fun IRestApiTest.login(username: String, password: String): ResultActions =
    mvc.perform(MockMvcRequestBuilders.post("/token").contentType(MediaType.APPLICATION_JSON)
       .content(mapper.serialize(LoginRequest(username, password))))

fun IRestApiTest.token(username: String, password: String): String {
    val login = this.login(username, password).andExpect(MockMvcResultMatchers.status().isOk).andReturn()
    val token = assertDoesNotThrow { mapper.readValue(login.response.contentAsByteArray, LoginToken::class.java) }
    return token.token
}

private fun ObjectMapper.serialize(obj: Any): String =
    this.writeValueAsString(obj)

data class LoginRequest(
    val username: String, val password: String
)

data class LoginToken(
    val token: String
)
