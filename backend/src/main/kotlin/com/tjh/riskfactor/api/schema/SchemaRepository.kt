package com.tjh.riskfactor.api.schema

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonValue

import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.Query

import com.tjh.riskfactor.common.BaseEntity
import com.tjh.riskfactor.common.IdType
import com.tjh.riskfactor.common.QueryRepository

import java.io.Serializable
import java.sql.Date
import javax.persistence.*

/**
 * 单个问题的构建规则
 */
@Entity @Table
class Rule(
    /**
     * 可为null，代表是辅助用的纯文本
     */
    @Enumerated
    var type: RuleType? = null,

    var label: String? = null,

    /**
     * 最终收集到excel表格中的名称。内容为字符串数组，以特定分隔符划分
     *   例："头1<sep>头2<sep>头3" 代表此项为一级表头为“头1”，二级表头为“头2”，三级表头为“头3”的值
     */
    @JsonIgnore
    var collectName: String? = null
): BaseEntity()

@Suppress("unused")
enum class RuleType(@JsonValue val value: String) {
    ROOT("root"), // 只做管理用，理应不会出现在后端服务以外的地方
    TEXT("text"), // 对应文本输入
    NUMBER("number"), // 对应数字输入，浮点
    DATE("date"), // 对应日期选择
    EXPR("expression"), // 自动根据其他项的值计算新值

    YESNO_CHOICE("either"), // 两项单选，否/是，一般选“是”有后置问题

    // 问题组类型
    LIST("list"), // 固定数量问题组
    TEMPLATE("template"), // 可变长度问题组，一般是可重复添加的项目
    // 该类问题下所属question list的第一个（且应仅有一个）为添加问题模板
    MULTI_CHOICE("choice-multi"), // 多选，一般是Checkbox实现
    SINGLE_CHOICE("choice"), // 单选，一般是RadioButton实现
    SINGLE_SELECT("select"), // 单项下拉选择
    MULTI_SELECT("select-multi"), // 多项下拉选择

    TABLE("table"),
    TABLE_HEADER("table-header"),

    HEADER("header");

    /**
     * 此类型的回答是否应当导出到导出文件当中
     */
    fun shouldCollect(): Boolean = when(this) {
        ROOT, LIST, TEMPLATE, TABLE, TABLE_HEADER, HEADER -> false
        else -> true
    }
}

/**
 * 用以指定问题的[list]属性
 */
@Entity @Table
@IdClass(RuleList.IdClass::class)
class RuleList(
    @Id @Column(nullable = false)
    var headId: IdType,

    @Column(nullable = false)
    var elemId: IdType,

    @Id @Column(nullable = false)
    var order: Int
) {
    data class IdClass(var headId: IdType = 0, var order: IdType = 0): Serializable
}

/**
 *
 */
@Entity @Table
@IdClass(RuleAttribute.IdClass::class)
class RuleAttribute(
    @Id var ruleId: IdType = 0,

    @Id var attrName: String = "",

    @Column(nullable = false) @Lob
    var attrValue: String = ""
) {
    data class IdClass(var ruleId: IdType = 0, var attrName: String = ""): Serializable
}

/**
 * 代表一个完整的问卷结构
 */
@Entity @Table
class Schema(
    /**
     * 创建者的用户id
     */
    var creatorId: IdType = 0,

    /**
     * 所有组的id，不一定是创建者的用户组
     */
    var groupId: IdType = 0,

    @Column(nullable = false)
    var name: String = "",

    /**
     * 根问题的id
     */
    var rootId: IdType = 0,

    var createdAt: Date = Date(0),

    var modifiedAt: Date = Date(0)
): BaseEntity()

@Repository
interface RuleRepository: QueryRepository<Rule, IdType> {
    @Query("""select rule.id from RuleList list inner join Rule rule
        on list.headId = :id and list.elemId = rule.id order by list.order asc""")
    fun findList(id: IdType): List<IdType>

    @Query("""select attr from RuleAttribute attr inner join Rule rule
        on rule.id = :id and attr.ruleId = rule.id""")
    fun findAttributes(id: IdType): List<RuleAttribute>
}

@Repository
interface RuleListRepository: QueryRepository<RuleList, IdType>

@Repository
interface RuleAttributeRepository: QueryRepository<RuleAttribute, IdType>

@Repository
interface SchemaRepository: QueryRepository<Schema, IdType>
