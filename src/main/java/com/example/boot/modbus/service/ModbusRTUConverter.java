package com.example.boot.modbus.service;

import org.springframework.stereotype.Component;

@Component
public class ModbusRTUConverter {

    ModbusRTUConverter() {}

    public byte[] convertToRTU(byte[] tcpRequest) {
        // Проверяем, что длина запроса Modbus TCP не менее 8 байт (минимальный размер заголовка)
        if (tcpRequest.length < 8) {
            throw new IllegalArgumentException("Invalid Modbus TCP request");
        }

        // Преобразование: пропускаем первые 6 байт (MBAP Header) и добавляем CRC16
        byte[] rtuRequest = new byte[tcpRequest.length - 6 + 2];
        System.arraycopy(tcpRequest, 6, rtuRequest, 0, tcpRequest.length - 6);

        // Вычисление CRC16
        int crc = calculateCRC(rtuRequest, rtuRequest.length - 2);
        rtuRequest[rtuRequest.length - 2] = (byte) (crc & 0xFF);
        rtuRequest[rtuRequest.length - 1] = (byte) ((crc >> 8) & 0xFF);

        return rtuRequest;
    }

    public byte[] convertToTCP(byte[] rtuResponse) {
        // Проверяем, что длина ответа Modbus RTU не менее 3 байт (адрес устройства + код функции + данные)
        if (rtuResponse.length < 3) {
            throw new IllegalArgumentException("Invalid Modbus RTU response");
        }

        // Преобразование: добавляем MBAP Header
        byte[] tcpResponse = new byte[rtuResponse.length - 2 + 6];
        // Transaction Identifier (2 байта) - может быть любым
        tcpResponse[0] = 0;
        tcpResponse[1] = 0;
        // Protocol Identifier (2 байта) - всегда 0 для Modbus
        tcpResponse[2] = 0;
        tcpResponse[3] = 0;
        // Length (2 байта) - длина оставшегося сообщения
        int length = rtuResponse.length - 2 + 1; // данные + адрес устройства
        tcpResponse[4] = (byte) ((length >> 8) & 0xFF);
        tcpResponse[5] = (byte) (length & 0xFF);

        // Данные ответа Modbus RTU без CRC
        System.arraycopy(rtuResponse, 0, tcpResponse, 6, rtuResponse.length - 2);

        return tcpResponse;
    }

    private int calculateCRC(byte[] data, int length) {
        int crc = 0xFFFF;

        for (int pos = 0; pos < length; pos++) {
            crc ^= (data[pos] & 0xFF); // XOR byte into least sig. byte of crc

            for (int i = 8; i != 0; i--) { // Loop over each bit
                if ((crc & 0x0001) != 0) { // If the LSB is set
                    crc >>= 1; // Shift right and XOR 0xA001
                    crc ^= 0xA001;
                } else {
                    crc >>= 1; // Just shift right
                }
            }
        }

        return crc;
    }
}
