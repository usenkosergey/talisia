package com.example.talisia.service;

import com.example.talisia.entity.ChatEntity;
import com.example.talisia.info.Info;
import okhttp3.*;
import org.antlr.v4.runtime.misc.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Usenko Sergey, 27.07.2024
 */
@Service
public class ChatGTPInitService {

    @Value("${OpenAi.apiKey}")
    private String apiKey;
    private static final String UPLOAD_URL = "https://api.openai.com/v1/files";

    //сообщение/вопрос от человека + ID


    public void temp() {
        String id = "232132423423";
        String question = "Привет, ты кто?";

        ChatEntity chatEntity = new ChatEntity();
        chatEntity.setQuestion(question);
        chatEntity.setCliendId(id);
        try {
            String answer = getAnswerFromGPT(question);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private String getAnswerFromGPT(String question) throws IOException {

        JSONArray jsonArray = new JSONArray();

        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", Info.desc + Info.procedure);

        JSONObject clientMessage = new JSONObject();
        clientMessage.put("role", "user");
        clientMessage.put("content", question);

        jsonArray.put(systemMessage);
        jsonArray.put(clientMessage);

        JSONObject mainJson = Info.mainJson;
        JSONObject bodyJson = mainJson.getJSONObject("body");
        bodyJson.put("messages", jsonArray);
        mainJson.put("body", bodyJson);

        byte[] bytes = mainJson.toString().getBytes();

        OkHttpClient client = new OkHttpClient();

        RequestBody fileBody = RequestBody.create(bytes, MediaType.parse("application/jsonl"));
        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", String.valueOf(System.currentTimeMillis()), fileBody)
                .addFormDataPart("purpose", "batch")
                .build();

        // Подготовка запроса
        Request request = new Request.Builder()
                .url(UPLOAD_URL)
                .post(formBody)
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

        // Выполнение запроса
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            // Чтение ответа
            JSONObject jsonResponse = new JSONObject(response.body().string());
            System.out.println(jsonResponse.toString(2));
        }

        return null;

    }

    private void sendRequestToChatGpt(String body, HttpURLConnection con) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
        writer.write(body);
        writer.flush();
        writer.close();
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

    private String getAnswerFromChatGpt(String urlOi, String body) throws IOException {
        URL url = new URL(urlOi);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Authorization", "Bearer " + apiKey);
        con.setDoOutput(true);

        sendRequestToChatGpt(body, con);
        String response = getResponseFromChatGpt(con);

//        JSONObject responseObject = new JSONObject(response);
//        JSONArray choices = responseObject.getJSONArray("choices");
//        JSONObject messageObject = choices.getJSONObject(0).getJSONObject("message"); //todo избавиться от индекса 0
//        String content = messageObject.getString("content");
        return content;
    }
}
