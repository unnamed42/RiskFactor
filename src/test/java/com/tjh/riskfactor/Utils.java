package com.tjh.riskfactor;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import java.util.HashMap;

public class Utils {

    public static Map<String, Object> fromJson(String body) throws JsonProcessingException {
        final var type = new TypeReference<HashMap<String, Object>>() {};
        return new ObjectMapper().readValue(body, type);
    }

    public static String toJson(String ...values) throws JsonProcessingException {
        final var map = new HashMap<String, String>();
        for(int i=0; i<values.length; i+=2)
            map.put(values[i], values[i+1]);
        return new ObjectMapper().writeValueAsString(map);
    }

    public static ResultActions login(MockMvc mvc, String username, String password) throws Exception {
        final var json = toJson("username", username, "password", password);
        final var request = MockMvcRequestBuilders.post("/auth").content(json)
                        .contentType(MediaType.APPLICATION_JSON);
        return mvc.perform(request);
    }

    public static String token(MockMvc mvc, String username, String password) throws Exception {
        final var response = login(mvc, username, password).andExpect(status().isOk())
                       .andReturn().getResponse().getContentAsString();
        final var json = fromJson(response);

        assertThat(json).containsKeys("token", "username");
        assertThat(json.get("username")).isEqualTo(username);
        return (String)json.get("token");
    }

}
