package ro.socketandthreadpractice;

import java.io.*;
import java.net.*;
import java.util.*;

public class VerySimpleChatServer {

    ArrayList<PrintWriter> clientOutputStreams;

    public class ClientHandler implements Runnable {				// A class that implements Runnable, where I defined the work that a thread will perform.
        BufferedReader reader;
        Socket sock;

        public ClientHandler(Socket clientSocket) {
            try {
                sock = clientSocket;
                InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader(isReader);				// Chain a BufferReader  to a InputStreamReader to the input stream from the socket

            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }

        public void run() {											// The method that will be run by the new thread call stack.
            String message;
            try {
                while ((message = reader.readLine()) != null) {		// Read a line at a time, passing the line to the makeCard method, that parses it and turns it,
                    System.out.println("read " + message);			// Printing in the server terminal, all the message detected.
                    tellEveryonre(message);
                }
            } catch (Exception ex) {
                System.out.println("One client has left the chat");
                //ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        VerySimpleChatServer testServer = new VerySimpleChatServer();
        testServer.go();

    }

    public void go() {
        clientOutputStreams = new ArrayList<>();
        System.out.println("Server is up and running \nWainting for a connection");

        try {
            ServerSocket serverSock = new ServerSocket(5000);					// ServerSocket makes this server app 'listen' for client rezuests on port 5000 on the machine this code is running on.

            // The server goes into a permanent loop, waiting for (and servicing) client requests
            while(true) {
                Socket clientSocket = serverSock.accept();								// The accept method block (just sits and waiting) until a request comes in,
                // and them the method returns a Socket (on some anonymous port) for communicating with the client.

                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());	// Now we use the Socket connection to the client to make a PrintWriter
                clientOutputStreams.add(writer);										// Then we add the printwriter object to a list

                Thread t = new Thread(new ClientHandler(clientSocket));					// Make a thread and pass the ClientHandler (thread job) to it's constructor.
                t.start();																// Start the thread execution (it moves the thread into runnable state)
                System.out.println("got a connection");
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public void tellEveryonre(String message) {
        Iterator it = clientOutputStreams.iterator();
        while(it.hasNext()) {
            try {
                PrintWriter writer = (PrintWriter) it.next();
                writer.println(message);												// We send a String message, then we empty this printwriter from this current message.
                writer.flush();

            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}

