package com.tjh.riskfactor.controller

import org.junit.jupiter.api.Test

import org.springframework.test.web.servlet.MockMvc
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

import com.tjh.riskfactor.TestBase

class LoginControllerTest: TestBase() {

    @Test
    fun testLogin(@Autowired mvc: MockMvc) {
        // 不存在的用户
        login(mvc, "what", "the-fuck").andExpect(status().isNotFound)
        // 密码错误
        login(mvc, "admin", "a").andExpect(status().isUnauthorized)
        // 成功
        token(mvc, "admin", "admin")
    }

}

