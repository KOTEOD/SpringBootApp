package com.example.boot.modbus.service;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

@Component
public class ModbusTCPServer {
    private static final int DEFAULT_PORT = 502;

    private final ModbusRTUConverter modbusRTUConverter;
    private final COMPortManager comPortManager;

    ModbusTCPServer(ModbusRTUConverter modbusRTUConverter,COMPortManager comportManager) {
        this.modbusRTUConverter = modbusRTUConverter;
        this.comPortManager = comportManager;
    }

    @PostConstruct
    public void init() {
        startServer(DEFAULT_PORT);
    }
    // Запуск сервера с многопоточным режимом соеденений
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
    //обработка соеденения с клиентом, принятие и передача запросов
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
    //чтение данные запроса добавление в масив и возвращение
    private byte[] readRequest(InputStream input) throws IOException {
        byte[] buffer = new byte[256];
        int bytesRead = input.read(buffer);
        byte[] request = new byte[bytesRead];
        System.arraycopy(buffer, 0, request, 0, bytesRead);
        return request;
    }
    //отправляет ответные данные в выходной поток
    private void sendResponse(OutputStream output, byte[] response) throws IOException {
        output.write(response);
        output.flush();
    }

    private byte[] processRequest(byte[] request) {
        try {
            byte[] rtuRequest = modbusRTUConverter.convertToRTU(request);

            comPortManager.writeData(rtuRequest);
            byte[] rtuResponse = comPortManager.readData();


            return modbusRTUConverter.convertToTCP(rtuResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[]{};
        }
    }
}
