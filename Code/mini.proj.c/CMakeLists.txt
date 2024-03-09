cmake_minimum_required(VERSION 3.10)
project(SocketAppCpp)

# Set C++17 as the standard
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED True)

# Include directories
include_directories(include)

find_package(Threads REQUIRED)

# Server sources
set(SERVER_SOURCES
    src/Server.cpp
    src/SessionHandler.cpp
    src/main_server.cpp
)

# Client sources
set(CLIENT_SOURCES
    src/Client.cpp
    src/main_client.cpp
)

# Define the server executable
add_executable(ServerApp ${SERVER_SOURCES})

# Define the client executable
add_executable(ClientApp ${CLIENT_SOURCES})

# Link pthread if you're using std::thread
target_link_libraries(ServerApp Threads::Threads)
target_link_libraries(ClientApp Threads::Threads)

# Test Server sources
set(TEST_SERVER_SOURCES
    
    test/test_server.cpp
    src/Server.cpp
)

# Test Client sources
set(TEST_CLIENT_SOURCES
    
    test/test_client.cpp
    src/Client.cpp
)

# Define the test server executable
add_executable(TestServer ${TEST_SERVER_SOURCES})

# Define the test client executable
add_executable(TestClient ${TEST_CLIENT_SOURCES})

# Link pthread if you're using std::thread
target_link_libraries(TestServer Threads::Threads)
target_link_libraries(TestClient Threads::Threads)
