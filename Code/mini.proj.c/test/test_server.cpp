#include "Server.hpp"
#include <iostream>
#include <cassert>
#include <sys/socket.h> 
#include <netinet/in.h> 
#include <arpa/inet.h> 
#include <unistd.h> 
#include <thread> 
#include <chrono>

void testAcceptConnection() {
    std::cout << "Test: Accept Connection" << std::endl;

    Server server(12345);
    std::thread serverThread([&server](){ server.start(); });

    std::this_thread::sleep_for(std::chrono::seconds(1));

    int sock = socket(AF_INET, SOCK_STREAM, 0);
    assert(sock >= 0 && "Socket creation failed");

    sockaddr_in server_addr{};
    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(12345);
    server_addr.sin_addr.s_addr = inet_addr("127.0.0.1");

    int connResult = connect(sock, (struct sockaddr*)&server_addr, sizeof(server_addr));
    assert(connResult >= 0 && "Connection to server failed");

    close(sock);
    std::cout << "Test passed: Server accepts connection." << std::endl;

    serverThread.join(); 
}

int main() {
    testAcceptConnection();
    return 0;
}