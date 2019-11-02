package com.tjh.riskfactor.service;

import lombok.Getter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.tjh.riskfactor.repo.*;
import com.tjh.riskfactor.entity.form.*;
import com.tjh.riskfactor.entity.view.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TaskService implements IDBService<Task> {

    private final QuestionRepository questions;
    private final SectionRepository sections;
    private final AnswerRepository answers;
    private final GroupService groups;

    @Getter
    private final TaskRepository repo;

    public Optional<TaskBrief> taskBrief(Integer id) {
        return repo.findTaskInfoById(id);
    }

    public List<TaskBrief> tasks() {
        return repo.findAllTasks();
    }

    /**
     * 获取项目下属的分节的基本信息
     * @param id 项目id
     * @return 所有分节的基本信息
     */
    public List<SectionBrief> taskSectionsInfo(Integer id) {
        return repo.findSectionNamesById(id);
    }

    /**
     * 获取项目下属的所有回答
     * @param taskId 项目id
     * @return 所有回答
     */
    public List<AnswerBrief> taskAnswers(Integer taskId) {
        return answers.findAllByTaskId(taskId);
    }

    /**
     * 获取项目下属的所有由某用户创建的回答
     * @param taskId 项目id
     * @param uid 用户id
     * @return 所有该用户创建的回答
     */
    public List<AnswerBrief> userAnswers(Integer taskId, Integer uid) {
        return answers.findTaskAnswersCreatedBy(taskId, uid);
    }

    /**
     * 获取项目下属所有由某用户组创建的回答
     * @param taskId 项目id
     * @param groupId 用户组id
     * @return 所有该用户组创建的回答
     */
    public List<AnswerBrief> centerAnswers(Integer taskId, Integer groupId) {
        var names = groups.memberNames(groupId);
        return answers.findAllByTaskIdCreatedBy(taskId, names);
    }

    public void deleteTaskAnswer(Integer answerId) {
        answers.deleteById(answerId);
    }

    /**
     * 获取分节
     * @param sid 分节id
     * @return 分节的全部信息
     */
    public Optional<Section> section(Integer sid) {
        return sections.findById(sid);
    }

    List<Section> saveSections(Stream<Section> sections) {
        return this.sections.saveAll(sections::iterator);
    }

    List<Question> saveQuestions(Stream<Question> questions) {
        return this.questions.saveAll(questions::iterator);
    }

    @Override
    public String getEntityName() {
        return "task";
    }
}
