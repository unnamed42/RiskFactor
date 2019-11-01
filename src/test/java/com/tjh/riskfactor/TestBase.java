package com.tjh.riskfactor;

import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static com.tjh.riskfactor.util.Utils.fromJson;
import static com.tjh.riskfactor.util.Utils.kvMap;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public abstract class TestBase {

    protected static ResultActions login(MockMvc mvc, String username, String password) throws Exception {
        final var json = kvMap("username", username).add("password", password).buildJson();
        final var request = MockMvcRequestBuilders.post("/login").content(json.get())
                            .contentType(MediaType.APPLICATION_JSON);
        return mvc.perform(request);
    }

    protected static String token(MockMvc mvc, String username, String password) throws Exception {
        final var response = login(mvc, username, password).andExpect(status().isOk())
                             .andReturn().getResponse().getContentAsString();
        final var json = fromJson(response);

        assertThat(json).containsKeys("token");
        return (String)json.get("token");
    }

}
