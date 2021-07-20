package ro.socketandthreadpractice;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SimpleChatClient {
    JTextArea incoming;
    JTextField outgoing;
    BufferedReader reader;
    PrintWriter writer;
    Socket sock;

    public static void main(String[] args) {
        new SimpleChatClient().go();
    }

    public void go() {
        //let's build the GUI

        JFrame frame = new JFrame("Ludicrously Simple Chat Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel mainPanel = new JPanel();
        outgoing = new JTextField(20);
        incoming = new JTextArea(15, 50);
        incoming.setLineWrap(true);									// Sets the line-wrapping policy of the text area. If set to true the lines will be wrapped if they are too long to fit within the allocated width.
        incoming.setWrapStyleWord(true);							// If set to true the lines will be wrapped at word boundaries (whitespace). If set to false, the lines will be wrapped at character boundaries.
        incoming.setEditable(false);

        JScrollPane qScroller = new JScrollPane(incoming);			// Sets the ScrollPane, where the question TextField will be placed
        qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        JButton sendButton = new JButton("Send");					// The message will be sent to the Server, after the "Send" button action
        sendButton.addActionListener(new SendButtonListener());

        mainPanel.add(qScroller);
        mainPanel.add(outgoing);
        mainPanel.add(sendButton);

        setUpNetworking();											// We create the Socket and the PrintWriter, right before displaying the app GUI.

        Thread readerThread = new Thread(new IncomingReader());		// This starts a new thread using a new inner class, as the Runnable(job) for the thread.
        readerThread.start();										// The thread's job is to read from the server's socket stream, displaying incoming messages.

        frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
        frame.setSize(650, 500);
        frame.setVisible(true);
    }

    private void setUpNetworking() {
        try {
            sock = new Socket("127.0.0.1", 5000);					// We're using localhost, so we can test the client and server on one machine.
            InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());		// We are using the socket to get the input stream so the 'reader' thread can get messages from the server.
            reader = new BufferedReader(streamReader);
            writer = new PrintWriter(sock.getOutputStream());									// We are using output stream to send the message to the server..
            System.out.println("networking established");

        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    public class SendButtonListener implements ActionListener {		// SendButtonListener is the inner class that implements ActionListener interface
        public void actionPerformed(ActionEvent ev) {
            try {
                writer.println(outgoing.getText());					// When the user clicks send button, this method sends the contents of the text field to the server.
                writer.flush();

            } catch(Exception ex) {
                ex.printStackTrace();
            }
            outgoing.setText("");
            outgoing.requestFocus();
        }
    }

    public class IncomingReader implements Runnable {			// This is what the new thread does! In the run() method, it stays in a loop(as long as what it gets from the server is not null),
        public void run() {										// reading a line at a time, and adding each line to the scrolling text area.
            String message;

            try {
                while ((message = reader.readLine()) != null) {
                    System.out.println("read-> " + message);
                    incoming.append(message + "\n");			// The messages will be added one after the other using JTextArea append method.
                }
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}



