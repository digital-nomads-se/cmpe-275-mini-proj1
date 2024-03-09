#ifndef SERVER_HPP
#define SERVER_HPP

#include <map>
#include <string>
#include <vector>
#include <thread>
#include <atomic>

class Server {
public:
    Server(int port);
    ~Server();
    void start();

private:
    int port;
    std::vector<std::thread> threads;
    std::atomic<int> clientCount;
    void handleClient(int clientSocket);
};

#endif 
