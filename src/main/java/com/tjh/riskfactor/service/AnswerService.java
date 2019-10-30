package com.tjh.riskfactor.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

import com.tjh.riskfactor.repo.AnswerRepository;
import static com.tjh.riskfactor.repo.AnswerRepository.AnswerBrief;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnswerService implements IDBService {

    private final AnswerRepository answers;
    private final AccountService accounts;

    @Override
    public void drop() {
        answers.deleteAll();
    }

    /**
     * 找到可被该用户读取的数据（自己创建的数据，若为组管理员则还包含本组成员创建的全部数据）
     * @param username 用户名
     * @return 所有Answer的id
     */
    public List<AnswerBrief> writableAnswers(String username) {
        val gid = accounts.findManagingGroupId(username);
        return gid.map(accounts::findMemberNamesByGid).map(answers::findAnswersForCreatorNames)
                .orElseGet(() -> answers.findAnswersByCreatorName(username));
    }

}
