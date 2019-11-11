package com.tjh.riskfactor

import org.assertj.core.api.Assertions.assertThat

import org.junit.jupiter.api.extension.ExtendWith

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

import com.tjh.riskfactor.util.fromJson
import com.tjh.riskfactor.util.toJson

@SpringBootTest
@ExtendWith(SpringExtension::class)
@AutoConfigureMockMvc
class TestBase {

    protected fun login(mvc: MockMvc, username: String, password: String) =
        mvc.perform(MockMvcRequestBuilders.post("/login").contentType(MediaType.APPLICATION_JSON)
            .content(toJson(mapOf("username" to username, "password" to password)))
        )

    protected fun token(mvc: MockMvc, username: String, password: String): String {
        val resp = login(mvc, username, password).andExpect(status().isOk).andReturn()
        val json = fromJson(resp.response.contentAsString); assertThat(json).containsKey("token")
        return json["token"] as String
    }

}
