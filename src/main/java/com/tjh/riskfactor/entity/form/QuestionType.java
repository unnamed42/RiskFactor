package com.tjh.riskfactor.entity.form;

public enum QuestionType {
    // 单项输入，输入类型为对应类型
    TEXT, // 对应文本输入
    NUMBER, // 对应数字输入，浮点
    DATE, // 对应日期选择
    CHOICE, // 选择/下拉选择的选项内容
    IMMUTABLE, // 不可变内容，其值由服务器赋予

    YESNO_CHOICE, // 两项单选，否/是，一般选“是”有后置问题

    // 问题组类型
    LIST, // 固定数量问题组
    LIST_APPENDABLE, // 可变长度问题组，一般是可重复添加的项目
                     // 该类问题下所属question list的第一个（且应仅有一个）为添加问题模板
    MULTI_CHOICE, // 多选，一般是Checkbox实现
    SINGLE_CHOICE, // 单选，一般是RadioButton实现
    SINGLE_SELECT, // 单项下拉选择
    MULTI_SELECT // 多项下拉选择
}
