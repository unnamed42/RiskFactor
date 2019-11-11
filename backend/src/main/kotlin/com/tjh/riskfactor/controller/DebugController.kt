package com.tjh.riskfactor.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize

import com.tjh.riskfactor.service.DataService

@CrossOrigin
@RestController
@PreAuthorize("@e.isRoot()")
class DebugController {

    @Autowired private lateinit var service: DataService

    @GetMapping("/debug/tasks")
    @ResponseStatus(HttpStatus.OK)
    fun reloadQuestions() = service.reloadTask()

    @GetMapping("/debug/users")
    @ResponseStatus(HttpStatus.OK)
    fun reloadUsers() = service.reloadUsers()

}