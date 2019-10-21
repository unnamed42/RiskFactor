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
    private QuestionType type;

    @Column(unique = true)
    private String field;

    private String label;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "id")
    private QuestionOption option;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "question_list",
        joinColumns = @JoinColumn(name = "head", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "qid", referencedColumnName = "id")
    )
    @OrderColumn(name = "sequence", nullable = false)
    @JsonInclude(Include.NON_EMPTY)
    private List<Question> list;

}
