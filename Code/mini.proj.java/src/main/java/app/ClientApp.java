package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import socket.BasicClient;

public class ClientApp {

    private static final Logger logger = Logger.getLogger(ClientApp.class.getName());

    public ClientApp() {
    }

    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("Usage: java ClientApp <host> <port>");
            return;
        }
    
        String host = args[0];
        int port;
    
        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid port number. Please provide a valid port number.");
            return;
        }

        var myClient = new BasicClient("app", host, port);
        try {
            myClient.connect();
            myClient.join("group_chat");

            var br = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                System.out.print("\nenter message ('exit' to quit): ");
                var m = br.readLine();
                if (m.length() == 0 || "exit".equalsIgnoreCase(m)) {
                    break;
                }

                myClient.sendMessage(m);
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "An error occurred while reading input or sending message", ex);
        } finally {
            myClient.stop();
        }
    }
}
