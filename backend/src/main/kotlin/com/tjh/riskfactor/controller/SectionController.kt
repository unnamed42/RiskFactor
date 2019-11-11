package com.tjh.riskfactor.controller

import org.springframework.web.bind.annotation.*
import org.springframework.beans.factory.annotation.Autowired

import com.tjh.riskfactor.service.SectionService

@CrossOrigin
@RestController
class SectionController {

    @Autowired private lateinit var service: SectionService

    @GetMapping("/sections/{sid}")
    fun section(@PathVariable sid: Int) = service.checkedFind(sid)

}
