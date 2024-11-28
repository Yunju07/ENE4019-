import java.io.*;
import java.net.*;

/**
 * WebClient class implements a simple web client.
 * Its primary responsibilities include:
 * 1. Initializing the UDP connection to web server
 * 2. Sending HTTP request and receiving HTTP response
 */
public class UDPWebClient {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 6789;
        String resource = "/index.html";

        try {
            // Mission 1. InetAddress와 DatagramSocket 초기화
            InetAddress address = InetAddress.getByName(host);
            DatagramSocket socket = new DatagramSocket();

            /**
             * Improve your HTTP Client to provide other request Methods(POST, DELETE, …)
             * and also improve to handle headers(Content-Type, User-Agent, …)
             */
            // Mission 2. HTTP 요청 메시지 생성 및 전송
            String requestMessage = "GET " + resource + " HTTP/1.0\r\n" +
                    "User-Agent: UDPWebClient\r\n" +
                    "Host: " + host + "\r\n\r\n";
            byte[] sendData = requestMessage.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
            socket.send(sendPacket);

            // Mission 3. 응답 데이터 초기화 및 수신
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receivePacket);

            // 응답 메시지 출력
            String responseMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
            System.out.println("Response from server:");
            System.out.println(responseMessage);

            // 소켓 닫기
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
