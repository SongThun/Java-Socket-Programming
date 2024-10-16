import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Server class
public class Server {

    private static int PORT;
    // List to store all active client sockets
    private static final List<ClientHandler> clientHandlers = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {
        // Validate port config
        PORT = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        if (PORT < 1 || PORT > 65535) {
            System.out.println("Invalid port number. Please use a port between 1 and 65535.");
            return;
        }
        new Server().startServer();
    }

    public void startServer() {
        ExecutorService pool = Executors.newFixedThreadPool(12); 
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.printf("Server started, listening on port %d ...\n", PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept(); 
                // Create new thread to handle client connection
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                pool.execute(clientHandler); 
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Broadcast a message to all current clients
    // sender == null if Server is sending
    private void broadcastMessage(String message, ClientHandler sender, String senderName) {
        synchronized (clientHandlers) {
            for (ClientHandler clientHandler : clientHandlers) {
                if (sender == null || clientHandler != sender) {
                    if (sender != null) 
                        message = senderName + ": " + message;
                    clientHandler.sendMessage(message); // Send message to other clients
                }
            }
        }
    }

    // Grandchild Thread: Handle each connection
    class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in; 

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            System.out.println("Accepted connection from " + clientSocket);
            try {
                InputStream inputStream = clientSocket.getInputStream();
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(inputStream));
                
                sendMessage(String.format("Server#%d - Enter your chat name: ", PORT));
                
                // Get user chat name
                String clientName;
                clientName = in.readLine();

                // Add client to list only when chat name is entered.
                clientHandlers.add(this); 

                String enterMessage = String.format("** %s entered the chat. **", clientName);
                broadcastMessage(enterMessage, null, "");

                // Create new thread to handle InputStream
                Thread grandGrandChildThread = new Thread(new InputHandler(this, clientName));
                grandGrandChildThread.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Get buffered reader object
        public BufferedReader getBufferedReader() {
            return this.in;
        }

        // Send message to this client
        public void sendMessage(String message) {
            out.println(message);
        }

        // Close connection and release resource of this client
        public void closeConnection() {
            try {
                clientHandlers.remove(this);
                if (in != null) 
                    in.close();
                if (out != null) 
                    out.close();
                if (clientSocket != null && !clientSocket.isClosed()) 
                    clientSocket.close();
                System.out.println("Closed connection to " + clientSocket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        
    }

    // GrandGrandChild Thread: Handle input stream from client and broadcast messages
    class InputHandler implements Runnable {
        private ClientHandler clientHandler;
        private String clientName;

        public InputHandler(ClientHandler clientHandler, String clientName) {
            this.clientHandler = clientHandler;
            this.clientName = clientName;
            
        }

        @Override
        public void run() {
            try (BufferedReader reader = clientHandler.getBufferedReader()) {
                String input;
                
                while ((input = reader.readLine()) != null) {
                    // System.out.println("Received: " + input);
                    broadcastMessage(input, clientHandler, clientName); // Broadcast message to all clients, except the sender
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // Disconnect client
                String exitMessage = String.format("** %s left   the chat. **", clientName);
                broadcastMessage(exitMessage, null, "");
                clientHandler.closeConnection();
            }
        }
    }
}
