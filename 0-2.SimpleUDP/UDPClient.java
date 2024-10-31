import java.io.*;
import java.net.*;

class UDPClient {
    public static void main(String args[]) throws Exception {

        // Create BufferedReader to read input from user
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

        // Create UDP socket
        DatagramSocket clientSocket = new DatagramSocket();


        // Get the server's IP address
        InetAddress IPAddress = InetAddress.getByName("fe80::b95:f33d:68eb:1877%3");

        // Create byte arrays for sending and receiving data
        byte[] sendData = new byte[1024];
        byte[] receiveData = new byte[1024];

        // Read a line of input from user
        String sentence = inFromUser.readLine();

        // Convert the input string to a byte array
        sendData = sentence.getBytes();

        // Create a packet to send (data, data length, IP address, port number x)
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);


        // Send the packet to the server
        clientSocket.send(sendPacket);


        // Create a packet to receive data (byte array to receive data)
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        // Receive the packet from the server
        clientSocket.receive(receivePacket);



        // Convert the received data to a string
        String modifiedSentence = new String(receivePacket.getData());
        // Print Sentence received from the server
        System.out.println("FROM SERVER:" + modifiedSentence);

        // Close the socket to end the connection
        clientSocket.close();
    }
}


