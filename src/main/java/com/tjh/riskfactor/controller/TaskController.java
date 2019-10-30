package com.tjh.riskfactor.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tjh.riskfactor.entity.form.Answer;
import com.tjh.riskfactor.entity.form.AnswerSection;
import com.tjh.riskfactor.service.AnswerService;
import lombok.RequiredArgsConstructor;

import lombok.val;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.tjh.riskfactor.entity.form.Section;
import com.tjh.riskfactor.entity.form.Task;
import com.tjh.riskfactor.entity.view.*;
import com.tjh.riskfactor.service.TaskService;
import com.tjh.riskfactor.service.GroupService;
import org.springframework.web.multipart.MultipartFile;

import static com.tjh.riskfactor.error.ResponseErrors.notFound;
import static com.tjh.riskfactor.util.Utils.isRoot;
import static com.tjh.riskfactor.util.Utils.kvMap;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class TaskController {

    private final TaskService service;
    private final GroupService groups;
    private final AnswerService answers;

    @GetMapping("/tasks")
    List<TaskBrief> availableTasks(Authentication auth) {
        return service.availableTasks(auth.getName());
    }

    @GetMapping("/task/{id}")
    TaskBrief task(@PathVariable Integer id) {
        return service.taskBrief(id)
               .orElseThrow(() -> notFound("task", id.toString()));
    }

    @GetMapping("/task/{id}/sections")
    List<Section> sections(@PathVariable Integer id) {
        return service.task(id).map(Task::getSections)
                .orElseThrow(() -> notFound("task", id.toString()));
    }

    @GetMapping("/task/{id}/sections/name")
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

    @PostMapping("/task/{id}/answer")
    String postAnswer(@PathVariable Integer id, Authentication auth, @RequestBody Map<String, Map<String, Object>> body) {
        List<AnswerSection> parts = body.entrySet().stream().map(e -> {
            val ans = new AnswerSection().setSectionPath(e.getKey()).setBody(e.getValue());
            return answers.saveAnswerSection(ans);
        }).collect(toList());
        Answer ans = answers.saveAnswer(id, auth.getName(), parts);
        return kvMap("id", ans.getId()).buildJson().get();
    }

    @PostMapping("/task/{id}/answer/file")
    String postAnswer(@PathVariable Integer id, Authentication auth, @RequestParam("file")MultipartFile file) throws IOException {
        val mapper = new ObjectMapper();
        val type = new TypeReference<Map<String, Map<String, Object>>>(){};
        return postAnswer(id, auth, mapper.readValue(file.getInputStream(), type));
    }

    @DeleteMapping("/task/{id}/answer/{aid}")
    void deleteAnswer(@PathVariable Integer id, @PathVariable Integer aid) {
        service.deleteTaskAnswer(aid);
    }

}
