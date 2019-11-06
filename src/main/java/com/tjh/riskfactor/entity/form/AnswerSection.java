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

    @JoinColumn(table = "section", referencedColumnName = "id", nullable = false)
    private Integer parentId;

    @JoinColumn(table = "section", referencedColumnName = "id", nullable = false)
    private Integer childId;

    @Convert(converter = MapConverter.class)
    @Column(length = 10240, nullable = false)
    private Map<String, Object> body;

    @Transient public AnswerSection setSection(Integer parent, Integer child) {
        parentId = parent; childId = child;
        return this;
    }

}
