package com.tjh.riskfactor.entity.form;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data @Entity
@Table(name = "answer_entry")
@Accessors(chain = true)
@EqualsAndHashCode(of = "id")
public class AnswerEntry {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", nullable = false)
    private Question question;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", nullable = false)
    private Answer answer;

    /**
     * null：无答案
     * 有内容：根据question-type来实际解析
     */
    private String value;

}
