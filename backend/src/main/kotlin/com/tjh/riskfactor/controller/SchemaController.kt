package com.tjh.riskfactor.controller

import com.tjh.riskfactor.service.SchemaInfo
import com.tjh.riskfactor.service.SchemaRules
import com.tjh.riskfactor.service.SchemaService
import org.springframework.web.bind.annotation.*

import com.tjh.riskfactor.repository.IdType
import com.tjh.riskfactor.repository.EpochTime
import com.tjh.riskfactor.repository.propertyOf

@CrossOrigin
@RestController
class SchemaController(private val service: SchemaService) {

    @GetMapping("/schema")
    fun getAllSchema(): List<SchemaInfo> =
        service.getSchemas()

    @GetMapping("/schema/{schemaId}/modifiedAt")
    fun schemaUpdated(@PathVariable schemaId: IdType): EpochTime =
        service.schemas.propertyOf(schemaId) { modifiedAt }

    @GetMapping("/schema/{schemaId}")
    fun getSchema(@PathVariable schemaId: IdType): SchemaInfo =
        service.getSchema(schemaId)

    @GetMapping("/schema/{schemaId}/rules")
    fun getSchemaRules(@PathVariable schemaId: IdType): SchemaRules =
        service.getSchemaRules(schemaId)

}
