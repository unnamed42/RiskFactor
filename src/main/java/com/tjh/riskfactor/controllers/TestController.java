package com.tjh.riskfactor.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.tjh.riskfactor.utils.HttpUtils;
import com.tjh.riskfactor.utils.JsonBuilder;
import lombok.val;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestController {

    @RequestMapping(value = "", method = RequestMethod.GET)
    String test() {
        return new JsonBuilder().add("lhs", "rhs").add("abc", new int[1])
                .build();
    }

    @RequestMapping(value = "/{param}", method = RequestMethod.POST)
    String validate(@PathVariable String param, @RequestBody JsonNode body) {
        val value = HttpUtils.jsonNode(body, param).asText();
        return new JsonBuilder().add("response", value).build();
    }

}
