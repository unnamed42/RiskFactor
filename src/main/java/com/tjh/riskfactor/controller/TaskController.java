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

    private final TaskService service;
    private final GroupService groups;
    private final UserService users;
    private final AnswerService answers;

    /**
     * 获取全部项目的基础信息
     * @return 全部项目的基础信息
     */
    @GetMapping("/task")
    public List<TaskBrief> tasks() {
        return service.tasks();
    }

    /**
     * 获取某个项目的基础信息
     * @param id 项目id
     * @return 基础信息
     */
    @GetMapping("/task/{id}")
    public TaskBrief task(@PathVariable Integer id) {
        return service.taskBrief(id)
               .orElseThrow(() -> notFound("task", id.toString()));
    }

    @GetMapping("/task/{id}/sections")
    public List<Section> sections(@PathVariable Integer id) {
        return service.checkedFind(id).getSections();
    }

    @GetMapping("/task/{id}/sections/name")
    public List<SectionBrief> sectionNames(@PathVariable Integer id) {
        return service.taskSectionsInfo(id);
    }

    @GetMapping("/task/{id}/answers")
    public List<AnswerBrief> answers(@PathVariable Integer id, @AuthenticationPrincipal JwtUserDetails userDetails) {
        // 是root组，返回所有内容
        if(userDetails.isRoot())
            return service.taskAnswers(id);
        // 根据是否是组管理员，返回所有内容
        return users.managedGroupId(userDetails.getId())
            .map(gid -> service.centerAnswers(id, gid))
            .orElseGet(() -> service.userAnswers(id, userDetails.getId()));
    }

    @PostMapping("/task/{id}/answer")
    public String postAnswer(@PathVariable Integer id, Authentication auth, @RequestBody Map<String, Map<String, Object>> body) {
        List<AnswerSection> parts = body.entrySet().stream().map(e -> {
            var ans = new AnswerSection().setSectionPath(e.getKey()).setBody(e.getValue());
            return answers.saveAnswerSection(ans);
        }).collect(toList());
        Answer ans = answers.saveAnswer(id, auth.getName(), parts);
        return kvMap("id", ans.getId()).buildJson().get();
    }

    @PostMapping("/task/{id}/answer/file")
    public String postAnswer(@PathVariable Integer id, Authentication auth, @RequestParam("file")MultipartFile file) throws IOException {
        var mapper = new ObjectMapper();
        var type = new TypeReference<Map<String, Map<String, Object>>>(){};
        return postAnswer(id, auth, mapper.readValue(file.getInputStream(), type));
    }

}
