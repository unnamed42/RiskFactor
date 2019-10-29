//package com.tjh.riskfactor.controller;
//
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import com.tjh.riskfactor.util.JsonBuilder;
//
//@RestController
//@RequestMapping("/test")
//@PreAuthorize("hasAnyAuthority('root', 'admin')")
//public class TestController {
//
//    @GetMapping
//    String test(String name) {
//        return new JsonBuilder().add("lhs", "rhs").add("abc", new int[1])
//                .build();
//    }
////
////    @RequestMapping(value = "/{param}", method = RequestMethod.POST)
////    @PreAuthorize("hasAuthority('users')")
////    String validate(@PathVariable String param, @RequestBody JsonNode body) {
////        val value = HttpUtils.jsonNode(body, param).asText();
////        return new JsonBuilder().add("response", value).build();
////    }
//
//}
