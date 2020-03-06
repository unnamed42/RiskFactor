package com.tjh.riskfactor.api.schema

import org.springframework.web.bind.annotation.*

import com.tjh.riskfactor.common.IdType

@CrossOrigin
@RestController
class SchemaController(private val service: SchemaService) {

    @GetMapping("/schema/{schemaId}")
    fun getSchema(@PathVariable schemaId: IdType) =
        service.getSchema(schemaId)

}
