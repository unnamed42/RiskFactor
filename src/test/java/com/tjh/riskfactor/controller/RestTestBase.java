package com.tjh.riskfactor.controller;

import lombok.val;

import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;

import org.mockito.MockitoAnnotations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public abstract class RestTestBase {

    MockMvc mvc;

    @Autowired
    protected WebApplicationContext context;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    static <T> String toJson(T object) throws IOException {
        return new ObjectMapper().writeValueAsString(object);
    }

    static Map<String, Object> fromJson(String json) throws IOException {
        val typeRef = new TypeReference<HashMap<String, Object>>() {};
        return new ObjectMapper().readValue(json, typeRef);
    }

    ResultActions login(String username, String password) throws Exception {
        val user = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);
        val request = MockMvcRequestBuilders.post("/auth")
                .contentType(MediaType.APPLICATION_JSON).content(user);
        return mvc.perform(request);
    }

    Map<String, Object> token(String username, String password) throws Exception {
        val response = login(username, password).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        val json = fromJson(response);
        Assert.assertEquals(username, json.get("username"));
        Assert.assertTrue(json.containsKey("token"));
        return json;
    }

}
