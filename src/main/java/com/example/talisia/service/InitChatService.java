package com.example.talisia.service;

import com.example.talisia.entity.ChatEntity;
import com.example.talisia.info.Info;
import com.example.talisia.repository.InitRepository;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * @author Usenko Sergey, 11.08.2024
 */
@Service
//@RequestScope
public class InitChatService {
    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Value("${chat.model}")
    private String model;

    @Autowired
    private InitRepository repository;

    String urlOi = "https://api.openai.com/v1/chat/completions";

    public void init1() {
        String id = "232132423423";
        List<String> question = Arrays.asList(
                "Привет, ты кто",
                "Обращайся ко мне Василий",
                "Что такое Талисия",
                "Где находится?",
                "Как меня зовут",
                "какая процедура мне лучше всего подойдет для лица?"
        );

        for (String s : question) {
            System.out.println("вопрос :" + s);
            System.out.println("ответ :" + init(id, s));
        }

    }
    public String init(String id, String question) {
//        String id = "232132423423";
//        String question = "Привет, ты кто";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("model", model);
        jsonObject.put("temperature", 1);

        JSONArray messagesArr = new JSONArray();
        messagesArr.put(Info.getJsonPromt());

        List<ChatEntity> allPreviousConversations = repository.findAllByUserIdAndAnswerIsNotNullOrderById(id);
        for (ChatEntity chatEntity : allPreviousConversations) {
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", chatEntity.getQuestion());
            messagesArr.put(userMessage);

            JSONObject assistantMessage = new JSONObject();
            assistantMessage.put("role", "assistant");
            assistantMessage.put("content", chatEntity.getAnswer());
            messagesArr.put(assistantMessage);
        }

        JSONObject userLastMessage = new JSONObject();
        userLastMessage.put("role", "user");
        userLastMessage.put("content", question);
        messagesArr.put(userLastMessage);
        jsonObject.put("messages", messagesArr);

        ChatEntity chatEntity = new ChatEntity();
        chatEntity.setQuestion(question);
        chatEntity.setUserId(id);
        repository.save(chatEntity);

        String content;
        try {
            content = getAnswerFromChatGpt(urlOi, apiKey, jsonObject.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

//        System.out.println(content);

        chatEntity.setAnswer(content);
        repository.save(chatEntity);

        return content;
    }

    private String getAnswerFromChatGpt(String urlOi, String apiKey, String body) throws IOException {
        URL url = new URL(urlOi);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Authorization", "Bearer " + apiKey);
        con.setDoOutput(true);

        sendRequestToChatGpt(body, con);
        String response = getResponseFromChatGpt(con);

        JSONObject responseObject = new JSONObject(response);
        JSONArray choices = responseObject.getJSONArray("choices");
        JSONObject messageObject = choices.getJSONObject(0).getJSONObject("message"); //todo избавиться от индекса 0
        String content = messageObject.getString("content");
        return content;
    }

    private @NotNull String getResponseFromChatGpt(HttpURLConnection con) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String line;
        StringBuilder response = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        return response.toString();
    }

    private void sendRequestToChatGpt(String body, HttpURLConnection con) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
        writer.write(body);
        writer.flush();
        writer.close();
    }

}
