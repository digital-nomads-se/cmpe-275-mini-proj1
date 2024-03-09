#include "Client.hpp"
#include <iostream>
#include <sys/socket.h>
#include <netinet/in.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <cstring>
#include <thread>
#include <atomic>
#include <condition_variable>

std::condition_variable cv;
std::mutex cv_m;
bool received = true;

Client::Client(const std::string& host, int port) : host(host), port(port), sock(-1), isDisconnecting(false) {
    std::cout << "Client created. Preparing to connect to server at " << host << ":" << port << std::endl;
}


void Client::connectToServer() {
    sock = socket(AF_INET, SOCK_STREAM, 0);
    if (sock < 0) {
        std::cerr << "Failed to create socket." << std::endl;
        return;
    }

    sockaddr_in server_addr{};
    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(port);

    if (inet_pton(AF_INET, host.c_str(), &server_addr.sin_addr) <= 0) {
        std::cerr << "Invalid address/ Address not supported." << std::endl;
        return;
    }

    if (connect(sock, (struct sockaddr *)&server_addr, sizeof(server_addr)) < 0) {
        std::cerr << "Connection Failed : " << strerror(errno) << std::endl;
        return;
    }
    std::cout << "Connected to the server." << std::endl;

    std::thread receiveThread([this]() {
        while (!isDisconnecting) {
            if (!receiveMessage()) {
                break;
            }
            {
                std::lock_guard<std::mutex> lk(cv_m);
                received = true;
            }
            cv.notify_one();
        }
    });

    std::thread sendThread([this]() {
        std::string message;
        do {
            {
                std::unique_lock<std::mutex> lk(cv_m);
                cv.wait(lk, []{return received;});
            }
            std::cout << "Send message to server (type 'quit' to exit): ";
            std::getline(std::cin, message);

            if (message == "quit") {
                isDisconnecting = true;
                sendMessage(message); 
                break;
            }

            sendMessage(message);
            {
                std::lock_guard<std::mutex> lk(cv_m);
                received = false;
            }
        } while (true);
    });

   
    receiveThread.join();
    sendThread.join();

    if (sock != -1) {
        close(sock);
        sock = -1; 
        std::cout << "Client connection closed." << std::endl;
    }
}


void Client::sendMessage(const std::string& message) {
    std::string messageWithDelimiter = message + "\n";
    send(sock, messageWithDelimiter.c_str(), messageWithDelimiter.size(), 0);
}


bool Client::receiveMessage() {
    char buffer[1024] = {0};
    ssize_t bytesReceived = read(sock, buffer, 1024);
    if (bytesReceived <= 0) {
        if (bytesReceived < 0) {
            std::cerr << "Read failed: " << strerror(errno) << std::endl;
        } else {
            std::cout << "Server closed the connection." << std::endl;
        }
        return false;
    }
    std::string message(buffer);
    std::cout << message << std::endl;
    return true;
}


Client::~Client() {
    isDisconnecting = true; 
    if (sock != -1) {
        close(sock);
        std::cout << "Client connection closed." << std::endl;
    }
}
