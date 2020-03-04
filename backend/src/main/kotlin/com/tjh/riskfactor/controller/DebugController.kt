package com.tjh.riskfactor.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.security.access.prepost.PreAuthorize

import com.tjh.riskfactor.component.InitialDataLoader

@CrossOrigin
@RestController
@PreAuthorize("@e.isRoot()")
class DebugController(private val service: InitialDataLoader) {

    @GetMapping("/debug/tasks")
    @ResponseStatus(HttpStatus.OK)
    fun reloadQuestions() = service.reloadTask()

    @GetMapping("/debug/users")
    @ResponseStatus(HttpStatus.OK)
    fun reloadUsers() = service.reloadUsers()

}
