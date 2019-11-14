package com.tjh.riskfactor.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import com.tjh.riskfactor.entity.form.*
import com.tjh.riskfactor.repo.SectionRepository

@Service
class SectionService(override val repo: SectionRepository): IDBService<Section>("section") {

    @Transactional
    fun sectionsOfTask(taskId: Int): List<Section> {
        val sections = repo.findAllByOwnerTaskId(taskId)
        val unused = sections.map { it.questions?.size }.toList()
        return sections
    }

}
