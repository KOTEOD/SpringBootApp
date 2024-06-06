package com.example.boot.modbus.service;

import org.springframework.stereotype.Component;

import org.springframework.stereotype.Component;

@Component
public class ModbusRTUConverter {

    public byte[] convertToRTU(byte[] tcpRequest) {
        if (tcpRequest.length < 8) {
            throw new IllegalArgumentException("Invalid Modbus TCP request");
        }

        byte[] rtuRequest = new byte[tcpRequest.length - 6 + 2];
        System.arraycopy(tcpRequest, 6, rtuRequest, 0, tcpRequest.length - 6);

        int crc = calculateCRC(rtuRequest, rtuRequest.length - 2);
        rtuRequest[rtuRequest.length - 2] = (byte) (crc & 0xFF);
        rtuRequest[rtuRequest.length - 1] = (byte) ((crc >> 8) & 0xFF);

        return rtuRequest;
    }

    public byte[] convertToTCP(byte[] rtuResponse) {
        if (rtuResponse.length < 3) {
            throw new IllegalArgumentException("Invalid Modbus RTU response");
        }

        byte[] tcpResponse = new byte[rtuResponse.length - 2 + 6];
        tcpResponse[0] = 0;
        tcpResponse[1] = 0;

        tcpResponse[2] = 0;
        tcpResponse[3] = 0;

        int length = rtuResponse.length - 2 + 1;
        tcpResponse[4] = (byte) ((length >> 8) & 0xFF);
        tcpResponse[5] = (byte) (length & 0xFF);

        System.arraycopy(rtuResponse, 0, tcpResponse, 6, rtuResponse.length - 2);

        return tcpResponse;
    }

    private int calculateCRC(byte[] data, int length) {
        int crc = 0xFFFF;

        for (int pos = 0; pos < length; pos++) {
            crc ^= (data[pos] & 0xFF);

            for (int i = 8; i != 0; i--) {
                if ((crc & 0x0001) != 0) {
                    crc >>= 1;
                    crc ^= 0xA001;
                } else {
                    crc >>= 1;
                }
            }
        }

        return crc;
    }
}