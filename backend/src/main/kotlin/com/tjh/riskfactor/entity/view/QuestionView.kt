package com.tjh.riskfactor.entity.view

import com.tjh.riskfactor.entity.form.QuestionType

interface QuestionView {
    val type: QuestionType?
    val label: String?
}
