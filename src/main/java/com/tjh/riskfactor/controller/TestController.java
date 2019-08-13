package com.tjh.riskfactor.controller;

import lombok.val;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.web.bind.annotation.*;

import com.tjh.riskfactor.util.HttpUtils;
import com.tjh.riskfactor.util.JsonBuilder;

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
