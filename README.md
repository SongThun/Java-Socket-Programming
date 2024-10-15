# Socket Programming in Java: A Simple Chat Application

## Overview
**Computer Network Lab 1b**
This project is a simple chat application implemented in Java using socket programming. It allows multiple clients to connect to a server and exchange messages in real time. The server can handle multiple client connections simultaneously, making use of multithreading to ensure that each client can send and receive messages without blocking others.

## Key Takeaways

- **Java**: Programming language used for the application.
- **Socket Programming**: Implemented using Java's `java.net` package for creating server-client architecture.
- **Multithreading**: Utilized to handle multiple clients concurrently using Java's `Thread` and `Runnable` interfaces.

## Getting Started

### Prerequisites

- Java Development Kit (JDK) installed on your machine (version 8 or higher).

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/SongThun/Java-Socket-Programming.git
   ```

2. Navigate to the project directory:
   ```bash
   cd Java-Socket-Programming
   ```

3. Compile the Java files:
   ```bash
   javac Server.java Client.java
   ```

### Running the Application

1. Start the server:
   ```bash
   java Server [port_number]
   ```
   If no port number is specified, the default port `8080` will be used.

2. Start one or more clients (on different command prompts):
   ```bash
   java Client
   ```

3. Enter your chat name when prompted and start chatting!

### Exiting the Application

To exit the application, type `exit` in the client console. This will close the connection and notify other clients that you have left the chat.
