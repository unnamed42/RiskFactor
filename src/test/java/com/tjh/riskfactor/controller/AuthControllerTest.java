package com.tjh.riskfactor.controller;

import lombok.val;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tjh.riskfactor.TestBase;
import static com.tjh.riskfactor.Utils.*;

public class AuthControllerTest extends TestBase {

    @Value("${security.jwt.claimed-property}")
    private String claimed;

    @Test
    void testLogin(@Autowired MockMvc mvc) throws Exception {
        // 不存在的用户
        login(mvc, "what", "the-fuck").andExpect(status().isNotFound());
        // 密码错误
        login(mvc, "admin", "a").andExpect(status().isUnauthorized());
        // 成功
        token(mvc, "admin", "admin");
    }

    @Test
    void testAuthInfo(@Autowired MockMvc mvc) throws Exception {
        // 未登录
        mvc.perform(get("/auth")).andExpect(status().isUnauthorized());
        // 不合法验证信息
        mvc.perform(get("/auth").header(AUTHORIZATION, "")).andExpect(status().isUnauthorized());
        mvc.perform(get("/auth").header(AUTHORIZATION, "Bearer")).andExpect(status().isUnauthorized());
        mvc.perform(get("/auth").header(AUTHORIZATION, "Bearer ")).andExpect(status().isUnauthorized());
        mvc.perform(get("/auth").header(AUTHORIZATION, "Bearer 123456")).andExpect(status().isUnauthorized());
        // 登录用户
        val req = get("/auth").header(AUTHORIZATION, "Bearer " + token(mvc, "admin", "admin"));
        val json = mvc.perform(req).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertThat(fromJson(json)).containsKeys("issued_at", "expire_at", claimed);
    }

}
