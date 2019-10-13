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
    // 该项指明筛选是根据Question的哪个field（目前只有fieldName和description）来进行筛选
    // 若为空，那么关闭筛选功能
    private String filterField;

}
