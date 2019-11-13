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
    @get:Column(nullable = false)
    var name: String,

    @get:Column(nullable = false)
    @get:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    var mtime: Date = Date(),

    @get:ManyToOne
    @get:JoinColumn(name = "gid", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    var group: Group,

    @get:OneToMany(cascade = [CascadeType.REMOVE])
    @get:JoinTable(name = "task_sections",
        joinColumns = [JoinColumn(name = "tid")],
        inverseJoinColumns = [JoinColumn(name = "sid")]
    )
    @get:OrderColumn(name = "seq", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    var sections: MutableList<Section>? = null
): IEntity() {

    // 用来给JSON用的，传入内容为用户组名
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @get:Transient var center: String = ""

    @JsonProperty(value = "center", access = JsonProperty.Access.READ_ONLY)
    @Transient fun getCenterName() = group.displayName
}
