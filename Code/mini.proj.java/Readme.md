# Java Socket Client-Server Project

## Description
This project is a simple implementation of a client-server model using Java's socket programming. The server accepts connections from clients and can handle multiple clients simultaneously. The client sends a message to the server and the server echoes the message back to the client.

## Getting Started

### Dependencies
* Java Development Kit (JDK) 21 or above
* Apache Maven

### Installing
* Clone the repository
* Navigate to the project directory

### Building the project
* Build the project using Maven: `mvn clean install`

### Executing program
* Run the server program: `java -cp target/mini.proj-1.0-SNAPSHOT.jar first.mini.proj.ServerApp`
* Run the client program in a different terminal or machine: `java -cp target/mini.proj-1.0-SNAPSHOT.jar first.mini.proj.ClientApp`

### Running Tests
* Run the test file: `mvn test`