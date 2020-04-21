package com.tjh.riskfactor.component.loader

import com.fasterxml.jackson.annotation.JsonUnwrapped

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import com.tjh.riskfactor.repository.*
import com.tjh.riskfactor.service.AccountService
import com.tjh.riskfactor.service.RuleAttributes

@Component
class SchemaLoader(
    private val rules: RuleRepository,
    private val ruleLists: RuleListRepository,
    private val ruleAttrs: RuleAttributeRepository,
    private val schemas: SchemaRepository,
    private val accounts: AccountService
) {

    @Transactional
    fun loadFromSchema(models: List<SchemaModel>) =
        models.forEach(this::loadFromSchema)

    private fun loadFromSchema(schema: SchemaModel) {
        // 带ref引用的规则 RuleModel::ref -> Rule
        val refReferenced = mutableMapOf<String, Rule>()
        // 要替换placeholder中ref引用的规则 pair(Rule, RuleAttributes::placeholder)
        val refDependent = mutableListOf<Pair<Rule, String>>()

        // 根节点，是虚拟的，不对渲染结果起实际作用
        val rootModel = RuleModel(type = RuleType.ROOT, list = schema.list)

        fun traverse(ruleModel: RuleModel): Rule {
            val entity = ruleModel.saved()
            // ref规则，登记ref名
            if(ruleModel.ref != null)
                refReferenced[ruleModel.ref] = entity
            // ref替换请求，登记其id和计算表达式
            val placeholder = ruleModel.options?.placeholder
            if(placeholder != null && ruleModel.type == RuleType.EXPR)
                refDependent.add(entity to placeholder)
            // 存储其list
            if(ruleModel.list != null && ruleModel.list.isNotEmpty()) {
                val childNodes = ruleModel.list.map { traverse(it) }
                saveList(entity.id, childNodes)
            }
            return entity
        }

        val rootNode = traverse(rootModel)

        // 所有规则条目已经存储完成，现在替换ref代表的placeholder，ref换为对应的"$${id}"
        val regex = "\\$(\\w+)".toRegex()
        val attrName = RuleAttributes::placeholder.name
        refDependent.forEach { (rule, placeholder) ->
            val replaced = placeholder.replace(regex) { match ->
                val (ref) = match.destructured
                val id = refReferenced[ref]?.id ?: throw RuntimeException("referenced ref name [$ref] does not exist")
                "$$id"
            }
            ruleAttrs.save(RuleAttribute(rule.id, attrName, replaced))
        }

        // 所有规则条目已经完全处理完毕，存储schema
        schemas.save(Schema(
            name = schema.name, creatorId = accounts.findUser(schema.creator).id,
            groupId = accounts.findGroup(schema.group).id, rootObjectId = rootNode.id))
    }

    private fun saveList(parentId: IdType, rules: List<Rule>) {
        ruleLists.saveAll(rules.mapIndexed { index, rule -> RuleList(parentId, rule.id, index) })
    }

    private fun RuleModel.saved(): Rule = rules.save(Rule(type = type, label = label)).also { rule ->
        this.options?.toAttributeList(rule.id)?.also { ruleAttrs.saveAll(it) }
    }
}

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
