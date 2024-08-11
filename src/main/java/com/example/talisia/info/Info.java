package com.example.talisia.info;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Usenko Sergey, 28.07.2024
 */
public class Info {


    public static JSONObject mainJson = new JSONObject();
    private static JSONObject bodyJson = new JSONObject();

    public static String desc = "Ты ассистент поликлиники «Талисия». Ты знаешь только про эту поликлинику. Больше у тебя нет никаких данных. Ты можешь отвечать только про поликлинику «Талисия». Если вопрос касается чего-то другого, ты должен отвечать – я ассистент поликлиники «Талисия». О поликлинике «Талисия». Более 10 лет заботимся о красоте и здоровье. Сеть клиник в Центральном и Южном регионах. Более 10.000 довольных клиентов. Система лояльности, накопительных скидок, абонементов, рассрочка. Ассоциация Отельеров АМОС* рекомендует! Лицензия на осуществление медицинской деятельности №Л041-01126-23/00643266. с опытом работы от 5 до 20+лет. адрес в г. Сочи - г. Сочи, ул. Красноармейская ул., 4/1. телефон в г. Сочи + 7 921 826-88-88. адрес в поселке городского типа - Красная Поляна, ул. Трудовой Славы, д.4Ежедневно ул. Трудовой Славы, д.4Ежедневно. телефон в поселке городского типа +7 862 299-88-50.";

    static {
        mainJson.put("method", "POST");
        mainJson.put("url", "/v1/chat/completions");

        bodyJson.put("model", "gpt-4o-mini");
        bodyJson.put("max_tokens", 1000);
        mainJson.put("body", bodyJson);
    }

    public static JSONObject getJsonPromt() {
        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("src/main/java/com/example/talisia/procedure.txt"));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            systemMessage.put("content", Info.desc + sb.toString());
//        systemMessage.put("content", "");
            return systemMessage;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
