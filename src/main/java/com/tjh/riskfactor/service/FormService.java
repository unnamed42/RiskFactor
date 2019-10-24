package com.tjh.riskfactor.service;

import lombok.val;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.tjh.riskfactor.entity.form.*;
import com.tjh.riskfactor.repo.QuestionRepository;
import com.tjh.riskfactor.repo.QuestionOptionRepository;
import com.tjh.riskfactor.repo.SectionRepository;
import org.springframework.transaction.annotation.Transactional;

import static com.tjh.riskfactor.error.ResponseErrors.notFound;

import java.util.List;
import java.util.Base64;
import java.util.stream.Stream;
import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class FormService {

    private final QuestionRepository questions;
    private final QuestionOptionRepository questionOptions;
    private final SectionRepository sections;

    private String uuid() {
        val random = new SecureRandom();
        val buffer = new byte[9];
        random.nextBytes(buffer);
        val id = Base64.getUrlEncoder().encodeToString(buffer);
        if(questions.existsByField(id))
            return uuid();
        return id;
    }

    public Section section(String sectionTitle) {
        return sections.findByTitle(sectionTitle)
                .orElseThrow(() -> notFound("form", sectionTitle));
    }

    public List<Section> sections() {
        val sort = Sort.by(Sort.Direction.ASC, "id");
        return sections.findAll(sort);
    }

    // 存储Question中的其他相关实体，确保存储q之前它的所有属性都是数据库中存在的
    @Transactional
    Question assignFields(Question q, String parent) {
        // 带有message但是没有设置required的Question，其required设置为true
        val option = q.getOption();
        if(option != null) {
            if(option.getMessage() != null && !option.getMessage().isEmpty())
                option.setRequired(true);
            q.setOption(questionOptions.save(option));
        }
        val field = q.getField();
        // 没有field的Question，即不重要的Question，随机生成field（用于前端的唯一key）
        // 此外情况，沿问题间的包含关系递归设置为 [父问题field]/[子问题field] 以确保唯一性
        val curr = field == null || parent == null ? null :
                        String.format("%s/%s", parent, field);
        q.setField(curr == null ? uuid() : curr);
        // 递归设置子问题的缺失属性
        val list = q.getList();
        if(list != null && list.size() != 0) {
            Stream<Question> subq = list.stream().map(q0 -> this.assignFields(q0, curr));
            q.setList(questions.saveAll(subq::iterator));
        }
        return q;
    }

    public Question saveQuestion(Question q) {
        return questions.save(assignFields(q, null));
    }

    @Transactional
    public Section saveSection(Section section) {
        val qs = section.getQuestions();
        // 将Section的id当作初始parent
        // 在Section未存入数据库之前，断开Questions存入数据库，来获取一个id
        if(section.getId() == null) {
            section.setQuestions(null);
            section = sections.save(section);
        }
        val parent = section.getId().toString();
        Stream<Question> qss = qs.stream().map(q -> assignFields(q, parent));
        section.setQuestions(questions.saveAll(qss::iterator));
        return sections.save(section);
    }

    public Section saveSection(Section section, String title) {
        return saveSection(section.setTitle(title));
    }

}
