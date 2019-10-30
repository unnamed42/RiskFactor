package com.tjh.riskfactor.entity.form;

import lombok.Data;
import lombok.experimental.Accessors;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import static com.fasterxml.jackson.annotation.JsonFormat.Shape;

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
    @JsonIgnore
    private User creator;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private Task ownerTask;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private Section ownerSection;

    @Column(nullable = false)
    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd")
    private Date mtime;

    @Convert(converter = MapConverter.class)
    @Column(length = 10240, nullable = false)
    @Basic(fetch = FetchType.LAZY)
    private Map<String, Object> body;

    @JsonProperty("creator")
    @Transient public String getCreator() { return creator.getUsername(); }

    @JsonProperty("ownerSection")
    @Transient public Integer ownerSectionId() { return ownerSection.getId(); }

    @JsonProperty("ownerTask")
    @Transient public Integer ownerTaskId() { return ownerTask.getId(); }

}
