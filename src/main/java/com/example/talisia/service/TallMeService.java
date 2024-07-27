package com.example.talisia.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Usenko Sergey, 27.07.2024
 */
@Service
public class TallMeService {

    @Value("${TalkMe.Key}")
    private String key;

    public void tallMe() throws IOException {

        String body = "{ " +
                "  \"dateRange\": { " +
                "    \"start\": \"2024-07-26 00:00:00\", " +
                "    \"stop\": \"2024-07-27 19:45:00\" " +
                "  } " +
                "}";

        URL url = new URL("https://lcab.talk-me.ru/json/v1.0/chat/message/getList");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.addRequestProperty("Content-Type", "application/json");
        conn.addRequestProperty("X-Token", key);
        conn.setDoOutput(true);

        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
        writer.write(body);
        writer.flush();

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        StringBuilder response = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        System.out.println(response);

    }
}
