#ifndef MESSAGE_HPP
#define MESSAGE_HPP

#include <string>

class Message {
public:
    static std::string encode(const std::string& message);
    static std::string decode(const std::string& message);
};

#endif 
