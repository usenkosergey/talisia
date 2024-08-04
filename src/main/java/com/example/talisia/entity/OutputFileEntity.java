package com.example.talisia.entity;

import jakarta.persistence.*;

/**
 * @author Prokopenko Andrey
 * @since 04.08.2024
 */
@Entity
@Table(name = "output_file")
public class OutputFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "output_file")
    private String outputFile;
    @Column(name = "input_file")
    private String inputFile;
    @Column(name = "chat_id")
    private Long chatId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public String getInputFile() {
        return inputFile;
    }

    public void setInputFile(String inputFile) {
        this.inputFile = inputFile;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
}
