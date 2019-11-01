package com.tjh.riskfactor.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.tjh.riskfactor.entity.form.*;
import com.tjh.riskfactor.entity.view.*;
import com.tjh.riskfactor.service.*;
import com.tjh.riskfactor.security.JwtUserDetails;
import static com.tjh.riskfactor.error.ResponseErrors.notFound;
import static com.tjh.riskfactor.util.Utils.kvMap;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;

@RestController
@RequiredArgsConstructor
public class TaskController {

    private final TaskService tasks;
    private final GroupService groups;
    private final UserService users;
    private final AnswerService answers;

    @GetMapping("/tasks")
    List<TaskBrief> availableTasks(Authentication auth) {
        return tasks.availableTasks(auth.getName());
    }

    @GetMapping("/task/{id}")
    TaskBrief task(@PathVariable Integer id) {
        return tasks.taskBrief(id)
               .orElseThrow(() -> notFound("task", id.toString()));
    }

    @GetMapping("/task/{id}/sections")
    List<Section> sections(@PathVariable Integer id) {
        return tasks.task(id).map(Task::getSections)
                .orElseThrow(() -> notFound("task", id.toString()));
    }

    @GetMapping("/task/{id}/sections/name")
    List<SectionBrief> sectionNames(@PathVariable Integer id) {
        return tasks.taskSectionsInfo(id);
    }

    @GetMapping("/task/{id}/answers")
    List<AnswerBrief> answers(@PathVariable Integer id, @AuthenticationPrincipal JwtUserDetails userDetails) {
        // 是root组，返回所有内容
        if(userDetails.isRoot())
            return tasks.taskAnswers(id);
        // 根据是否是组管理员，返回所有内容
        return users.managedGroupId(userDetails.userId())
            .map(gid -> tasks.centerAnswers(id, gid))
            .orElseGet(() -> tasks.userAnswers(id, userDetails.userId()));
    }

    @PostMapping("/task/{id}/answer")
    String postAnswer(@PathVariable Integer id, Authentication auth, @RequestBody Map<String, Map<String, Object>> body) {
        List<AnswerSection> parts = body.entrySet().stream().map(e -> {
            final var ans = new AnswerSection().setSectionPath(e.getKey()).setBody(e.getValue());
            return answers.saveAnswerSection(ans);
        }).collect(toList());
        Answer ans = answers.saveAnswer(id, auth.getName(), parts);
        return kvMap("id", ans.getId()).buildJson().get();
    }

    @PostMapping("/task/{id}/answer/file")
    String postAnswer(@PathVariable Integer id, Authentication auth, @RequestParam("file")MultipartFile file) throws IOException {
        final var mapper = new ObjectMapper();
        final var type = new TypeReference<Map<String, Map<String, Object>>>(){};
        return postAnswer(id, auth, mapper.readValue(file.getInputStream(), type));
    }

    @DeleteMapping("/task/{id}/answer/{aid}")
    void deleteAnswer(@PathVariable Integer id, @PathVariable Integer aid) {
        tasks.deleteTaskAnswer(aid);
    }

}
