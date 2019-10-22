package com.tjh.riskfactor.controller;

import lombok.val;

import org.junit.Before;
import org.junit.runner.RunWith;

import org.mockito.MockitoAnnotations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

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

}
