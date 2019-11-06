package com.tjh.riskfactor.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.apache.poi.ss.usermodel.WorkbookFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tjh.riskfactor.entity.form.Answer;
import com.tjh.riskfactor.entity.form.AnswerSection;
import com.tjh.riskfactor.repo.AnswerRepository;
import com.tjh.riskfactor.repo.AnswerSectionRepository;
import static com.tjh.riskfactor.error.ResponseErrors.notFound;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnswerService implements IDBService<Answer> {

    private final TaskService tasks;
    private final UserService users;

    @Getter
    private final AnswerRepository repo;
    private final AnswerSectionRepository answerSections;

    public AnswerSection saveAnswerSection(AnswerSection ans) {
        return answerSections.save(ans);
    }

    public Answer saveAnswer(Integer taskId, String username, List<AnswerSection> parts) {
        var ans = new Answer().setParts(parts)
                .setCreator(users.find(username).orElseThrow(() -> notFound("user", username)))
                .setTask(tasks.checkedFind(taskId))
                .setMtime(new Date());
        return repo.save(ans);
    }

    @Transactional
    public Answer importFromExcel(Integer taskId, Integer userId, InputStream is) throws IOException {
        var ownerTask = tasks.checkedFind(taskId);
        var creator = users.checkedFind(userId);
        try (var document = WorkbookFactory.create(is)) {
            var sheet = document.getSheetAt(0);
            var parentHeader = sheet.getRow(0);
            var childHeader = sheet.getRow(1);
        }
        return null;
    }

    @Override
    public String getEntityName() {
        return "answer";
    }

}
