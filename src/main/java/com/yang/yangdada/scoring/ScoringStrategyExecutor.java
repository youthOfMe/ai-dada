package com.yang.yangdada.scoring;

import com.yang.yangdada.annotation.ScoringStrategyConfig;
import com.yang.yangdada.common.ErrorCode;
import com.yang.yangdada.exception.BusinessException;
import com.yang.yangdada.model.entity.App;
import com.yang.yangdada.model.entity.UserAnswer;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ScoringStrategyExecutor {

    @Resource
    private List<ScoringStrategy> scoringStrategyList;

    public UserAnswer doScore(List<String> choiceList, App app) {
        Integer appType = app.getAppType();
        Integer appScoringStrategy = app.getScoringStrategy();
        if (appType == null || appScoringStrategy == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "应用配置有误, 未找到匹配的策略");
        }
        // 根据注解获取策略
        for (ScoringStrategy scoringStrategy : scoringStrategyList) {
            if (scoringStrategy.getClass().isAnnotationPresent(ScoringStrategyConfig.class)) {
                ScoringStrategyConfig scoringStrategyConfig = scoringStrategy.getClass().getAnnotation(ScoringStrategyConfig.class);
                if (scoringStrategyConfig.appType() == appType && scoringStrategyConfig.scoringStrategy() == appScoringStrategy) {
                    return scoringStrategy.doScore(choiceList, app);
                }
            }
        }
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "应用配置有误, 未找到匹配的策略");
    }
}
