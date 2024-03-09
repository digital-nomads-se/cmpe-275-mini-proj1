#include "Server.hpp"
#include <iostream>

int main() {
    std::cout << "Starting server..." << std::endl;
    int port = 12345; 
    Server server(port); 
    server.start();
    return 0;
}

