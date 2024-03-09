package app;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import socket.BasicServer;

class ServerApp {
    private static final Logger logger = Logger.getLogger(ServerApp.class.getName());

    public ServerApp() {
    }

    public static void main(String[] args) {
        try {

            if (args.length < 2) {
                System.out.println("Usage: java ServerApp <host> <port>");
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

            var server = new BasicServer(host, port);
            try {
                // Start the server in a new thread
                new Thread(server::start).start();
                
                // Wait for the user to press ENTER to stop the server
                try (Scanner scanner = new Scanner(System.in)) {
                    System.out.println("Press ENTER to stop the server");
                    scanner.nextLine();
                }
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "An error occurred while running the server", ex);
            } finally {
                // Stop the server
                server.stop();
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}