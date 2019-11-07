package com.tjh.riskfactor.entity.form;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
import java.util.List;

@Data @Entity
@Table(name = "section")
@Accessors(chain = true)
@EqualsAndHashCode(of = "id")
public class Section {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 标题，多级标题以斜杠分割，即 [一级标题]/[二级标题]/[三级标题]
     * 某一级标题允许空置，比如没有一级标题就是 /[二级标题]/[三级标题]，没有二级就是 [一级标题]//[三级标题]
     */
    @Column(nullable = false)
    private String title;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinTable(name = "section_question_list",
        joinColumns = @JoinColumn(name = "sid"),
        inverseJoinColumns = @JoinColumn(name = "qid")
    )
    @OrderColumn(name = "seq", nullable = false)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Question> questions;

    @JsonIgnore
    @Transient public String[] getTitles() {
        return title.split("/");
    }

}
