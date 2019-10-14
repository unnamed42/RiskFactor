package com.tjh.riskfactor.entity.form;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;

@Entity @Data
@Table(name = "question_option")
@Accessors(chain = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class QuestionOption {

    @Id @GeneratedValue
    @EqualsAndHashCode.Include
    @JsonIgnore
    private Integer id;

    // 指定是否为必填
    private boolean required = false;

    // 下拉菜单如果选项很多，那么需要有输入时筛选的功能
    // 指定输入时筛选的前缀key
    private String filterKey;

    // 占位文字，一般是输入类控件需要
    private String placeholderText;

    // 表单验证不通过时的错误提示信息
    private String errorMessage;

}
