package com.tjh.riskfactor.service

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonUnwrapped

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import com.tjh.riskfactor.common.*
import com.tjh.riskfactor.repository.*

import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmErasure

@Service
class SchemaService(
    val rules: RuleRepository,
    val schemas: SchemaRepository
) {

    fun getSchemas(): List<Schema> =
        schemas.findAll(Schema::modifiedAt.sorted())

    fun getSchema(schemaId: IdType): Schema =
        schemas.find(schemaId)

    private fun getRuleInfo(ruleId: IdType): RuleInfo {
        val rule = rules.find(ruleId)
        val options = RuleAttributes.fromAttributeList(rules.findAttributes(ruleId))
        val list = rules.findList(ruleId).map { getRuleInfo(it) }
        return RuleInfo(id = rule.id, label = rule.label,
            type = rule.type, options = options, list = list)
    }

    /**
     * 获得问卷的结构和初始化数据。所有的问卷构建规则id都已经转化成"$${id}"的格式
     */
    @Transactional(readOnly = true)
    fun getSchemaDetail(schemaId: IdType): Pair<Schema, List<RuleInfo>> {
        val schema = schemas.find(schemaId)
        val rules = getRuleInfo(schema.rootObjectId).list ?:
            throw Exception("schema $schemaId has no rules configured")

        return Pair(schema, rules)
    }

}

data class RuleInfo(
    val id: IdType,
    val type: RuleType?,
    val label: String?,
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val list: List<RuleInfo>?,
    @field:JsonUnwrapped
    val options: RuleAttributes?
)

data class RuleAttributes(
    /**
     * 指定是否为必填
     */
    var required: Boolean? = null,

    /**
     * 指定该问题的输入能否控制后续问题的显示。用于指定联动问题，即只有这个问题输入之后才能显示后续问题
     */
    var isEnabler: Boolean? = null,

    /**
     * 指定多选/下拉类控件的最后是否有一个“其他”用于输入自定义选项内容
     */
    var customizable: Boolean? = null,

    /**
     * 指定多选/下拉类控件的选项
     *
     * 如果需要给选项指定筛选键，用 &#91;内容&#93;/&#91;筛选键&#93; 格式
     */
    var choices: List<String>? = null,

    /**
     * 问题标签位置，仅供输入类组件使用，表示标签的位置
     * 值的含义同[addonPosition]
     */
    var labelPosition: String? = null,

    /**
     * 指定[RuleType.YESNO_CHOICE]问题的选项内容
     *
     * 格式：&#91;是含义]/&#91;否含义]
     */
    var yesno: String? = null,

    /**
     * 指定该问题是问题组长的前置/后置输入控件，比如 &#91;输入持续时间（整数）]&#91;单位选择（天/月）]，后者是前者的后置输入控件
     *
     * 该域的含义：
     *   null,: 无效果（默认）
     *   prefix: 前置输入
     *   postfix: 后置输入
     */
    var addonPosition: String? = null,

    /**
     * 问题的初始值
     *
     * 对于单项选择来说，只有一个条目；对于多项选择来说，有多个条目，由半角逗号分隔
     */
    var init: String? = null,

    /**
     * 额外说明
     */
    var description: String? = null,

    /**
     * 占位文字，一般是输入类控件需要
     *
     * 对于[RuleType.EXPR]类型来说，则是填充字符串。不使用[init]是因为初始值原则上是不变化的
     */
    var placeholder: String? = null
) {
    companion object {
        val reflection = RuleAttributes::class.declaredMemberProperties
            .map { it.name to it }.toMap()

        /**
         * 将key(string) -> value(String)转换为[RuleAttributes]
         */
        private fun fromAttributeList(pairs: Sequence<Pair<String, String>>): RuleAttributes {
            val result = RuleAttributes()
            for((key, value) in pairs) {
                val prop = reflection[key] as? KMutableProperty1 ?: continue
                @Suppress("UNCHECKED_CAST")
                if(prop.returnType.jvmErasure.isSubclassOf(List::class)) {
                    val setValue = value.split(separator)
                    (prop as KMutableProperty1<RuleAttributes, List<*>>).set(result, setValue)
                } else
                    (prop as KMutableProperty1<RuleAttributes, String>).set(result, value)
            }
            return result
        }

        fun fromAttributeList(attrs: List<RuleAttribute>): RuleAttributes? = if(attrs.isEmpty()) null else
            attrs.asSequence().map { it.attrName to it.attrValue }.let { fromAttributeList(it) }
    }

    /**
     * 将一个[RuleAttributes]的属性转换为key(string) -> value(string)的形式
     */
    fun toAttributeList(ruleId: IdType): List<RuleAttribute>? = reflection.asSequence()
        .filter { (_, getter) -> getter.get(this) != null }
        .map { (name, getter) -> RuleAttribute(ruleId, name, getter.get(this)!!.serialize()) }
        .toList().takeIf { it.isNotEmpty() }

    private fun Any.serialize(): String =
        if(this is List<*>) this.joinToString(separator) else this.toString()

}
