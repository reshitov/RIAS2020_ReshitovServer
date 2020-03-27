package com.company.lab1;

import java.io.*;
import java.net.*;
public class Server {
    private BufferedReader in = null;
    private String str = null;
    private byte[] buffer;
    private DatagramPacket packet;
    private InetAddress address;
    private String strR = null;
    private byte[] bufferR;
    private DatagramPacket packetR;
    private InetAddress addressR;
    private DatagramSocket socket;
    public Server() throws IOException {
        // System.out.println("Sending messages");
        // Создается объект DatagramSocket, чтобы
        // принимать запросы клиента
        socket = new DatagramSocket(1500);
        // Вызов метода transmit(), чтобы передавать сообщение всем
        // клиентам, зарегистрированным в группе
        transmit();
    }
    public void transmit() {
        try {
            // Создается входной поток, чтобы принимать
            // данные с консоли
            in = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                //Принимаю
                bufferR = new byte[256];
                packetR = new DatagramPacket(
                        bufferR, bufferR.length);
                // Получение данных от сервера
                socket.receive(packetR);
                strR = new String(packetR.getData());
                System.out.println(
                        "Получено сообщение: " + strR.trim());

                //Отправляю
                buffer = strR.getBytes();
                address = InetAddress.getByName("233.0.0.1");
                // Посылка пакета датаграмм на порт номер 1502
                packet = new DatagramPacket(
                        buffer,
                        buffer.length,
                        address,
                        1502);
                // Посылка сообщений всем клиентам в группе
                socket.send(packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Закрытие потока и сокета
                in.close();
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String arg[]) throws Exception {
        // Запуск сервера
        new Server();
    }
}