package com.tjh.riskfactor.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.tjh.riskfactor.entity.form.*;
import com.tjh.riskfactor.entity.view.*;
import com.tjh.riskfactor.service.*;
import com.tjh.riskfactor.security.JwtUserDetails;
import static com.tjh.riskfactor.error.ResponseErrors.notFound;

import java.io.IOException;
import java.util.List;

@CrossOrigin
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
    @GetMapping("/tasks")
    public List<TaskBrief> tasks() {
        return service.tasks();
    }

    /**
     * 获取某个项目的基础信息
     * @param id 项目id
     * @return 基础信息
     */
    @GetMapping("/tasks/{id}")
    public TaskBrief task(@PathVariable Integer id) {
        return service.taskBrief(id)
               .orElseThrow(() -> notFound("task", id.toString()));
    }

    @GetMapping("/tasks/{id}/sections")
    public List<Section> sections(@PathVariable Integer id) {
        return service.checkedFind(id).getSections();
    }

    @GetMapping("/tasks/{id}/sections/name")
    public List<SectionBrief> sectionNames(@PathVariable Integer id) {
        return service.taskSectionsInfo(id);
    }

    @GetMapping("/tasks/{id}/answers")
    public List<AnswerBrief> answers(@PathVariable Integer id, @AuthenticationPrincipal JwtUserDetails userDetails) {
        // 是root组，返回所有内容
        if(userDetails.isRoot())
            return service.taskAnswers(id);
        // 根据是否是组管理员，返回所有内容
        return users.managedGroupId(userDetails.getId())
            .map(gid -> service.centerAnswers(id, gid))
            .orElseGet(() -> service.userAnswers(id, userDetails.getId()));
    }

    @PostMapping("/tasks/{id}/answer/file")
    public void importAnswer(@PathVariable Integer id, @AuthenticationPrincipal JwtUserDetails details,
                             @RequestParam("file") MultipartFile file) throws IOException {
        answers.importFromExcel(id, details.getId(), file.getInputStream());
    }

}
