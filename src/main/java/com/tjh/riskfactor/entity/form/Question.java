package com.tjh.riskfactor.entity.form;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Data @Entity
@Table(name = "question")
@Accessors(chain = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Question {

    @Id @GeneratedValue
    @EqualsAndHashCode.Include
    @JsonIgnore
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType type;

    @Column(unique = true, nullable = false)
    private String fieldName;

    @Column(nullable = false)
    private String description;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "id")
    private QuestionOption option;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "question_list",
        joinColumns = @JoinColumn(name = "question_head", referencedColumnName = "id")
    )
    @OrderColumn(name = "sequence", nullable = false)
    private List<Question> list;

}
