package com.tjh.riskfactor.entity.form;

import com.tjh.riskfactor.entity.cvt.MapConverter;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Map;

@Data @Entity
@Table(name = "answer_section")
@Accessors(chain = true)
public class AnswerSection {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String sectionPath;

    @Convert(converter = MapConverter.class)
    @Column(length = 10240, nullable = false)
    private Map<String, Object> body;

}
