package com.tjh.riskfactor.entity.form;

import lombok.Data;
import lombok.experimental.Accessors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import static com.fasterxml.jackson.annotation.JsonFormat.Shape;
import static com.fasterxml.jackson.annotation.JsonProperty.Access;

import com.tjh.riskfactor.entity.User;
import com.tjh.riskfactor.entity.cvt.MapConverter;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;

@Data @Entity
@Table(name = "answer")
@Accessors(chain = true)
public class Answer {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", nullable = false)
    private User creator;

    // 用来给JSON用的，传入为创建者用户名
    @Transient
    @JsonProperty(access = Access.WRITE_ONLY)
    private String creatorName;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", nullable = false)
    private Section answerTo;

    // 用来给JSON用的，传入为Section的title
    @Transient
    @JsonProperty(access = Access.WRITE_ONLY)
    private String section;

    @Column(nullable = false)
    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd")
    private Date mtime;

    @Convert(converter = MapConverter.class)
    @Column(length = 102400, nullable = false)
    @Basic(fetch = FetchType.LAZY)
    private Map<String, Object> body;

}
