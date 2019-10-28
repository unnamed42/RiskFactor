package com.tjh.riskfactor.entity.form;

import lombok.Data;
import lombok.experimental.Accessors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.tjh.riskfactor.entity.cvt.MapConverter;

import javax.persistence.*;
import java.util.Map;

@Data @Entity
@Table(name = "answer")
@Accessors(chain = true)
public class Answer {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Integer id;

    @Column(nullable = false)
    private String creator;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", nullable = false)
    private Sections answering;

    @Convert(converter = MapConverter.class)
    @Column(length = 102400, nullable = false)
    private Map<String, Object> body;

}
