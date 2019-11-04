package com.tjh.riskfactor.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.tjh.riskfactor.entity.form.Answer;
import com.tjh.riskfactor.entity.form.AnswerSection;
import com.tjh.riskfactor.repo.AnswerRepository;
import com.tjh.riskfactor.repo.AnswerSectionRepository;
import static com.tjh.riskfactor.error.ResponseErrors.notFound;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final TaskService tasks;
    private final UserService users;

    private final AnswerRepository answers;
    private final AnswerSectionRepository answerSections;

    public Optional<Answer> answer(Integer id) {
        return answers.findById(id);
    }

    public void delete(Integer id) {
        answers.deleteById(id);
    }

    public AnswerSection saveAnswerSection(AnswerSection ans) {
        return answerSections.save(ans);
    }

    public Answer saveAnswer(Integer taskId, String username, List<AnswerSection> parts) {
        var ans = new Answer().setParts(parts)
                .setCreator(users.find(username).orElseThrow(() -> notFound("user", username)))
                .setTask(tasks.checkedFind(taskId))
                .setMtime(new Date());
        return answers.save(ans);
    }

}
