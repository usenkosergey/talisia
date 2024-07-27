package com.example.talisia.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author Usenko Sergey, 27.07.2024
 */
@Service
public class MainService {

    @Autowired
    private final TallMeService tallMeService;

    public MainService(TallMeService tallMeService) {
        this.tallMeService = tallMeService;
    }

    public void main() {
        while (true) {
            try {
                tallMeService.tallMe();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
