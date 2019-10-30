package com.tjh.riskfactor.entity.form;

import lombok.Data;
import lombok.experimental.Accessors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import static com.fasterxml.jackson.annotation.JsonFormat.Shape;

import com.tjh.riskfactor.entity.Group;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data @Entity
@Table(name = "task")
@Accessors(chain = true)
public class Task {

    @PrePersist
    private void assignFields() {
        if(mtime == null)
            mtime = new Date();
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "gid", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private Group group;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd")
    private Date mtime;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinTable(name = "task_sections",
        joinColumns = @JoinColumn(name = "tid"),
        inverseJoinColumns = @JoinColumn(name = "sid")
    )
    @OrderColumn(name = "seq", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Section> sections;

    // 用来给JSON用的，传入内容为用户组名
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Transient private String center;

    @JsonProperty(value = "center", access = JsonProperty.Access.READ_ONLY)
    @Transient public String getCenterName() {
        return group.getDisplayName();
    }

}
