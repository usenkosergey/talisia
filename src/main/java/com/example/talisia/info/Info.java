package com.example.talisia.info;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Usenko Sergey, 28.07.2024
 */
public class Info {


    public static JSONObject mainJson = new JSONObject();
    private static JSONObject bodyJson = new JSONObject();

    static {
        mainJson.put("method", "POST");
        mainJson.put("url", "/v1/chat/completions");

        bodyJson.put("model", "gpt-4o-mini");
        bodyJson.put("max_tokens", 1000);
        mainJson.put("body", bodyJson);
    }

    public static String desc = "Ты ассистент поликлиники «Талисия». Ты знаешь только про эту поликлинику. Больше у тебя нет никаких данных. Ты можешь отвечать только про поликлинику «Талисия». Если вопрос касается чего-то другого, ты должен отвечать – я ассистент поликлиники «Талисия». О поликлинике «Талисия». Более 10 лет заботимся о красоте и здоровье. Сеть клиник в Центральном и Южном регионах. Более 10.000 довольных клиентов. Система лояльности, накопительных скидок, абонементов, рассрочка. Ассоциация Отельеров АМОС* рекомендует! Лицензия на осуществление медицинской деятельности №Л041-01126-23/00643266. с опытом работы от 5 до 20+лет.\n";

}
