package com.example.boot.modbus.service;

import com.example.boot.modbus.entity.SettingsEntity;
import com.example.boot.modbus.repository.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

@Component
public class ModbusTCPServer {
    private static final int DEFAULT_PORT = 502;

    private final ModbusRTUConverter rtuConverter;
    private final COMPortManager comPortManager;
    private final SettingsRepository settingsRepository;
    private final SQLiteService sqLiteService;

    @Autowired
    public ModbusTCPServer(ModbusRTUConverter rtuConverter, COMPortManager comPortManager, SettingsRepository settingsRepository, @Qualifier("SQLiteDatabaseManagerImpl") SQLiteService sqLiteService) {
        this.rtuConverter = rtuConverter;
        this.comPortManager = comPortManager;
        this.settingsRepository = settingsRepository;
        this.sqLiteService = sqLiteService;
    }

    @PostConstruct
    public void init() {
        // Загрузка настроек из базы данных
        SettingsEntity settings = settingsRepository.findTopByOrderByIdDesc();
        String settingsJson = sqLiteService.readSettings();
        // Можно десериализовать settingsJson, если необходимо

        // Открытие COM-порта с загруженными настройками
        try {
            comPortManager.openPort(settings.getPortName(), settings.getBaudRate(), settings.getDataBits(), settings.getStopBits(), settings.getParity());
            startServer(DEFAULT_PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void shutdown() {
        // Закрытие COM-порта
        comPortManager.closePort();
    }

    public void startServer(int port) {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    new Thread(() -> handleClient(clientSocket)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void handleClient(Socket clientSocket) {
        try (InputStream input = clientSocket.getInputStream();
             OutputStream output = clientSocket.getOutputStream()) {

            byte[] request = readRequest(input);
            byte[] response = processRequest(request);
            sendResponse(output, response);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] readRequest(InputStream input) throws IOException {
        byte[] buffer = new byte[256];
        int bytesRead = input.read(buffer);
        byte[] request = new byte[bytesRead];
        System.arraycopy(buffer, 0, request, 0, bytesRead);
        return request;
    }

    private void sendResponse(OutputStream output, byte[] response) throws IOException {
        output.write(response);
        output.flush();
    }

    private byte[] processRequest(byte[] request) {
        try {
            byte[] rtuRequest = rtuConverter.convertToRTU(request);
            comPortManager.writeData(rtuRequest);
            byte[] rtuResponse = comPortManager.readData();
            return rtuConverter.convertToTCP(rtuResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[]{};
        }
    }
}