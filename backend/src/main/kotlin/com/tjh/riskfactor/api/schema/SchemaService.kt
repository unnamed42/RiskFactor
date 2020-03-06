package com.tjh.riskfactor.api.schema

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import com.tjh.riskfactor.common.*
import com.tjh.riskfactor.api.account.AccountService
import com.tjh.riskfactor.api.account.UserInfo
import com.tjh.riskfactor.api.answer.AnswerService

import java.sql.Date

@Service
class SchemaService(
    val rules: RuleRepository,
    val ruleLists: RuleListRepository,
    val ruleAttrs: RuleAttributeRepository,
    val schemas: SchemaRepository,
    private val answers: AnswerService,
    private val accounts: AccountService,
    private val mapper: ObjectMapper
) {

    /**
     * 获得问卷的结构和初始化数据。所有的问卷构建规则id都已经转化成"$${id}"的格式
     */
    @Transactional(readOnly = true)
    fun getSchema(schemaId: IdType): SchemaInfo {
        val schema = schemas.findChecked(schemaId)

        val rules = mutableMapOf<String, RuleInfo>()
        val collectShape = mutableMapOf<String, String>()

        // 从根节点开始，逐层获取
        var pending = listOf(schema.rootId)
        while(pending.isNotEmpty()) {
            pending = pending.flatMap {
                // TODO: 理论上是一定能拿到这个实体的。如果拿不到的情况打个log
                val rule = this.rules.findById(it).get()
                val list = this.rules.findList(it)
                val identifier = "$${rule.id}"
                rules[identifier] = rule.toInfo(list)
                rule.collectName?.let { collectShape[it] = identifier }
                list
            }
        }

        return schema.toInfo(rules, collectShape)
    }

    private fun saveList(parentId: IdType, rules: List<Rule>) {
        rules.mapIndexed { index, rule ->
            RuleList(parentId, rule.id, index)
        }.also { ruleLists.saveAll(it) }
    }

    @Transactional
    fun loadFromSchema(models: List<SchemaModel>) = models.forEach(this::loadFromSchema)

    private fun loadFromSchema(schema: SchemaModel) {
        // 带ref引用的规则 RuleModel::ref -> Rule
        val refReferenced = mutableMapOf<String, Rule>()
        // 要替换placeholder中ref引用的规则 pair(Rule, RuleAttributes::placeholder)
        val refRequested = mutableListOf<Pair<Rule, String>>()
        // 初始化数据 $${Rule::id} -> RuleModel::init
        val initialValues = mutableMapOf<String, String>()

        // 根节点，是虚拟的，不对渲染结果起实际作用
        val rootModel = RuleModel(type = RuleType.ROOT, list = schema.list)
        val rootNode = rootModel.saved()

        var ruleModels = listOf(rootModel); var ruleNodes = listOf(rootNode)
        while(ruleModels.isNotEmpty()) {
            val nextModels = mutableListOf<RuleModel>()
            val nextNodes = mutableListOf<Rule>()
            // 每个节点自身已经存储在数据库中，后续只需要处理它的list
            for(i in ruleModels.indices) {
                val currModel = ruleModels[i]; val currNode = ruleNodes[i]
                // 存储其list，并将结果存储到下一轮循环的节点列表中
                val childModels = currModel.list
                if(childModels != null && childModels.isNotEmpty()) {
                    val childNodes = childModels.map { it.saved() }
                    this.saveList(currNode.id, childNodes)
                    nextModels.addAll(childModels); nextNodes.addAll(childNodes)
                }
                // 记录初始化数据
                if(currModel.init != null)
                    initialValues["$${currNode.id}"] = currModel.init
                // ref规则，登记ref名
                if(currModel.ref != null)
                    refReferenced[currModel.ref] = currNode
                // ref替换请求，登记其id和计算表达式
                val placeholder = currModel.options?.placeholder
                if(placeholder != null && currModel.type == RuleType.EXPR)
                    refRequested.add(currNode to placeholder)
            }
            ruleModels = nextModels; ruleNodes = nextNodes
        }

        // 所有规则条目已经存储完成，现在替换ref代表的placeholder，ref换为对应的"$${id}"
        val regex = "\\$(\\w+)".toRegex()
        val attrName = RuleAttributes::placeholder.name
        refRequested.forEach { (rule, placeholder) ->
            val replaced = placeholder.replace(regex) { match ->
                val (ref) = match.destructured
                val id = refReferenced[ref] ?: throw RuntimeException("referenced ref name [$ref] does not exist")
                "$$id"
            }
            ruleAttrs.save(RuleAttribute(rule.id, attrName, replaced))
        }

        // 所有规则条目已经完全处理完毕，存储schema
        val savedSchema = schemas.save(schema.toEntity(rootNode.id))

        // 存储初始化数据
        if(initialValues.isNotEmpty())
            answers.writeAnswer(0, savedSchema.id, initialValues, accounts.findUserChecked(schema.creator))
    }

    private fun getRuleOptions(ruleId: IdType): RuleAttributes? =
        rules.findAttributes(ruleId).map { it.attrName to it.attrValue }.toMap()
            .takeIf{ it.isNotEmpty() }?.deserialize()

    private fun Rule.toInfo(list: List<IdType>) = RuleInfo(
        type = type, label = label, options = getRuleOptions(id), list = list.map { "$$it" })

    private fun Schema.toInfo(rules: Map<String, RuleInfo>, collectShape: Map<String, String>) = SchemaInfo(
        id = id, name = name, creator = accounts.userInfo(creatorId), root = "$$rootId",
        initialValues = answers.getInitialValues(id),
        rules = rules, collectShape = collectShape)

    private fun SchemaModel.toEntity(rootId: IdType) = Schema(
        name = name, creatorId = accounts.findUserChecked(creator).id,
        groupId = accounts.findGroup(group).id, rootId = rootId,
        createdAt = Date(System.currentTimeMillis()), modifiedAt = Date(System.currentTimeMillis())
    )

    private fun RuleModel.toEntity() = Rule(
        type = type, label = label)

    private fun RuleModel.saved(): Rule {
        val saved = rules.save(this.toEntity())
        this.options?.also {
            val attributes = it.serialize()
            ruleAttrs.saveAll(attributes.map { (k, v) -> RuleAttribute(saved.id, k, v) })
        }
        return saved
    }

    private fun RuleAttributes.serialize(): Map<String, String> {
        val type = object: TypeReference<Map<String, Any>>() {}
        val converted = mapper.convertValue(this, type)
        return converted.map {
            it.key to if(it.value is List<*>) (it.value as List<*>).joinToString(separator.toString()) else it.value.toString()
        }.toMap()
    }

    private fun Map<String, Any>.deserialize(): RuleAttributes {
        val mutable = this.toMutableMap()
        val choices = mutable["choices"]
        if(choices != null)
            mutable["choices"] = (choices as String).split(separator)
        return mapper.convertValue(mutable, RuleAttributes::class.java)
    }
}

data class RuleInfo(
    val type: RuleType?,
    val label: String?,
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val list: List<String>?,
    @field:JsonUnwrapped
    val options: RuleAttributes?
)

data class SchemaInfo(
    val id: IdType,
    val name: String,
    val creator: UserInfo,
    var root: String,
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val rules: Map<String, RuleInfo>,
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val initialValues: Map<String, String>,
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val collectShape: Map<String, String>
)

data class RuleModel(
    val ref: String? = null,
    val label: String? = null,
    val type: RuleType? = null,
    val init: String? = null,
    val list: List<RuleModel>? = null
) {
    @field:JsonUnwrapped
    val options: RuleAttributes? = null
}

data class SchemaModel(
    val creator: String, // username
    val group: String,
    val name: String,
    val list: List<RuleModel>?
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
     *
     * 对于[RuleType.IMMUTABLE]类型来说，则是填充字符串。不使用[init]是因为初始值原则上是不变化的
     */
    var placeholder: String? = null
)
