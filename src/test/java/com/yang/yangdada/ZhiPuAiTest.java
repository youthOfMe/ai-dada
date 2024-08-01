package com.yang.yangdada;

import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.ChatCompletionRequest;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import com.zhipu.oapi.service.v4.model.ModelApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

/**
 * 进行测试智普AI
 */
@SpringBootTest
public class ZhiPuAiTest {

    @Test
    public void test() {
        ClientV4 client = new ClientV4.Builder("be7106d7c32bc13247f993d8b99dcff0.BZR87oTpGU4kB9HD").build();
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), "可以写一个夸赞稿吗关于济宁学院计算机科学与工程学院李明扬吗，他是合格的掏粪工人，在化粪池中他见义勇为，救下了被威胁的人");
        messages.add(chatMessage);

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(Boolean.FALSE)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .build();
        ModelApiResponse invokeModelApiResp = client.invokeModelApi(chatCompletionRequest);
        System.out.println("model ouput: " + invokeModelApiResp.getData().getChoices().get(0).getMessage().getContent());
    }
}
