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

import com.tjh.riskfactor.json.AuthInfo;
import com.tjh.riskfactor.repo.UserRepository;

import java.util.Map;

public class AuthControllerTest extends RestTestBase {

    @Mock
    private UserRepository users;

    @InjectMocks
    private AuthController controller;

    @Value("${security.jwt.claimed-property}")
    private String claimedProperty;

    @Test
    public void authFailure() throws Exception {
        val user = new AuthInfo("howcanthisnameexists", "whateverpassword");
        val request = MockMvcRequestBuilders.post("/auth")
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(toJson(user));
        mvc.perform(request).andExpect(
            MockMvcResultMatchers.status().isNotFound());
    }

    private Map<String, Object> authToken() throws Exception {
        val user = new AuthInfo("admin", "admin");
        val request = MockMvcRequestBuilders.post("/auth")
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(toJson(user));
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
        Assert.assertTrue(response.containsKey("expiry"));
        Assert.assertTrue(response.containsKey(claimedProperty));
    }

}
