package com.tjh.riskfactor.entity.form

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.tjh.riskfactor.entity.IEntity

import javax.persistence.*

@Entity @Table(name = "section")
class Section(
    /**
     * 标题，多级标题以斜杠分割，即 [一级标题]/[二级标题]/[三级标题]
     * 某一级标题允许空置，比如没有一级标题就是 /[二级标题]/[三级标题]，没有二级就是 [一级标题]//[三级标题]
     */
    @get:Column(nullable = false)
    var title: String,

    @get:OneToMany(cascade = [CascadeType.REMOVE])
    @get:JoinTable(name = "section_question_list",
        joinColumns = [JoinColumn(name = "sid")],
        inverseJoinColumns = [JoinColumn(name = "qid")]
    )
    @get:OrderColumn(name = "seq", nullable = false)
    @get:JsonInclude(JsonInclude.Include.NON_EMPTY)
    var questions: MutableList<Question>?
): IEntity() {

    @JsonIgnore
    @Transient fun getTitles() = title.split('/')
}
