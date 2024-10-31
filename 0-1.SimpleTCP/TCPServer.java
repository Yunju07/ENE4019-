import java.io.*;
import java.net.*;

class TCPServer {
    public static void main(String argv[]) throws Exception {
        String clientSentence;
        String capitalizedSentence;

        // Create server socket on port 6789
        ServerSocket welcomeSocket = new ServerSocket(6789);
        while (true) {

            // Accept connection from client
            Socket connectionSocket = welcomeSocket.accept();

            // Create input stream to receive data from client
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

            // Create output stream to send data to client
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

            // Read a line of data from client
            clientSentence = inFromClient.readLine();

            // Convert received data to uppercase and add newline character
            capitalizedSentence = clientSentence.toUpperCase() + '\n';

            // Send converted data back to client
            outToClient.writeBytes(capitalizedSentence);
        }
    }
}
