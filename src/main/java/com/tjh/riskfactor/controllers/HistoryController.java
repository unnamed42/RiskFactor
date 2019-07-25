package com.tjh.riskfactor.controllers;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/history")
public class HistoryController {

    @RequestMapping(value = "{id}/{lhs}", method = RequestMethod.GET)
    public String get(@PathVariable int id, @PathVariable int lhs) {
        
    }

}
