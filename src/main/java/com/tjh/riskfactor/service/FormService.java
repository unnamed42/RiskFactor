package com.tjh.riskfactor.service;

import lombok.val;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.tjh.riskfactor.entity.form.Section;
import com.tjh.riskfactor.entity.form.Question;
import com.tjh.riskfactor.entity.form.QuestionType;
import com.tjh.riskfactor.repo.QuestionRepository;
import com.tjh.riskfactor.repo.QuestionOptionRepository;
import com.tjh.riskfactor.repo.SectionRepository;
import static com.tjh.riskfactor.error.ResponseErrors.notFound;

import java.util.Base64;
import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class FormService {

    private final QuestionRepository questions;
    private final QuestionOptionRepository questionOptions;
    private final SectionRepository sections;

    private static String uuid() {
        val random = new SecureRandom();
        val buffer = new byte[16];
        random.nextBytes(buffer);
        return Base64.getEncoder().encodeToString(buffer);
    }

    public Section fetchFormSection(String sectionTitle) {
        return sections.findByTitle(sectionTitle)
                .orElseThrow(() -> notFound("form", sectionTitle));
    }

    // 存储Question中的其他相关实体，确保存储q之前它的所有属性都是数据库中存在的
    private Question saveQuestionProps(Question q) {
        if(q.getType() == null)
            q.setType(QuestionType.CHOICE);
        val option = q.getOption();
        if(option != null) {
            if(option.getMessage() != null && !option.getMessage().isEmpty())
                option.setRequired(true);
            q.setOption(questionOptions.save(option));
        }
        val field = q.getField();
        if(field == null || field.isEmpty())
            q.setField(uuid());
        val list = q.getList();
        if(list != null && list.size() != 0) {
            val subq = list.stream().map(this::saveQuestionProps);
            q.setList(questions.saveAll(subq::iterator));
        }
        return q;
    }

    public Question saveQuestion(Question q) {
        return questions.save(saveQuestionProps(q));
    }

    public Section saveSection(Section section) {
        val qs = section.getQuestions().stream().map(this::saveQuestionProps);
        section.setQuestions(questions.saveAll(qs::iterator));
        return sections.save(section);
    }

    public Section saveSection(Section section, String title) {
        return saveSection(section.setTitle(title));
    }

}
