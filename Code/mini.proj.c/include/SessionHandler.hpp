#ifndef SESSION_HANDLER_HPP
#define SESSION_HANDLER_HPP

#include <string>
#include <map>
#include <chrono>
#include <mutex>

struct Session {
    std::string id;
    std::chrono::system_clock::time_point createdAt;
};

class SessionHandler {
public:
    SessionHandler() = default;
    ~SessionHandler() = default;
    SessionHandler(const SessionHandler&) = delete;
    SessionHandler& operator=(const SessionHandler&) = delete;

    std::string createSession(int clientSocket);
    bool endSession(int clientSocket);
    void printSessions() const;

private:
    std::map<int, Session> sessions; 
    mutable std::mutex sessionsMutex; 

    std::string generateSessionId(int clientSocket) const; 
};

#endif

