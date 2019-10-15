package com.tjh.riskfactor.entity.form;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import static com.fasterxml.jackson.annotation.JsonInclude.Include;

import org.hibernate.annotations.ColumnDefault;

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
    @Column(nullable = false)
    @ColumnDefault("false")
    @JsonInclude(Include.NON_DEFAULT)
    private boolean required = false;

    // 指定该问题的输入能否控制后续问题的显示
    // 用于指定联动问题，即只有这个问题输入之后才能显示后续问题
    @Column(name = "is_enabler", nullable = false)
    @ColumnDefault("false")
    @JsonInclude(Include.NON_DEFAULT)
    private boolean enabler = false;

    // 指定该问题是问题组长的前置/后置输入控件
    //   比如 [输入持续时间（整数）][单位选择（天/月）]，后者是前者的后置输入控件
    // 该域的含义：
    //   null: 无效果（默认）
    //   true: 前置输入
    //   false: 后置输入
    // 当问题类型为输入类（NUMBER，TEXT）时，指定说明文字与输入框的相对位置
    //   null: 说明位于输入框前方，如 [说明文字][输入框]（默认）
    //   true: 同上
    //   false: 说明文字位于输入框后方，如 [输入天数][天]
    private Boolean prefixPostfix;

    // 指定列表/多选类问题默认选项，默认是未选择
    private Integer defaultSelected;

    // 额外说明
    // 当问题类型为YESNO_CHOICE的时候，选项说明会使用这一项的内容（如果存在）
    //     比如为“有/无”的时候，显示两个选项“有”，“无”
    //     若为null，则显示默认的“是”，“否”
    //     如果还需要额外说明，则内容应类似“是/否/额外说明”
    // 当类型为IMMUTABLE时，其内容填充为该项内容
    //    若该项以“expr:”开头，那么内容为“expr:”后的表达式求值结果
    private String additionalDescription;

    // 下拉菜单如果选项很多，那么需要有输入时筛选的功能
    // 指定输入时筛选的前缀key
    // 对于下拉选择类问题来说，只要这一项不为空则开启输入筛选功能
    private String filterKey;

    // 占位文字，一般是输入类控件需要
    private String placeholderText;

    // 表单验证不通过时的错误提示信息
    private String errorMessage;

}
