package com.tjh.riskfactor.entity.form;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data @Entity
@Table(name = "answer")
@Accessors(chain = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Answer {

    @Id @GeneratedValue
    @EqualsAndHashCode.Include
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType type;

    @OneToOne
    @JoinColumn(name = "answer_to", nullable = false)
    private Question answerTo;

    // need conversion to actual data type
    private String answer;
}
