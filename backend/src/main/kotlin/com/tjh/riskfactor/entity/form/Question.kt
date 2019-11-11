package com.tjh.riskfactor.entity.form

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

import com.tjh.riskfactor.entity.IEntity

import javax.persistence.*

@Entity @Table(name = "question")
class Question(
    var type: QuestionType? = null,

    var label: String? = null,

    @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.REMOVE])
    @JoinTable(name = "question_list",
        joinColumns = [JoinColumn(name = "head", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "qid", referencedColumnName = "id")]
    )
    @OrderColumn(name = "sequence", nullable = false)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    var list: MutableList<Question>?
): IEntity() {

    // 指定是否为必填
    @Transient var required: Boolean? = null
    // 指定该问题的输入能否控制后续问题的显示
    // 用于指定联动问题，即只有这个问题输入之后才能显示后续问题
    @JsonProperty("isEnabler")
    @Transient var isEnabler: Boolean? = null
    // 问题标签位置，仅供输入类组件使用，表示标签的位置
    // 值的含义同addonPosition
    @Transient var labelPosition: String? = null
    // 指定YESNO_CHOICE问题的选项内容
    // 格式：[是含义]/[否含义]
    @Transient var yesno: String? = null
    // 指定该问题是问题组长的前置/后置输入控件
    //   比如 [输入持续时间（整数）][单位选择（天/月）]，后者是前者的后置输入控件
    // 该域的含义：
    //   null: 无效果（默认）
    //   prefix: 前置输入
    //   postfix: 后置输入
    @Transient var addonPosition: String? = null
    // 指定列表/多选类问题默认选项，默认是未选择
    // 对于单项选择来说，只有一个条目
    // 对于多项选择来说，有多个条目，由半角逗号分隔
    @Transient var selected: String? = null
    // 额外说明
    @Transient var description: String? = null
    // 占位文字，一般是输入类控件需要
    // 对于IMMUTABLE类型来说，则是填充字符串
    @Transient var placeholder: String? = null
    // 下拉菜单如果选项很多，那么需要有输入时筛选的功能
    // 指定输入时筛选的前缀key，仅用于CHOICE
    // 对于下拉选择类问题来说，只要这一项不为空则开启输入筛选功能
    @Transient var filterKey: String? = null

    @get:Column(name = "option", length = 512)
    @get:Access(AccessType.PROPERTY)
    @get:JsonIgnore
    var option: String?
        get() = collectOptions()
        set(value) = assignOptions(value)

    private fun collectOptions(): String? {
        val collected = this.javaClass.declaredFields.filter { it.isAnnotationPresent(Transient::class.java) }.map {
            it.isAccessible = true; val name = it.name
            try {
                val value = it.get(this) ?: return@map null
                if(value is Boolean && value == true)
                    return@map name
                else
                    return@map "$name:$value"
            } catch (e: IllegalAccessException) { return@map null }
        }.filterNotNull().joinToString("$;")
        return if(collected.isEmpty()) null else collected
    }

    private fun assignOptions(value: String?) {
        if(value == null) return
        value.split(Regex("\\$;")).forEach{ part ->
            val kv = part.split(':')
            try {
                val field = this.javaClass.getDeclaredField(kv[0])
                field.isAccessible = true
                if(kv.size == 1)
                    field.set(this, true)
                else
                    field.set(this, kv[1])
            } catch(e: IllegalAccessException) {
            } catch(e: NoSuchFieldException) {}
        }
    }

}
