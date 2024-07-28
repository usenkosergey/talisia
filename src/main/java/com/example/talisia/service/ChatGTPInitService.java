package com.example.talisia.service;

import com.example.talisia.entity.ChatEntity;
import com.example.talisia.info.Info;
import okhttp3.*;
import org.antlr.v4.runtime.misc.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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

        String fileName = String.valueOf(System.currentTimeMillis());
        JSONArray jsonArray = new JSONArray();

        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");

        BufferedReader reader = new BufferedReader(new FileReader("src/main/java/com/example/talisia/procedure.txt"));
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();

        systemMessage.put("content", Info.desc + sb.toString());

        JSONObject clientMessage = new JSONObject();
        clientMessage.put("role", "user");
        clientMessage.put("content", question);

        jsonArray.put(systemMessage);
        jsonArray.put(clientMessage);

        JSONObject mainJson = Info.mainJson;
        JSONObject bodyJson = mainJson.getJSONObject("body");
        bodyJson.put("messages", jsonArray);
        mainJson.put("body", bodyJson);
        mainJson.put("custom_id", fileName);

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

        String id = "";

        // Выполнение запроса
//        try (Response response = client.newCall(request).execute()) {
//            if (!response.isSuccessful()) {
//                throw new IOException("Unexpected code " + response);
//            }
//
//            // Чтение ответа
//            JSONObject jsonResponse = new JSONObject(response.body().string());
//            id = jsonResponse.getString("id");
//            System.out.println(jsonResponse.toString(2));
//        }

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("input_file_id", id);
        jsonObject.put("endpoint", "/v1/chat/completions");
        jsonObject.put("completion_window", "24h");

        URL url = new URL("https://api.openai.com/v1/batches");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Authorization", "Bearer " + apiKey);
        con.setDoOutput(true);

//        OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
//        writer.write(jsonObject.toString());
//        writer.flush();
//        writer.close();

//        BufferedReader reader1 = new BufferedReader(new InputStreamReader(con.getInputStream()));
//        String line1;
//        StringBuilder response1 = new StringBuilder();
//        while ((line1 = reader1.readLine()) != null) {
//            response1.append(line1);
//        }
//        reader1.close();
//        System.out.println(response1.toString());

//        JSONObject jsonResponse1 = new JSONObject(response1.toString());

//        retrieve(jsonResponse1.getString("input_file_id"));

        String outputFile = retrieveBatch("batch_Mt44NPvR2cuFIjOFo9IbUop3");
        retrieve(outputFile);
        return null;

    }

    private String retrieveBatch(String batchId) throws IOException {
        URL url = new URL("https://api.openai.com/v1/batches/" + batchId);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
//        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Authorization", "Bearer " + apiKey);
        con.setDoOutput(true);

//        OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
////        writer.flush();
//        writer.close();

        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String line;
        StringBuilder response = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        System.out.println(response.toString());

        JSONObject jsonResponse = new JSONObject(response.toString());

        return jsonResponse.getString("output_file_id");
    }

    private void retrieve(String outputFileId) throws IOException {
        URL url = new URL("https://api.openai.com/v1/files/" + outputFileId + "/content");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
//        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Authorization", "Bearer " + apiKey);
        con.setDoOutput(true);

//        OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
////        writer.flush();
//        writer.close();

        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String line;
        StringBuilder response = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        System.out.println(response.toString());
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
        return null;
    }
}
