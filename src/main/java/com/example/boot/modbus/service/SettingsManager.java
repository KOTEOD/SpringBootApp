package com.example.boot.modbus.service;

import com.example.boot.modbus.Settings;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class SettingsManager {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Settings loadSettings(String filePath) {
        try {
            return objectMapper.readValue(new File(filePath), Settings.class);
        } catch (IOException e) {
            e.printStackTrace();
            return new Settings(); // Возвращает пустые настройки в случае ошибки
        }
    }

    public void saveSettings(String filePath, Settings settings) {
        try {
            objectMapper.writeValue(new File(filePath), settings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
