package com.tjh.riskfactor.controller;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tjh.riskfactor.TestBase;
import static com.tjh.riskfactor.util.Utils.fromJson;

class LoginControllerTest extends TestBase {

    @Test
    void testLogin(@Autowired MockMvc mvc) throws Exception {
        // 不存在的用户
        login(mvc, "what", "the-fuck").andExpect(status().isNotFound());
        // 密码错误
        login(mvc, "admin", "a").andExpect(status().isUnauthorized());
        // 成功
        token(mvc, "admin", "admin");
    }

}
