package com.example.talisia.entity;

import jakarta.persistence.*;

/**
 * @author Prokopenko Andrey
 * @since 04.08.2024
 */
@Entity
@Table(name = "batch")
public class BatchEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "batch_id")
    private String batchId;
    @Column(name = "chat_id")
    private Long chatId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
}
