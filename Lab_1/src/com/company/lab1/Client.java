package com.company.lab1;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
public class Client {
    private static InetAddress address;
    private static InetAddress addressO;
    private static byte[] buffer;
    private static byte[] bufferO;
    private static DatagramPacket packet;
    private static DatagramPacket packetO;
    private static String str;
    private static String strO;
    private static String name;
    private static MulticastSocket socket;
    private static BufferedReader in = null;


    public static void main(String arg[]) throws Exception {
        // System.out.println("Ожидание сообщения от сервера");
        try {
            // Создание объекта MulticastSocket, чтобы получать
            // данные от группы, используя номер порта 1502
            socket = new MulticastSocket(1502);
            address = InetAddress.getByName("233.0.0.1");
            // Регистрация клиента в группе
            socket.joinGroup(address);
            in = new BufferedReader(new InputStreamReader(System.in));

            System.out.println(
                    "Введите имя : ");
            name = in.readLine();
            while (true) {
                // Отправка
                System.out.println(
                        "Введите строку для передачи серверу: ");
                strO = in.readLine();
                strO = name + ": " + strO;
                bufferO = strO.getBytes();
                addressO = InetAddress.getByName("localhost");
                // Посылка пакета датаграмм на порт номер 1502
                packetO = new DatagramPacket(
                        bufferO,
                        bufferO.length,
                        addressO,
                        1500);
                // Посылка сообщений всем клиентам в группе
                socket.send(packetO);

                // Прием
                buffer = new byte[256];
                packet = new DatagramPacket(
                        buffer, buffer.length);
                // Получение данных от сервера
                socket.receive(packet);
                str = new String(packet.getData());
                System.out.println(
                        "Получено сообщение: " + str.trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Удаление клиента из группы
                socket.leaveGroup(address);
                // Закрытие сокета
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
