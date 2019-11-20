package com.tjh.riskfactor.entity.form;

import com.fasterxml.jackson.annotation.JsonValue;

enum class QuestionType(private val value: String) {
    // 单项输入，输入类型为对应类型

    // 可为null，代表是多选/单选的选项

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

    HEADER("header");

    @JsonValue
    fun getValue() = this.value
}
