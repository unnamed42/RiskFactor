package com.tjh.riskfactor.entity.form

import com.fasterxml.jackson.annotation.*

import com.tjh.riskfactor.entity.IEntity
import com.tjh.riskfactor.entity.cvt.QuestionOptionsConverter

import javax.persistence.*

@Entity @Table(name = "question")
class Question(
    /**
     * 可为null，代表是辅助用的纯文本
     */
    @get:Column
    @get:Enumerated
    var type: QuestionType? = null,

    @get:Column
    var label: String? = null,

    @get:ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.REMOVE])
    @get:JoinTable(name = "question_list",
        joinColumns = [JoinColumn(name = "head", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "qid", referencedColumnName = "id")]
    )
    @get:OrderColumn(name = "sequence", nullable = false)
    @get:JsonInclude(JsonInclude.Include.NON_EMPTY)
    var list: MutableList<Question>? = null,

    @get:Transient
    @get:JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    var ref: String? = null
): IEntity() {
    @get:Column(length = 102400)
    @get:Convert(converter = QuestionOptionsConverter::class)
    @get:JsonUnwrapped
    var options: QuestionOptions? = null
}

@Suppress("unused")
enum class QuestionType(@get:JsonValue val value: String) {
    TEXT("text"), // 对应文本输入
    NUMBER("number"), // 对应数字输入，浮点
    DATE("date"), // 对应日期选择
    IMMUTABLE("disabled"), // 不可变内容，其值由服务器赋予

    YESNO_CHOICE("either"), // 两项单选，否/是，一般选“是”有后置问题

    // 问题组类型
    LIST("list"), // 固定数量问题组
    LIST_APPENDABLE("template"), // 可变长度问题组，一般是可重复添加的项目
    // 该类问题下所属question list的第一个（且应仅有一个）为添加问题模板
    MULTI_CHOICE("choice-multi"), // 多选，一般是Checkbox实现
    SINGLE_CHOICE("choice"), // 单选，一般是RadioButton实现
    SINGLE_SELECT("select"), // 单项下拉选择
    MULTI_SELECT("select-multi"), // 多项下拉选择

    TABLE("table"),
    TABLE_HEADER("table-header"),

    HEADER("header");
}

@Suppress("unused")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
open class QuestionOptions {
    /**
     * 指定是否为必填
     */
    open var required: Boolean? = null

    /**
     * 指定该问题的输入能否控制后续问题的显示。用于指定联动问题，即只有这个问题输入之后才能显示后续问题
     */
    open var isEnabler: Boolean? = null

    /**
     * 指定多选/下拉类控件的最后是否有一个“其他”用于输入自定义选项内容
     */
    open var customizable: Boolean? = null

    /**
     * 指定多选/下拉类控件的选项
     *
     * 如果需要给选项指定筛选键，用 &#91;内容&#93;/&#91;筛选键&#93; 格式
     */
    open var choices: List<String>? = null

    /**
     * 问题标签位置，仅供输入类组件使用，表示标签的位置
     * 值的含义同[addonPosition]
     */
    open var labelPosition: String? = null

    /**
     * 指定[QuestionType.YESNO_CHOICE]问题的选项内容
     *
     * 格式：&#91;是含义]/&#91;否含义]
     */
    open var yesno: String? = null

    /**
     * 指定该问题是问题组长的前置/后置输入控件，比如 &#91;输入持续时间（整数）]&#91;单位选择（天/月）]，后者是前者的后置输入控件
     *
     * 该域的含义：
     *   null: 无效果（默认）
     *   prefix: 前置输入
     *   postfix: 后置输入
     */
    open var addonPosition: String? = null

    /**
     * 指定列表/多选类问题默认选项，默认是未选择
     *
     * 对于单项选择来说，只有一个条目；对于多项选择来说，有多个条目，由半角逗号分隔
     */
    open var selected: String? = null

    /**
     * 额外说明
     */
    open var description: String? = null

    /**
     * 占位文字，一般是输入类控件需要
     *
     * 对于[QuestionType.IMMUTABLE]类型来说，则是填充字符串
     */
    open var placeholder: String? = null
}
