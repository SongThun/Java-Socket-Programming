import java.io.*;
import java.net.*;

// Client class
public class Client {
    private static int ServerPORT;
    // Main Window Thread: Create new connection to the server
    public static void main(String[] args) {
        if (args.length > 0) {
            ServerPORT = Integer.parseInt(args[0]);
        }
        else {
            ServerPORT = 8080;
        }
        new Client().startClient();
    }

    // Chat Window Thread: Connect to server and handle socket input
    public void startClient() {
        try (Socket socket = new Socket("localhost", ServerPORT)) {
            System.out.println("Connected to server...");
            System.out.println("Type ':exit' to quit the application.\n");

            // Create new thread to handle input from server
            Thread socketInputHandler = new Thread(new InputHandler(socket.getInputStream()));
            socketInputHandler.start(); 

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            
            String userInput;
            while ((userInput = reader.readLine()) != null) {
                if (":exit".equalsIgnoreCase(userInput)) 
                    break;
                writer.println(userInput); // Send user input to server
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Grandchild Thread: Handle input from server socket
    class InputHandler implements Runnable {
        private InputStream inputStream;

        public InputHandler(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String input;
                while ((input = reader.readLine()) != null) {
                    System.out.println(input); 
                }
            } catch (SocketException e) {
                System.err.println(e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
