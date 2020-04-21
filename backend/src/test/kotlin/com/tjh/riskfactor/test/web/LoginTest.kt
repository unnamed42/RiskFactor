package com.tjh.riskfactor.test.web

import com.fasterxml.jackson.databind.ObjectMapper

import org.junit.jupiter.api.Test

import org.springframework.test.web.servlet.MockMvc
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@AutoConfigureMockMvc
class LoginTest: IRestApiTest {

    @Autowired override lateinit var mvc: MockMvc
    @Autowired override lateinit var mapper: ObjectMapper

    @Test
    fun testLogin() {
        // 不存在的用户
        this.login("what", "the-fuck").andExpect(status().isNotFound)
        // 密码错误
        this.login("admin", "a").andExpect(status().isUnauthorized)
        // 成功
        this.token("admin", "admin")
    }

}
