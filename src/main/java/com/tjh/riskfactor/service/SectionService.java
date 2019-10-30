package com.tjh.riskfactor.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.tjh.riskfactor.entity.form.Section;
import com.tjh.riskfactor.repo.SectionRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SectionService {

    private final SectionRepository sections;

    public Optional<Section> section(Integer id) {
        return sections.findById(id);
    }

}
