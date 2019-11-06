package com.tjh.riskfactor.entity.form;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.List;

/**
 * Section：问题分组
 * 其下可能带有一组问题（questions）或者是一组问题组（sections），二者必有其一，
 *   但是不能同时存在
 */

@Data @Entity
@Table(name = "section")
@Accessors(chain = true)
@EqualsAndHashCode(of = "id")
public class Section {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinTable(name = "subsection_list",
        joinColumns = @JoinColumn(name = "head"),
        inverseJoinColumns = @JoinColumn(name = "sid")
    )
    @OrderColumn(name = "seq", nullable = false)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Section> sections;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinTable(name = "section_question_list",
        joinColumns = @JoinColumn(name = "sid"),
        inverseJoinColumns = @JoinColumn(name = "qid")
    )
    @OrderColumn(name = "seq", nullable = false)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Question> questions;

}
