package com.tjh.riskfactor.controller;

import lombok.val;

import org.junit.Test;
import org.junit.Assert;

import org.mockito.Mock;
import org.mockito.InjectMocks;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.tjh.riskfactor.repo.UserRepository;

import java.util.Map;

public class AuthControllerTest extends RestTestBase {

    @Mock
    private UserRepository users;

    @InjectMocks
    private AuthController controller;

    @Value("${security.jwt.claimed-property}")
    private String claimedProperty;

    private static String login(String username, String password) {
        return String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);
    }

    @Test
    public void authFailure() throws Exception {
        // non-exist user
        val user = login("howcanthisnameexists", "whateverpassword");
        val request = MockMvcRequestBuilders.post("/auth")
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(user);
        mvc.perform(request).andExpect(
            MockMvcResultMatchers.status().isNotFound());

        // wrong password
        val user2 = login("admin", "admi");
        val request2 = MockMvcRequestBuilders.post("/auth")
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(user2);
        mvc.perform(request2).andExpect(
            MockMvcResultMatchers.status().isUnauthorized()
        );
    }

    private Map<String, Object> authToken() throws Exception {
        val user = login("admin", "admin");
        val request = MockMvcRequestBuilders.post("/auth")
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(user);
        val body = mvc.perform(request).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andReturn().getResponse().getContentAsString();
        return fromJson(body);
    }

    @Test
    public void authSuccess() throws Exception {
        val response = authToken();
        Assert.assertEquals("admin", response.get("username"));
        Assert.assertTrue(response.containsKey("token"));
    }

    @Test
    public void authInfo() throws Exception {
        val token = authToken().get("token");
        val request = MockMvcRequestBuilders.get("/auth")
                .header("Authorization", "Bearer " + token);
        val body = mvc.perform(request).andExpect(
            MockMvcResultMatchers.status().isOk()
        ).andReturn().getResponse().getContentAsString();

        val response = fromJson(body);
        Assert.assertTrue(response.containsKey("issued_at"));
        Assert.assertTrue(response.containsKey("expire_at"));
        Assert.assertTrue(response.containsKey(claimedProperty));
    }

}
