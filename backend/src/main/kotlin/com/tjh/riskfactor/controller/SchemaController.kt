package com.tjh.riskfactor.controller

import org.springframework.web.bind.annotation.*

import com.tjh.riskfactor.service.*
import com.tjh.riskfactor.repository.*

@CrossOrigin
@RestController
class SchemaController(private val service: SchemaService) {

    @GetMapping("/schema")
    fun getAllSchema(): List<SchemaInfo> =
        service.getSchemas()

    @GetMapping("/schema/{schemaId}/modifiedAt")
    fun schemaMtime(@PathVariable schemaId: IdType): EpochTime =
        service.schemas.propertyOf(schemaId) { modifiedAt }

    @GetMapping("/schema/{schemaId}")
    fun getSchema(@PathVariable schemaId: IdType): SchemaInfo =
        service.getSchema(schemaId)

    @GetMapping("/schema/{schemaId}/detail")
    fun getSchemaDetail(@PathVariable schemaId: IdType): SchemaDetail =
        service.getSchemaDetail(schemaId)

}
