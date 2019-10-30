package com.tjh.riskfactor.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.tjh.riskfactor.service.TaskService;
import com.tjh.riskfactor.service.GroupService;
import static com.tjh.riskfactor.repo.TaskRepository.*;
import static com.tjh.riskfactor.repo.AnswerRepository.*;
import static com.tjh.riskfactor.error.ResponseErrors.notFound;
import static com.tjh.riskfactor.util.Utils.isRoot;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TaskController {

    private final TaskService service;
    private final GroupService groups;

    @GetMapping("/tasks")
    List<TaskBrief> availableTasks(Authentication auth) {
        return service.availableTasks(auth.getName());
    }

    @GetMapping("/task/{id}")
    TaskBrief task(@PathVariable Integer id) {
        return service.findTaskInfoById(id)
               .orElseThrow(() -> notFound("task", id.toString()));
    }

    @GetMapping("/task/{id}/sections")
    List<SectionBrief> sectionNames(@PathVariable Integer id) {
        return service.taskSectionsInfo(id);
    }

    @GetMapping("/task/{id}/answers")
    List<AnswerBrief> answers(@PathVariable Integer id, Authentication auth) {
        // 是root组，返回所有内容
        if(isRoot(auth))
            return service.taskAnswers(id);
        // 根据是否是组管理员，返回所有内容
        return groups.idManagedBy(auth.getName())
                .map(gid -> service.taskAnswers(id, gid))
                .orElseGet(() -> service.taskAnswers(id, auth.getName()));
    }

}
