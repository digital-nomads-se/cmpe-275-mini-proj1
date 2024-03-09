#include "Client.hpp"
#include <iostream>
#include <cassert>
#include <cstring> 

void testSendMessage() {
    std::cout << "Test: Send Message" << std::endl;

    Client client("127.0.0.1", 12345);
    client.connectToServer();

    std::string testMessage = "Hello, Server!";
    client.sendMessage(testMessage);

    bool receivedMessage = client.receiveMessage();
     if (!receivedMessage) {
        std::cerr << "Error: Message was not acknowledged correctly" << std::endl;
        exit(1);
    }
    std::cout << "Test passed: Message sent and acknowledged." << std::endl;
}

int main() {
    testSendMessage();
    return 0;
}
