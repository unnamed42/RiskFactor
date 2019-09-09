package com.tjh.riskfactor.entity.form;

public enum QuestionType {
    // 单项输入，输入类型为对应类型
    TEXT, // 对应文本输入
    NUMBER, // 对应数字输入，浮点
    DATE, // 对应日期选择
    CHOICE, // 选择/下拉选择的选项内容
    IMMUTABLE, // 不可变内容，其值由服务器赋予

    // 问题组类型
    LIST, // 固定数量问题组
    LIST_APPENDABLE, // 可变长度问题组，一般是可重复添加的项目
    MULTI_CHOICE, // 多选，一般是Checkbox实现
    SINGLE_CHOICE, // 单选，一般是RadioButton实现
    ENABLER, // 联动问题，选为true则显示它的问题组
    SINGLE_SELECT, // 单项下拉选择
    MULTI_SELECT // 多项下拉选择
}
