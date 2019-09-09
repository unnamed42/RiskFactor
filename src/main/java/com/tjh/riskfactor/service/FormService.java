package com.tjh.riskfactor.service;

import com.tjh.riskfactor.entity.form.Answer;
import com.tjh.riskfactor.entity.form.Section;
import com.tjh.riskfactor.repo.AnswerRepository;
import com.tjh.riskfactor.repo.QuestionRepository;
import com.tjh.riskfactor.repo.SectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.tjh.riskfactor.service.Errors.notFound;

@Service
@RequiredArgsConstructor
public class FormService {

    private final AnswerRepository answers;
    private final QuestionRepository questions;
    private final SectionRepository sections;

    public Section fetchFormSection(String sectionTitle) {
        return sections.findByTitle(sectionTitle)
                .orElseThrow(() -> notFound("form", sectionTitle));
    }

    public void acceptAnswers(List<Answer> answers, String sectionTitle) {

    }

}
