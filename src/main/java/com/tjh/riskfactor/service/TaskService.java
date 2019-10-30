package com.tjh.riskfactor.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tjh.riskfactor.entity.form.*;
import com.tjh.riskfactor.repo.QuestionRepository;
import com.tjh.riskfactor.repo.SectionRepository;
import com.tjh.riskfactor.repo.TaskRepository;
import static com.tjh.riskfactor.repo.TaskRepository.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TaskService implements IDBService {

    private final QuestionRepository questions;
    private final SectionRepository sections;
    private final TaskRepository tasks;

    @Transactional
    public void drop() {
        tasks.deleteAll();
    }

    public Optional<TaskBrief> findTaskInfoById(Integer id) {
        return tasks.findTaskInfoById(id);
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

    List<Section> saveSections(Stream<Section> sections) {
        return this.sections.saveAll(sections::iterator);
    }

    List<Question> saveQuestions(Stream<Question> questions) {
        return this.questions.saveAll(questions::iterator);
    }

    void saveTask(Task t) {
        tasks.save(t);
    }

}
