package com.example.talisia.service;

import com.example.talisia.entity.BatchEntity;
import com.example.talisia.entity.OutputFileEntity;
import com.example.talisia.record.FileRecord;
import com.example.talisia.repository.BatchRepository;
import com.example.talisia.repository.OutputFileRepository;
import jakarta.transaction.Transactional;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

/**
 * @author Prokopenko Andrey
 * @since 04.08.2024
 */
@Service
public class BatchService {

    @Value("${OpenAi.apiKey}")
    private String apiKey;

    @Autowired
    private BatchRepository batchRepository;
    @Autowired
    private OutputFileRepository outputFileRepository;
    private static final Logger logger = LoggerFactory.getLogger(BatchService.class);

    @Scheduled(fixedRate = 5000)
    @Transactional
    public void retrieveBatches() {
        logger.info("Retrieving batches from API ____________________________________");
        List<BatchEntity> batches = batchRepository.findAll();

        batches.forEach(batch -> {
            try {
                FileRecord fileRecord = retrieveBatch(batch.getBatchId());
                if (!fileRecord.inputFile().isEmpty() && !fileRecord.outputFile().isEmpty()) {
                    OutputFileEntity outputFileEntity = new OutputFileEntity();
                    outputFileEntity.setInputFile(fileRecord.inputFile());
                    outputFileEntity.setOutputFile(fileRecord.outputFile());
                    outputFileEntity.setChatId(batch.getChatId());
                    outputFileRepository.save(outputFileEntity);
                    batchRepository.delete(batch);
                    logger.info("Retrieved batch id ___________________" + batch.getBatchId());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public FileRecord retrieveBatch(String batchId) throws IOException {
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

        return new FileRecord(jsonResponse.optString("input_file_id"), jsonResponse.optString("output_file_id"));
    }
}
