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

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @JsonIgnore
    private Integer id;

    @Column(nullable = false)
    @ColumnDefault("false")
    @JsonInclude(Include.NON_DEFAULT)
    private boolean required = false;

    @Column(name = "is_enabler", nullable = false)
    @ColumnDefault("false")
    @JsonInclude(Include.NON_DEFAULT)
    private boolean enabler = false;


    private Boolean prefixPostfix;


    private String defaultSelected;

    // 额外说明
    // 当问题类型为YESNO_CHOICE的时候，选项说明会使用这一项的内容（如果存在）
    //     比如为“有/无”的时候，显示两个选项“有”，“无”
    //     若为null，则显示默认的“是”，“否”
    //     如果还需要额外说明，则内容应类似“是/否/额外说明”
    // 当类型为IMMUTABLE时，其内容填充为该项内容
    //    若该项以“expr:”开头，那么内容为“expr:”后的表达式求值结果
    //    比如为 expr:height*height/weight 时，获取数据库中fieldName为height和weight的变量，然后求值
    private String detail;

    private String filterKey;

    private String placeholder;

}
