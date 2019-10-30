package com.tjh.riskfactor.service;

import lombok.val;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tjh.riskfactor.entity.form.*;
import com.tjh.riskfactor.repo.QuestionRepository;
import com.tjh.riskfactor.repo.SectionRepository;
import com.tjh.riskfactor.repo.TaskRepository;
import static com.tjh.riskfactor.repo.TaskRepository.*;
import static com.tjh.riskfactor.error.ResponseErrors.notFound;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.security.SecureRandom;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class TaskService implements IDBService {

    private final QuestionRepository questions;
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

    /**
     * 获取用户能访问的项目
     * @param username 用户名
     * @return 能访问的项目的基本信息
     */
    public List<TaskBrief> availableTasks(String username) {
        return tasks.findAllTasks();
    }

    /**
     * 获取项目下属的分节的基本信息
     * @param id 项目id
     * @return 所有分节的基本信息
     */
    public List<SectionBrief> taskSectionsInfo(Integer id) {
        return tasks.findSectionNamesById(id);
    }

    /**
     * 获取分节
     * @param sid 分节id
     * @return 分节的全部信息
     */
    public Optional<Section> findSectionById(Integer sid) {
        return sections.findById(sid);
    }

    // 沿问题路径生成唯一key
    private static String key(String parent, Question q) {
        if(parent == null)
            return null;
        val field = q.getField();
        if(field == null)
            // 类型为LIST的问题，本身在路径中无意义，作为包装存在，可以不参与生成
            // 对于这类Question，路径处理中跳过，即生成key为 [父问题field]/[LIST子问题field]
            return q.getType() == QuestionType.LIST ? parent : null;
        return String.format("%s/%s", parent, field);
    }

    private String assignKey(String parent, Question q) {
        String key = key(parent, q);
        if(key != null && q.getType() != QuestionType.LIST)
            q.setField(key);
        return key;
    }

    // 存储Question中的其他相关实体，确保存储q之前它的所有属性都是数据库中存在的
    @Transactional
    Question assignFields(Question q, String parent) {
        // 设置唯一field
        val key = assignKey(parent, q);
        // 递归设置子问题的缺失属性
        val list = q.getList();
        if(list != null && list.size() != 0) {
            Stream<Question> subq = list.stream().map(q0 -> this.assignFields(q0, key));
            q.setList(questions.saveAll(subq::iterator));
        }
        return q;
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
        Stream<Question> qss = qs.stream().map(q -> assignFields(q, "$"));
        section.setQuestions(questions.saveAll(qss::iterator));
        return sections.save(section);
    }

    @Transactional
    Task saveTask(Task task) {
        val group = accounts.findGroupByName(task.getCenter())
                     .orElseThrow(() -> notFound("group", task.getCenter()));
        task.setGroup(group).setSections(task.getSections().stream()
                                         .map(this::saveSection).collect(toList()));
        return tasks.save(task);
    }

}
