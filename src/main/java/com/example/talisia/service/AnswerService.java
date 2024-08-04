package com.example.talisia.service;

import com.example.talisia.entity.ChatEntity;
import com.example.talisia.entity.OutputFileEntity;
import com.example.talisia.repository.InitRepository;
import com.example.talisia.repository.OutputFileRepository;
import jakarta.transaction.Transactional;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Optional;

/**
 * @author Prokopenko Andrey
 * @since 04.08.2024
 */
@Service
public class AnswerService {

    @Value("${OpenAi.apiKey}")
    private String apiKey;

    @Autowired
    private OutputFileRepository outputFileRepository;
    @Autowired
    private InitRepository repository;

    @Scheduled(fixedRate = 10000)
    public void retrieveAnswers() {
        List<OutputFileEntity> outputFiles = outputFileRepository.findAll();

        outputFiles.forEach(outputFile -> {
            try {
                retrieve(outputFile);
                deleteFile(outputFile.getInputFile());
                deleteFile(outputFile.getOutputFile());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void deleteFile(String file) throws IOException {
        URL url = new URL("https://api.openai.com/v1/files/" + file);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("DELETE");
        con.setRequestProperty("Authorization", "Bearer " + apiKey);
        con.setDoOutput(true);

        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String line;
        StringBuilder response = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

    }

    @Transactional
    public void retrieve(OutputFileEntity outputFile) throws IOException {
        URL url = new URL("https://api.openai.com/v1/files/" + outputFile.getOutputFile() + "/content");
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
        JSONObject jsonObject = new JSONObject(response.toString());
        JSONObject jsonObject1 = jsonObject.getJSONObject("response");
        JSONObject body  = jsonObject1.getJSONObject("body");
        JSONArray choices = body.getJSONArray("choices");
        JSONObject jsonObject2 = choices.getJSONObject(0);
        JSONObject message = jsonObject2.getJSONObject("message");
        String content = message.getString("content");

        Optional<ChatEntity> optChat = repository.findById(outputFile.getChatId());
        if (optChat.isPresent()) {
            ChatEntity chat = optChat.get();
            chat.setAnswer(content);
            repository.save(chat);
            outputFileRepository.delete(outputFile);
        }
    }
}
