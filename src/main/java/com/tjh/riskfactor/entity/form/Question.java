package com.tjh.riskfactor.entity.form;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import static com.fasterxml.jackson.annotation.JsonInclude.Include;

import static com.tjh.riskfactor.util.Utils.annotatedDeclaredFields;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data @Entity
@Table(name = "question")
@Accessors(chain = true)
@EqualsAndHashCode(of = "id")
public class Question {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private QuestionType type;

    private String label;

    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(name = "question_list",
        joinColumns = @JoinColumn(name = "head", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "qid", referencedColumnName = "id")
    )
    @OrderColumn(name = "sequence", nullable = false)
    @JsonInclude(Include.NON_EMPTY)
    private List<Question> list;

    // 指定是否为必填
    @Transient private Boolean required;
    // 指定该问题的输入能否控制后续问题的显示
    // 用于指定联动问题，即只有这个问题输入之后才能显示后续问题
    @Transient private Boolean isEnabler;
    // 问题标签位置，仅供输入类组件使用，表示标签的位置
    // 值的含义同addonPosition
    @Transient private String labelPosition;
    // 指定YESNO_CHOICE问题的选项内容
    // 格式：[是含义]/[否含义]
    @Transient private String yesno;
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

    @Column(name = "option", length = 512)
    @Access(AccessType.PROPERTY)
    @JsonIgnore
    public String getOption() {
        String fields = annotatedDeclaredFields(this.getClass(), Transient.class).map(field -> {
            field.setAccessible(true);
            var name = field.getName();
            try {
                var value = field.get(this);
                if(value == null)
                    return null;
                if(field.getType().equals(Boolean.class) && ((Boolean) value))
                    return name;
                else
                    return String.format("%s:%s", name, value);
            } catch(IllegalAccessException ignored) { return null; }
        }).filter(Objects::nonNull).collect(Collectors.joining("$;"));

        return fields.isEmpty() ? null : fields;
    }

    public void setOption(String option) {
        if(option == null)
            return;
        Arrays.stream(option.split("\\$;")).forEach(part -> {
            var value = part.split(":");
            try {
                var field = this.getClass().getDeclaredField(value[0]);
                field.setAccessible(true);
                if(value.length == 1)
                    field.set(this, true);
                else
                    field.set(this, value[1]);
            } catch (IllegalAccessException | NoSuchFieldException ignored) {}
        });
    }

}
