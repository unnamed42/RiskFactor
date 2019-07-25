package com.tjh.riskfactor.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/history")
public class HistoryController {

    @RequestMapping(value = "{id}/{lhs}", method = RequestMethod.GET)
    public String get(@PathVariable int id, @PathVariable int lhs) {
        if (id != lhs)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return String.format("{\"%d\":\"%d\"}", id, lhs);
    }

}
