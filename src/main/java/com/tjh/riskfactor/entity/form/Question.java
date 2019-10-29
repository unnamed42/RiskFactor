package com.tjh.riskfactor.entity.form;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import static com.fasterxml.jackson.annotation.JsonInclude.Include;

import javax.persistence.*;
import java.util.List;

@Data @Entity
@Table(name = "question")
@Accessors(chain = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Question {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @JsonIgnore
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType type = QuestionType.CHOICE;

    @Column(unique = true)
    private String field;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinTable(name = "question_list",
        joinColumns = @JoinColumn(name = "head", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "qid", referencedColumnName = "id")
    )
    @OrderColumn(name = "sequence", nullable = false)
    @JsonInclude(Include.NON_EMPTY)
    private List<Question> list;

    // 指定是否为必填
    @Transient private boolean required = false;
    // 指定该问题的输入能否控制后续问题的显示
    // 用于指定联动问题，即只有这个问题输入之后才能显示后续问题
    @Transient private boolean isEnabler = false;
    // 问题标签
    @Transient private String label;
    // 指定该问题是问题组长的前置/后置输入控件
    //   比如 [输入持续时间（整数）][单位选择（天/月）]，后者是前者的后置输入控件
    // 该域的含义：
    //   null: 无效果（默认）
    //   prefix: 前置输入
    //   postfix: 后置输入
    @Transient private String addonPosition;
    // 指定列表/多选类问题默认选项，默认是未选择
    // 对于单项选择来说，只有一个条目
    // 对于多项选择来说，有多个条目，由半角逗号分隔
    @Transient private String selected;
    // 额外说明
    @Transient private String description;
    // 占位文字，一般是输入类控件需要
    // 对于IMMUTABLE类型来说，则是填充字符串
    @Transient private String placeholder;
    // 下拉菜单如果选项很多，那么需要有输入时筛选的功能
    // 指定输入时筛选的前缀key，仅用于CHOICE
    // 对于下拉选择类问题来说，只要这一项不为空则开启输入筛选功能
    @Transient private String filterKey;

    @Column(length = 1024)
    public String getOption() {
        return "";
    }

    public Question setOption(String option) {
        return this;
    }

}
