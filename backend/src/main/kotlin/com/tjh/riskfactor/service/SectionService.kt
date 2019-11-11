package com.tjh.riskfactor.service

import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

import com.tjh.riskfactor.entity.form.*
import com.tjh.riskfactor.repo.SectionRepository

@Service
class SectionService: IDBService<Section>("section") {

    @Autowired override lateinit var repo: SectionRepository

    @Transactional
    fun sectionsOfTask(taskId: Int): List<Section> {
        val sections = repo.findAllByOwnerTaskId(taskId)
        sections.forEach{ it.questions!!.size }
        return sections
    }

}
