package com.tjh.riskfactor.service

import org.springframework.stereotype.Service

import com.tjh.riskfactor.entity.form.Question
import com.tjh.riskfactor.repo.QuestionRepository

@Service
class QuestionService(override val repo: QuestionRepository): IDBService<Question>("question")
