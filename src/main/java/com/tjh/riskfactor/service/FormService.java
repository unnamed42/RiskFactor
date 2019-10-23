package com.tjh.riskfactor.service;

import lombok.val;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import com.tjh.riskfactor.entity.form.*;
import com.tjh.riskfactor.repo.QuestionRepository;
import com.tjh.riskfactor.repo.QuestionOptionRepository;
import com.tjh.riskfactor.repo.SectionRepository;
import org.springframework.transaction.annotation.Transactional;

import static com.tjh.riskfactor.error.ResponseErrors.notFound;

import java.util.List;
import java.util.Base64;
import java.io.IOException;
import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class FormService {

    private final QuestionRepository questions;
    private final QuestionOptionRepository questionOptions;
    private final SectionRepository sections;

    private static String uuid() {
        val random = new SecureRandom();
        val buffer = new byte[9];
        random.nextBytes(buffer);
        return Base64.getEncoder().encodeToString(buffer);
    }

    public Section fetchFormSection(String sectionTitle) {
        return sections.findByTitle(sectionTitle)
                .orElseThrow(() -> notFound("form", sectionTitle));
    }

    public List<Section> fetchSections() {
        val sort = Sort.by(Sort.Direction.ASC, "id");
        return sections.findAll(sort);
    }

    // 存储Question中的其他相关实体，确保存储q之前它的所有属性都是数据库中存在的
    @Transactional
    Question saveQuestionProps(Question q) {
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

    @Transactional
    public Section saveSection(Section section) {
        val qs = section.getQuestions().stream().map(this::saveQuestionProps);
        section.setQuestions(questions.saveAll(qs::iterator));
        return sections.save(section);
    }

    public Section saveSection(Section section, String title) {
        return saveSection(section.setTitle(title));
    }

    public void initDatabase(String resource) throws IOException {
        val type = new TypeReference<List<Section>>() {};
        val mapper = new ObjectMapper(new YAMLFactory());
        try(val is = TypeReference.class.getResourceAsStream(resource)) {
            List<Section> sections = mapper.readValue(is, type);
            sections.forEach(this::saveSection);
        }
    }

}
