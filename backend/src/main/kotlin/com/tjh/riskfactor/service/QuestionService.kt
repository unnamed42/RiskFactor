package com.tjh.riskfactor.service

import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Autowired

import com.tjh.riskfactor.entity.form.Question
import com.tjh.riskfactor.repo.QuestionRepository

@Service
class QuestionService: IDBService<Question>("question") {

    @Autowired override lateinit var repo: QuestionRepository

}
