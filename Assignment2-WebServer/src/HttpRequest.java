fimport java.io.*;
import java.net.*;
import java.util.*;

final class HttpRequest implements Runnable {
    final static String CRLF = "\r\n";
    Socket socket;

    // Constructor
    public HttpRequest(Socket socket) throws Exception {
        this.socket = socket;
    }

    // Implement the run() method of the Runnable interface.
    public void run() {
        try {
            processRequest();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // ProcessRequest Method class handles HTTP Request Messages
    private void processRequest() throws Exception {
        // Mission 2: parse the HTTP request (Fill #5 ~ #7)
        InputStream is = socket.getInputStream(); // Mission 2: Get input stream from the socket
        DataOutputStream os = new DataOutputStream(socket.getOutputStream()); // Mission 2: Get Output stream from the socket

        // Set up input stream filters.
        BufferedReader br = new BufferedReader(new InputStreamReader(is)); // Mission 2: wrap InputStreamReader and BufferedReader filters around the input stream

        // Mission 2(2-A, 2-B, 2-C): parse the HTTP request (Fill #8 ~ #9)
        String requestLine = br.readLine(); // Mission 2: Get the request line of the HTTP request message

        // Fill #9: Use StringTokenizer to HTTP request
        StringTokenizer tokens = new StringTokenizer(requestLine);
        String method = tokens.nextToken(); // Mission 2-A: Get method information
        String fileName = tokens.nextToken(); // Mission 2-B: Get URI information
        String version = tokens.nextToken(); // Mission 2-C: Get HTTP Version information

        // Prepend a "." so that file request is within the current directory.
        System.out.println("Requested file path: " + fileName);
        System.out.println("Current directory: " + new File(".").getAbsolutePath());



        // Open the requested file.
        FileInputStream fis = null;
        boolean fileExists = true;
        try {
            fis = new FileInputStream("."+fileName);
        } catch (FileNotFoundException e) {
            fileExists = false;
        }

        // Debug info for private use
        System.out.println("Incoming!!!");
        System.out.println(requestLine);
        String headerLine = null;
        while ((headerLine = br.readLine()) != null && !headerLine.isEmpty()) {
            System.out.println(headerLine);
        }

        // Construct the response message.
        String statusLine = null;
        String contentTypeLine = null;
        String contentLengthLine = null;
        String entityBody = null;

        // Mission 3. Analyze the request and send an appropriate response
        if (fileExists) {
            // Fill #10. When requested file exists, Status Code 200 OK
            statusLine = "HTTP/1.1 200 OK" + CRLF; // Mission 3-A: Status Code 200 OK
            contentTypeLine = "Content-Type: " + contentType(fileName) + CRLF;

            contentLengthLine = "Content-Length: " + getFileSizeBytes(fileName) + CRLF;

        } else {
            // Fill #11. When requested file doesn’t exist, Status Code 404 NOT FOUND
            statusLine = "HTTP/1.1 404 Not Found" + CRLF; // Mission 3-B: Status Code 404 Not found
            contentTypeLine = "Content-Type: text/html" + CRLF;
            entityBody = "<HTML>" +
                    "<HEAD><TITLE>Not Found</TITLE></HEAD>" +
                    "<BODY>Not Found</BODY></HTML>";
        }

        /*
         * // Mission 3-C: Status Code 400 Bad Request // Check if the method is
         * supported (only GET is supported) if (!method.equals("GET")) { // Fill in the
         * 400 Bad Request response statusLine = "HTTP/1.1 400 Bad Request" + CRLF;
         * contentTypeLine = "Content-Type: text/html" + CRLF; entityBody = "<HTML>" +
         * "<HEAD><TITLE>Bad Request</TITLE></HEAD>" +
         * "<BODY>Bad Request: Unsupported HTTP Method</BODY></HTML>";
         * os.writeBytes(statusLine); os.writeBytes(contentTypeLine);
         * os.writeBytes(CRLF); // Blank line between headers and body
         * os.writeBytes(entityBody); os.close(); br.close(); socket.close(); return; //
         * Exit after sending 400 response }
         *
         * // Check if HTTP version is valid (only HTTP/1.0 or HTTP/1.1) if
         * (!version.equals("HTTP/1.0") && !version.equals("HTTP/1.1")) { // Fill in the
         * 400 Bad Request response for invalid HTTP version statusLine =
         * "HTTP/1.1 400 Bad Request" + CRLF; contentTypeLine =
         * "Content-Type: text/html" + CRLF; entityBody = "<HTML>" +
         * "<HEAD><TITLE>Bad Request</TITLE></HEAD>" +
         * "<BODY>Bad Request: Unsupported HTTP Version</BODY></HTML>";
         * os.writeBytes(statusLine); os.writeBytes(contentTypeLine);
         * os.writeBytes(CRLF); // Blank line between headers and body
         * os.writeBytes(entityBody); os.close(); br.close(); socket.close(); return; //
         * Exit after sending 400 response }
         */

        // Send the status line.
        os.writeBytes(statusLine);

        // Send the content type line.
        os.writeBytes(contentTypeLine);

        // Send the content length line.
        os.writeBytes(contentLengthLine);

        // Send a blank line to indicate the end of the header lines.
        os.writeBytes(CRLF);

        // Send the entity body.
        if (fileExists) {
            sendBytes(fis, os);
            fis.close();
        } else {
            os.writeBytes(entityBody); // Mission 3: Send appropriate entity body
        }

        // Close streams and socket.
        os.close();
        br.close();
        socket.close();
    }

    // Method to send file bytes to output stream
    private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception {
        byte[] buffer = new byte[1024];
        int bytes = 0;

        while ((bytes = fis.read(buffer)) != -1) {
            os.write(buffer, 0, bytes);
        }
    }

    // Method to determine the content type based on the file extension
    private static String contentType(String fileName) {
        if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
            return "text/html";
        }

        // Fill #12 Detect appropriate file extensions and return appropriate response type (audio)
        if (fileName.endsWith(".mp3")) {
            return "audio/mpeg";
        }

        // Fill #13 Detect appropriate file extensions and return appropriate response type (image)
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        }

        // Default content type
        return "application/octet-stream";
    }

    // Method to get the file size in bytes
    private static long getFileSizeBytes(String fileName) throws IOException {
        File file = new File(fileName);
        return file.length();
    }
}
