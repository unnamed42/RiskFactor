package com.tjh.riskfactor.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.tjh.riskfactor.service.TaskService;
import static com.tjh.riskfactor.repo.TaskRepository.TaskBrief;
import static com.tjh.riskfactor.error.ResponseErrors.notFound;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TaskController {

    private final TaskService service;

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
    List<?> sectionNames(@PathVariable Integer id) {
        return service.taskSectionsInfo(id);
    }
//
//    @GetMapping("/task/{id}/answers")
//    List<?> answers(@PathVariable Integer id) {
//
//    }
//
//    @GetMapping("task/{id}/{centerId}/answers")
//    List<?> centerAnswers(@PathVariable Integer id, @PathVariable Integer centerId) {
//
//    }

}
