#include "SessionHandler.hpp"
#include <iostream>


std::string SessionHandler::createSession(int clientSocket) {
    std::lock_guard<std::mutex> lock(sessionsMutex); 
    std::string sessionId = generateSessionId(clientSocket);
    auto now = std::chrono::system_clock::now();
    sessions[clientSocket] = {sessionId, now};
    return sessionId;
}

bool SessionHandler::endSession(int clientSocket) {
    std::lock_guard<std::mutex> lock(sessionsMutex); 
    auto it = sessions.find(clientSocket);
    if (it != sessions.end()) {
        sessions.erase(it);
        return true; 
    }
    return false; 
}

void SessionHandler::printSessions() const {
    std::lock_guard<std::mutex> lock(sessionsMutex); 
    std::cout << "Active Sessions:" << std::endl;
    for (const auto& pair : sessions) {
        auto time = std::chrono::system_clock::to_time_t(pair.second.createdAt);
        std::cout << "  Socket: " << pair.first << ", ID: " << pair.second.id << ", Created At: " << std::ctime(&time);
    }
}

std::string SessionHandler::generateSessionId(int clientSocket) const {
    return "Session_" + std::to_string(clientSocket);
}
