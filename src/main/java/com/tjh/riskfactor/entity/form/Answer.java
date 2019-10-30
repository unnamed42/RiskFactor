package com.tjh.riskfactor.entity.form;

import lombok.Data;
import lombok.experimental.Accessors;

import com.fasterxml.jackson.annotation.JsonFormat;
import static com.fasterxml.jackson.annotation.JsonFormat.Shape;

import com.tjh.riskfactor.entity.User;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data @Entity
@Table(name = "answer")
@Accessors(chain = true)
public class Answer {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", nullable = false)
    private User creator;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", nullable = false)
    private Task task;

    @Column(nullable = false)
    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd")
    private Date mtime;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinTable(name = "answer_parts",
        joinColumns = @JoinColumn(name = "aid"),
        inverseJoinColumns = @JoinColumn(name = "part_id")
    )
    private List<AnswerSection> parts;

}
