import java.io.*;
import java.net.*;
import java.util.*;

final class UDPHttpRequest implements Runnable {
    final static String CRLF = "\r\n";

    // 전역 변수 선언
    private DatagramSocket socket;
    private DatagramPacket packet;

    // 생성자
    public UDPHttpRequest(DatagramPacket packet, DatagramSocket socket) throws Exception {
        this.packet = packet;
        this.socket = socket;
    }

    // Runnable 인터페이스 구현
    public void run() {
        try {
            processRequest();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void processRequest() throws Exception {
        // 요청 데이터 추출
        byte[] requestData = packet.getData();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(requestData);
        InputStreamReader inputStreamReader = new InputStreamReader(byteArrayInputStream);
        BufferedReader br = new BufferedReader(inputStreamReader);

        // 요청 라인 읽기
        String requestLine = br.readLine();
        System.out.println("Request: " + requestLine);

        // 파일 이름 추출
        StringTokenizer tokens = new StringTokenizer(requestLine);
        String method = tokens.nextToken();
        String fileName = tokens.nextToken();
        fileName = "." + fileName;

        // 파일 열기
        FileInputStream fis = null;
        boolean fileExists = true;
        try {
            fis = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            fileExists = false;
        }

        // 응답 메시지 생성
        String statusLine = null;
        String contentTypeLine = null;
        String contentLengthLine = null;
        String entityBody = null;

        if (fileExists) {
            statusLine = "HTTP/1.0 200 OK" + CRLF;
            contentTypeLine = "Content-Type: " + contentType(fileName) + CRLF;
            contentLengthLine = "Content-Length: " + getFileSizeBytes(fileName) + CRLF;
        } else {
            statusLine = "HTTP/1.0 404 Not Found" + CRLF;
            contentTypeLine = "Content-Type: text/html" + CRLF;
            entityBody = "<HTML><HEAD><TITLE>Not Found</TITLE></HEAD><BODY>Not Found</BODY></HTML>";
        }

        // 응답 헤더 작성
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(statusLine.getBytes());
        baos.write(contentTypeLine.getBytes());
        if (contentLengthLine != null) {
            baos.write(contentLengthLine.getBytes());
        }
        baos.write(CRLF.getBytes());

        // 엔티티 작성
        if (fileExists) {
            sendBytes(fis, baos);
            fis.close();
        } else {
            baos.write(entityBody.getBytes());
        }

        // 응답 패킷 전송
        byte[] responseBytes = baos.toByteArray();
        DatagramPacket responsePacket = new DatagramPacket(
                responseBytes, responseBytes.length, packet.getAddress(), packet.getPort());
        socket.send(responsePacket);

        // 스트림 닫기
        br.close();
    }

    private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception {
        byte[] buffer = new byte[1024];
        int bytes;
        while ((bytes = fis.read(buffer)) != -1) {
            os.write(buffer, 0, bytes);
        }
    }

    private static String contentType(String fileName) {
        if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
            return "text/html";
        }
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        if (fileName.endsWith(".ram") || fileName.endsWith(".ra")) {
            return "audio/x-pn-realaudio";
        }
        return "application/octet-stream";
    }

    private static long getFileSizeBytes(String fileName) throws IOException {
        File file = new File(fileName);
        return file.length();
    }
}
