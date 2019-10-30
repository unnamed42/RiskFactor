package com.tjh.riskfactor.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.tjh.riskfactor.entity.form.Answer;
import com.tjh.riskfactor.entity.form.AnswerSection;
import com.tjh.riskfactor.repo.AnswerRepository;
import com.tjh.riskfactor.repo.AnswerSectionRepository;
import static com.tjh.riskfactor.error.ResponseErrors.notFound;

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

    public AnswerSection saveAnswerSection(AnswerSection ans) {
        return answerSections.save(ans);
    }

    public Answer saveAnswer(Integer taskId, String username, List<AnswerSection> parts) {
        val ans = new Answer().setParts(parts)
                .setCreator(users.userWithName(username).orElseThrow(() -> notFound("user", username)))
                .setTask(tasks.task(taskId).orElseThrow(() -> notFound("task", taskId.toString())))
                .setMtime(new Date());
        return answers.save(ans);
    }

}
