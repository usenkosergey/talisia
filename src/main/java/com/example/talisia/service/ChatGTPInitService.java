package com.example.talisia.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author Usenko Sergey, 27.07.2024
 */
@Service
public class ChatGTPInitService {

    @Value("${OpenAi.apiKey}")
    private String apiKey;

    //сообщение/вопрос от человека + ID


}
