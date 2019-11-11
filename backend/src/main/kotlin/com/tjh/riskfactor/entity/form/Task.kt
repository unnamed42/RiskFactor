package com.tjh.riskfactor.entity.form

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty

import com.tjh.riskfactor.entity.IEntity
import com.tjh.riskfactor.entity.Group

import javax.persistence.*
import java.util.Date

/**
 * 一个项目
 */
@Entity @Table(name = "task")
class Task(
    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    var mtime: Date = Date(),

    @OneToMany(cascade = [CascadeType.REMOVE])
    @JoinTable(name = "task_sections",
        joinColumns = [JoinColumn(name = "tid")],
        inverseJoinColumns = [JoinColumn(name = "sid")]
    )
    @OrderColumn(name = "seq", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    var sections: MutableList<Section>?
): IEntity() {
    @ManyToOne
    @JoinColumn(name = "gid", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    lateinit var group: Group

    // 用来给JSON用的，传入内容为用户组名
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Transient var center: String = ""

    @JsonProperty(value = "center", access = JsonProperty.Access.READ_ONLY)
    @Transient fun getCenterName() = group.displayName
}
