#include "Client.hpp"
#include <iostream>
#include <string>
int main() {
    std::string host = "127.0.0.1";
    int port = 12345;

    Client client(host, port);
    client.connectToServer();

    return 0;
}
