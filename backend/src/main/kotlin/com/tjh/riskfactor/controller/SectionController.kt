package com.tjh.riskfactor.controller

import org.springframework.web.bind.annotation.*

import com.tjh.riskfactor.service.SectionService

@CrossOrigin
@RestController
class SectionController(private val service: SectionService) {

    @GetMapping("/sections/{sid}")
    fun section(@PathVariable sid: Int) = service.checkedFind(sid)

}
