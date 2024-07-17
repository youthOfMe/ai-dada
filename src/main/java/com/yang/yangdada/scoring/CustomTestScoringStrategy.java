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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomTestScoringStrategy implements ScoringStrategy {

    @Resource
    private QuestionService questionService;

    @Resource
    private ScoringResultService scoringResultService;

    @Override
    public UserAnswer doScore(List<String> choice, App app) {
        // 1. 根据ID查询到题目和题目结果信息
        Question question = questionService.getOne(
                Wrappers.lambdaQuery(Question.class).eq(Question::getAppId, app.getId())
        );
        List<ScoringResult> scoringResultList = scoringResultService.list(
                Wrappers.lambdaQuery(ScoringResult.class)
                        .eq(ScoringResult::getAppId, app.getId())
        );

        // 2. 统计用户每个选择对应的属性个数, 如 I = 10个 E = 5个
        Map<String, Integer> optionCount = new HashMap<>();
        QuestionVO questionVO = QuestionVO.objToVo(question);
        List<QuestionContentDTO> questionContent = questionVO.getQuestionContent();

        // 遍历题目列表
        for (int i = 0; i < questionContent.size(); i++) {
            // 获取题目的答案
            String answer = choice.get(i);

            // 遍历题目中的选项
            for (QuestionContentDTO.Option option : questionContent.get(i).getOptions()) {
                if (option.getKey().equals(answer)) {
                    String result = option.getResult();

                    if (!optionCount.containsKey(result)) {
                        optionCount.put(result, 0);
                    }

                    optionCount.put(result, optionCount.get(result) + 1);
                }
            }
        }

        // 3. 遍历每种评分的结果，计算哪个结果的得分更高
        // 初始化最高分数和最低分数对应的评分结果
        int maxScore = 0;
        ScoringResult maxScoreResult = scoringResultList.get(0);

        // 遍历评分结果列表
        for (ScoringResult scoringResult : scoringResultList) {
            List<String> resultProp = JSONUtil.toList(scoringResult.getResultProp(), String.class);
            // 计算当前评分结果的分数
            int score = resultProp.stream().mapToInt(prop -> optionCount.getOrDefault(prop, 0)).sum();

            if (score > maxScore) {
                maxScore = score;
                maxScoreResult = scoringResult;
            }
        }

        // 4.构造返回值
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAppId(app.getId());
        userAnswer.setAppType(app.getAppType());
        userAnswer.setScoringStrategy(app.getScoringStrategy());
        userAnswer.setChoices(JSONUtil.toJsonStr(choice));
        userAnswer.setResultId(maxScoreResult.getId());
        userAnswer.setResultName(maxScoreResult.getResultName());
        userAnswer.setResultDesc(maxScoreResult.getResultDesc());
        userAnswer.setResultPicture(maxScoreResult.getResultPicture());
        return userAnswer;
    }
}
