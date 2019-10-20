package com.tjh.riskfactor.entity.form;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Data @Entity
@Accessors(chain = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Section {

    @Id @GeneratedValue
    @EqualsAndHashCode.Include
    @JsonIgnore
    private Integer id;

    @Column(unique = true, nullable = false)
    private String title;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "section_question_list",
        joinColumns = @JoinColumn(name = "sid", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "qid", referencedColumnName = "id")
    )
    @OrderColumn(name = "sequence", nullable = false)
    private List<Question> questions;
}
