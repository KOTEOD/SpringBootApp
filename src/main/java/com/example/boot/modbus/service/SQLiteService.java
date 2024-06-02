package com.example.boot.modbus.service;

import org.springframework.stereotype.Service;

@Service
public interface SQLiteService {
    void saveData(byte[] data);
    String readSettings();
    void updateSettings(String jsonSettings);
}
