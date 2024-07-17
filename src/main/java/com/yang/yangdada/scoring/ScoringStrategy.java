package com.yang.yangdada.scoring;

import com.yang.yangdada.model.entity.App;
import com.yang.yangdada.model.entity.UserAnswer;

import java.util.List;

/**
 * 评分策略
 */
public interface ScoringStrategy {

    /**
     * 执行评分
     *
     * @param choice
     * @param app
     * @return
     */
    UserAnswer doScore(List<String> choice, App app);
}
