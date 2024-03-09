#ifndef CLIENT_HPP
#define CLIENT_HPP

#include <string>
#include <atomic>
class Client {
public:
    Client(const std::string& host, int port);
    ~Client(); 
    void connectToServer();
    void sendMessage(const std::string& message);
    bool receiveMessage();
    bool isDataAvailable();
    

private:
    std::string host;
    int port;
    int sock; 
    std::atomic<bool> isDisconnecting;
};

#endif 
