package com.tjh.riskfactor.controller

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonUnwrapped

import org.springframework.web.bind.annotation.*

import com.tjh.riskfactor.service.*
import com.tjh.riskfactor.repository.*

@CrossOrigin
@RestController
class SchemaController(
    private val service: SchemaService,
    private val accountService: AccountService
) {

    /**
     * 获得全部问卷的简略信息
     */
    @GetMapping("/schema")
    fun getAllSchema(): List<SchemaInfo> =
        service.getSchemas().map { it.toInfo() }

    /**
     * 检查问卷是否更新
     */
    @GetMapping("/schema/{schemaId}/modifiedAt")
    fun schemaMtime(@PathVariable schemaId: IdType): EpochTime =
        service.schemas.propertyOf(schemaId) { modifiedAt }

    /**
     * 获得指定问卷的简略信息
     */
    @GetMapping("/schema/{schemaId}")
    fun getSchema(@PathVariable schemaId: IdType): SchemaInfo =
        service.getSchema(schemaId).toInfo()

    /**
     * 获得指定问卷的完整信息
     */
    @GetMapping("/schema/{schemaId}/detail")
    fun getSchemaDetail(@PathVariable schemaId: IdType): SchemaDetail {
        val (schema, rules) = service.getSchemaDetail(schemaId)
        return SchemaDetail(schema.toInfo(), rules)
    }

    private fun Schema.toInfo(): SchemaInfo = SchemaInfo(
        id = id, name = name, modifiedAt = modifiedAt, createdAt = createdAt,
        creator = accountService.userInfo(creatorId)
    )

}

data class SchemaInfo(
    val id: IdType,
    val name: String,
    val creator: UserInfo,
    val modifiedAt: EpochTime,
    val createdAt: EpochTime
)

data class SchemaDetail(
    @JsonUnwrapped
    val info: SchemaInfo,
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val rules: List<RuleInfo>
)
