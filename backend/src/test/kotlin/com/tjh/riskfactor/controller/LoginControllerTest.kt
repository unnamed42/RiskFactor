package com.tjh.riskfactor.controller

import org.junit.jupiter.api.Test

import org.springframework.test.web.servlet.MockMvc
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

import com.tjh.riskfactor.extension.login
import com.tjh.riskfactor.extension.token

@SpringBootTest
@AutoConfigureMockMvc
class LoginControllerTest {

    @Test
    fun testLogin(@Autowired mvc: MockMvc) {
        // 不存在的用户
        mvc.login("what", "the-fuck").andExpect(status().isNotFound)
        // 密码错误
        mvc.login("admin", "a").andExpect(status().isUnauthorized)
        // 成功
        mvc.token("admin", "admin")
    }

}

