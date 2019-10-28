package com.tjh.riskfactor.entity.form;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import com.fasterxml.jackson.annotation.JsonFormat;
import static com.fasterxml.jackson.annotation.JsonFormat.Shape;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data @Entity
@Table(name = "sections")
@Accessors(chain = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Sections {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String creator;

    @Column(nullable = false)
    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd")
    private Date ctime;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinTable(name = "section_parts",
          joinColumns = @JoinColumn(name = "ssid"),
          inverseJoinColumns = @JoinColumn(name = "sid")
    )
    @OrderColumn(name = "seq", nullable = false)
    private List<Section> sections;

}
