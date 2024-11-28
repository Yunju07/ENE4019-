import java.io.*;
import java.net.*;
import java.util.*;

public final class UDPWebServer {
    public static void main(String argv[]) throws Exception {
        // Create a DatagramSocket to listen for incoming packets on port 6789.
        DatagramSocket socket = new DatagramSocket(6789); // Fill in #1

        // Process HTTP service requests in an infinite loop.
        while (true) {
            // Initialize buffer and packet for receiving data.
            byte[] receiveData = new byte[1024]; // Fill in #2
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length); // Fill in #2

            // Listen for a UDP packet.
            socket.receive(receivePacket); // Fill in #3

            // Construct an object to process the HTTP request message.
            UDPHttpRequest request = new UDPHttpRequest(receivePacket, socket); // Fill in #3

            // Create a new thread to process the request.
            Thread thread = new Thread(request);

            // Start the thread.
            thread.start();
        }
    }
}
