package com.tjh.riskfactor.service;

import lombok.val;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tjh.riskfactor.entity.form.*;
import com.tjh.riskfactor.repo.QuestionRepository;
import com.tjh.riskfactor.repo.QuestionOptionRepository;
import com.tjh.riskfactor.repo.SectionRepository;
import com.tjh.riskfactor.repo.TaskRepository;

import static com.tjh.riskfactor.error.ResponseErrors.notFound;

import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;
import java.security.SecureRandom;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class FormService implements IDBService {

    private final QuestionRepository questions;
    private final QuestionOptionRepository questionOptions;
    private final SectionRepository sections;
    private final TaskRepository tasks;

    private final AccountService accounts;

    private String uuid() {
        val random = new SecureRandom();
        val buffer = new byte[9];
        random.nextBytes(buffer);
        val id = Base64.getUrlEncoder().encodeToString(buffer);
        if(questions.existsByField(id))
            return uuid();
        return id;
    }

    @Transactional
    public void drop() {
        tasks.deleteAll();
    }

    // 沿问题路径生成唯一key
    private static String key(String parent, Question q) {
        if(parent == null) return null;
        val field = q.getField();
        if(field == null) {
            // 类型为LIST的问题，本身在路径中无意义，作为包装存在，可以不参与生成
            // 对于这类Question，路径处理中跳过，即生成key为 [父问题field]/[LIST子问题field]
            if(q.getType() == QuestionType.LIST)
                return parent;
            // 其余为null
            return null;
        }
        return String.format("%s/%s", parent, field);
    }

    private String assignKey(String parent, Question q) {
        String key = key(parent, q);
        // 没有field的Question，即不重要的Question，随机生成field（用于前端的唯一key）
        if(key == null || q.getType() == QuestionType.LIST)
            q.setField(uuid());
        else
            q.setField(key);
        return key;
    }

    // 存储Question中的其他相关实体，确保存储q之前它的所有属性都是数据库中存在的
    @Transactional
    Question assignFields(Question q, String parent) {
        // 默认type为CHOICE
        val type = q.getType();
        if(type == null)
            q.setType(QuestionType.CHOICE);
        // 带有message但是没有设置required的Question，其required设置为true
        val option = q.getOption();
        if(option != null)
            q.setOption(questionOptions.save(option));
        val key = assignKey(parent, q);
        // 递归设置子问题的缺失属性
        val list = q.getList();
        if(list != null && list.size() != 0) {
            Stream<Question> subq = list.stream().map(q0 -> this.assignFields(q0, key));
            q.setList(questions.saveAll(subq::iterator));
        }
        return q;
    }

    public Section section(String sectionTitle) {
        return sections.findByTitle(sectionTitle)
               .orElseThrow(() -> notFound("form", sectionTitle));
    }

    public Question saveQuestion(Question q) {
        return questions.save(assignFields(q, null));
    }

    @Transactional
    public Section saveSection(Section section) {
        if(section.getId() != null)
            return section;
        if(section.getSections() != null) {
            val ss = section.getSections().stream()
                 .map(this::saveSection).collect(toList());
            return sections.save(section.setSections(ss));
        }
        val qs = section.getQuestions();
        // 将Section的id当作初始parent
        // 在Section未存入数据库之前，断开Questions存入数据库，来获取一个id
        if(section.getId() == null) {
            section.setQuestions(null);
            section.setSections(null);
            section = sections.save(section);
        }
        val parent = section.getId().toString();
        Stream<Question> qss = qs.stream().map(q -> assignFields(q, parent));
        section.setQuestions(questions.saveAll(qss::iterator));
        return sections.save(section);
    }

    @Transactional
    public Task saveTask(Task task) {
        task.setGroup(accounts.findGroupByName(task.getCenter()));
        task.setSections(task.getSections().stream()
                         .map(this::saveSection).collect(toList()));
        return tasks.save(task);
    }

    public List<Task> availableTasks(String username) {
        return tasks.findAll();
    }

}
