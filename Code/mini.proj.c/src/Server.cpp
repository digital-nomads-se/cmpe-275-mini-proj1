#include <iostream>
#include <vector>
#include <thread>
#include <netinet/in.h>
#include <unistd.h>
#include <cstring> 
#include <cerrno> 
#include <atomic> 

class Server {
public:
    Server(int port);
    ~Server();
    void start();

private:
    void handleClient(int clientSocket);
    int port;
    std::vector<std::thread> threads;
};

Server::Server(int port) : port(port) {}

Server::~Server() {
    for (auto& t : threads) {
        if (t.joinable()) {
            t.join();
        }
    }
    threads.clear(); 
}

void Server::handleClient(int clientSocket) {
    std::atomic<bool> isRunning{true};

    std::thread readThread([this, clientSocket, &isRunning]() {
        char buffer[1024] = {0};
        while (isRunning) {
            memset(buffer, 0, sizeof(buffer));
            ssize_t valread = read(clientSocket, buffer, 1024);
            if (valread <= 0) {
                if (valread < 0) {
                    std::cerr << "Read failed: " << strerror(errno) << std::endl;
                } else {
                    std::cout << "Client closed the connection." << std::endl;
                }
                isRunning = false;
                break;
            }
            std::string message(buffer);
            if (message == "quit") {
                std::cout << "Client requested to quit." << std::endl;
                isRunning = false;
                break;
            }
            std::cout << "Received from client: " << buffer << std::endl;

            std::string response = "Server received your message.";
            ssize_t sent = send(clientSocket, response.c_str(), response.size(), 0);
            if (sent < 0) {
                std::cerr << "Send failed: " << strerror(errno) << std::endl;
                isRunning = false;
                break;
            }
        }
    });
    

    readThread.join();
    close(clientSocket);
}

void Server::start() {
    int serverSocket = socket(AF_INET, SOCK_STREAM, 0);
    if (serverSocket == -1) {
        std::cerr << "Failed to create socket." << std::endl;
        return;
    }

    sockaddr_in serverAddress{};
    serverAddress.sin_family = AF_INET;
    serverAddress.sin_port = htons(port);
    serverAddress.sin_addr.s_addr = INADDR_ANY;

    if (bind(serverSocket, (struct sockaddr*)&serverAddress, sizeof(serverAddress)) == -1) {
        std::cerr << "Failed to bind socket." << std::endl;
        return;
    }

    if (listen(serverSocket, 10) == -1) {
        std::cerr << "Failed to listen on socket." << std::endl;
        return;
    }

    std::cout << "Server is listening on port " << port << std::endl;

    while (true) {
        sockaddr_in clientAddress{};
        socklen_t clientSize = sizeof(clientAddress);
        int clientSocket = accept(serverSocket, (struct sockaddr*)&clientAddress, &clientSize);

        if (clientSocket == -1) {
            std::cerr << "Failed to accept client." << std::endl;
            continue;
        }

        std::cout << "Accepted a client." << std::endl;

        threads.push_back(std::thread([this, clientSocket]() {
            handleClient(clientSocket);
        }));
    }
}
