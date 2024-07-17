package com.yang.yangdada.scoring;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yang.yangdada.model.dto.question.QuestionContentDTO;
import com.yang.yangdada.model.entity.App;
import com.yang.yangdada.model.entity.Question;
import com.yang.yangdada.model.entity.ScoringResult;
import com.yang.yangdada.model.entity.UserAnswer;
import com.yang.yangdada.model.vo.QuestionVO;
import com.yang.yangdada.service.QuestionService;
import com.yang.yangdada.service.ScoringResultService;

import javax.annotation.Resource;
import java.util.List;

/**
 * 自定义测评类应用评分策略
 */
public class CustomScoreScoringStrategy implements ScoringStrategy {

    @Resource
    private QuestionService questionService;

    @Resource
    private ScoringResultService scoringResultService;

    @Override
    public UserAnswer doScore(List<String> choice, App app) {
        // 1. 根据ID查询到题目和题目结果信息(按分数降序排序)
        Question question = questionService.getOne(
                Wrappers.lambdaQuery(Question.class).eq(Question::getAppId, app.getId())
        );
        List<ScoringResult> scoringResultList = scoringResultService.list(
                Wrappers.lambdaQuery(ScoringResult.class)
                        .eq(ScoringResult::getAppId, app.getId()).orderByDesc(ScoringResult::getResultScoreRange)
        );

        // 2. 统计用户每个选择和答案是否符合, 进行统计分数
        int totalScore = 0;
        QuestionVO questionVO = QuestionVO.objToVo(question);
        List<QuestionContentDTO> questionContent = questionVO.getQuestionContent();

        // 遍历题目列表
        for (int i = 0; i < questionContent.size(); i++) {
            // 获取题目的答案
            String answer = choice.get(i);

            // 遍历题目中的选项
            for (QuestionContentDTO.Option option : questionContent.get(i).getOptions()) {
                if (option.getKey().equals(answer)) {
                    totalScore += option.getScore();
                }
            }
        }

        // 3. 返回最大得分结果
        ScoringResult result = new ScoringResult();
        for (ScoringResult scoringResult : scoringResultList) {
            if (totalScore >= scoringResult.getResultScoreRange()) {
                result = scoringResult;
                break;
            }
        }

        // 4. 构造返回信息
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAppId(app.getId());
        userAnswer.setAppType(app.getAppType());
        userAnswer.setScoringStrategy(app.getScoringStrategy());
        userAnswer.setChoices(JSONUtil.toJsonStr(choice));
        userAnswer.setResultId(result.getId());
        userAnswer.setResultName(result.getResultName());
        userAnswer.setResultDesc(result.getResultDesc());
        userAnswer.setResultPicture(result.getResultPicture());
        userAnswer.setResultScore(totalScore);
        return userAnswer;
    }
}
