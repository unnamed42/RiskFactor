package com.tjh.riskfactor.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.apache.poi.ss.usermodel.Cell;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tjh.riskfactor.util.ExcelReader;
import com.tjh.riskfactor.entity.form.*;
import com.tjh.riskfactor.repo.AnswerRepository;
import com.tjh.riskfactor.repo.AnswerEntryRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import static java.util.stream.Collectors.toMap;

@Service
@RequiredArgsConstructor
public class AnswerService implements IDBService<Answer> {

    private final TaskService tasks;
    private final UserService users;

    @Getter
    private final AnswerRepository repo;
    private final AnswerEntryRepository ansEntries;

    /**
     * 自Excel（xls，xlsx）格式导入回答（多个）
     * @param taskId 问题所属项目id
     * @param userId 导入动作执行者id
     * @param is 上传文件的输入流
     * @return 导入完成的数据库实体
     */
    @Transactional
    public Answer importFromExcel(Integer taskId, Integer userId, InputStream is) throws IOException {
        var task = tasks.checkedFind(taskId);
        var creator = users.checkedFind(userId);

        // 问卷的总体结构 查找表
        // 问题所属大纲标题(String) -> Pair<大纲id, 问题标签(String) -> 问题(Question)>
        var layout = task.getSections().stream().collect(toMap(
            s -> formatSectionTitle(s.getTitle()),
            s -> Pair.of(s, s.getQuestions().stream().collect(toMap(
                Question::getLabel, q -> q
            )))
        ));

        var answer = repo.save(new Answer().setTask(task).setCreator(creator)
            .setMtime(new Date()));

        try(var reader = new ExcelReader(is)) {
            var answers = reader.cells().map(dataCell -> {
                // 一级标题
                var h1 = dataCell.headerL1;
                // 二级标题，可能为空
                var h2 = dataCell.headerL2 == null ? "" : dataCell.headerL2;

                var title = String.format("%s/%s", trim(h1), trim(h2));
                // 问题标签
                var h3 = dataCell.header;
                var question = layout.get(title).getSecond().get(h3);

                return new AnswerEntry().setAnswer(answer)
                        .setQuestion(question).setValue(formatValue(dataCell.cell, question));
            });
            ansEntries.saveAll(answers::iterator);
        }

        return answer;
    }

    @Transactional(readOnly = true)
    String formatValue(Cell cell, Question question) {
        if(cell == null)
            return null;
        var content = cell.getStringCellValue();
        switch(question.getType()) {
            case TEXT: case NUMBER: case SINGLE_CHOICE:
                return content;

            case DATE:
                return formatDate(content);
            case YESNO_CHOICE:

            case LIST:
            default:
                return "";
        }
    }

    private static String formatSectionTitle(String title) {
        return title.replaceAll("（[^）]+）", "");
    }

    /**
     * 删除问题标题中的（单位），比如将"年龄（岁）"变成"年龄"
     * 同时去除前置/后置空格
     */
    private static String trim(String str) {
        var idx = str.lastIndexOf('（');
        if(idx == -1)
            return str;
        return str.substring(0, idx).trim();
    }

    /**
     * 重新格式化医院提供数据中的日期。认为输入内容已经是合法日期，不对数字加以验证。
     * @param str 日期字符串。为 年.月.日 格式，但月和日可能0填充至两位数，也可能不是
     * @return 解析完成后的日期，格式为 yyyy-MM-dd
     */
    private static String formatDate(String str) {
        var parts = str.split("\\.");
        var y = parts[0];
        var m = parts[1].length() == 1 ? '0' + parts[1] : parts[1];
        var d = parts[2].length() == 1 ? '0' + parts[2] : parts[2];
        return String.format("%s-%s-%s", y, m, d);
    }

    @Override
    public String getEntityName() {
        return "answer";
    }

}
