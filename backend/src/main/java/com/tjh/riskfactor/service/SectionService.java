package com.tjh.riskfactor.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tjh.riskfactor.entity.form.*;
import com.tjh.riskfactor.repo.SectionRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SectionService {

    private final SectionRepository repo;

    public Optional<Section> section(Integer id) {
        return repo.findById(id);
    }

    @Transactional
    public List<Section> sectionsOfTask(Integer taskId) {
        var sections = repo.findAllByOwnerTaskId(taskId);
        // getQuestion().size()来触发延迟加载属性的加载动作
        sections.forEach(section ->
            section.getQuestions().size());
        return sections;
    }

}
