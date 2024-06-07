package com.example.boot.modbus.controller;

import com.example.boot.modbus.entity.SettingsEntity;
import com.example.boot.modbus.repository.SettingsRepository;
import com.example.boot.modbus.service.COMPortManager;
import com.example.boot.modbus.service.SQLiteService;
import com.fasterxml.jackson.databind.ObjectMapper; // Импорт класса ObjectMapper
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/modbus")
public class ModbusTcpServerController {

    private final SettingsRepository settingsRepository;
    private final COMPortManager comPortManager;
    private final SQLiteService sqLiteService;
    private final ObjectMapper objectMapper; // Заменяем Gson на ObjectMapper

    @Autowired
    public ModbusTcpServerController(SettingsRepository settingsRepository, COMPortManager comPortManager, @Qualifier("SQLiteDatabaseManagerImpl") SQLiteService sqLiteService) {
        this.settingsRepository = settingsRepository;
        this.comPortManager = comPortManager;
        this.sqLiteService = sqLiteService;
        this.objectMapper = new ObjectMapper(); // Инициализация ObjectMapper
    }

    @GetMapping
    public SettingsEntity getSettings() {
        return settingsRepository.findTopByOrderByIdDesc();
    }

    @PostMapping
    public void updateSettings(@RequestBody SettingsEntity newSettings) {
        // Сохранение новых настроек в базе данных
        settingsRepository.save(newSettings);

        try {
            // Обновление настроек в SQLite через сервис
            String jsonSettings = objectMapper.writeValueAsString(newSettings);
            sqLiteService.updateSettings(jsonSettings);
        } catch (Exception e) {
            e.printStackTrace();
            // Обработка исключений при сериализации объекта в JSON
        }

        // Перезапуск COM-порта с новыми настройками
        comPortManager.closePort();
        comPortManager.openPort(newSettings.getPortName(), newSettings.getBaudRate(), newSettings.getDataBits(), newSettings.getStopBits(), newSettings.getParity());
    }
}