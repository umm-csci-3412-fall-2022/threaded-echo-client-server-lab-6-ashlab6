package echoserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer {
    // REPLACE WITH PORT PROVIDED BY THE INSTRUCTOR
    public static final int PORT_NUMBER = 6013; 
    public static void main(String[] args) throws IOException, InterruptedException {
        EchoServer server = new EchoServer();
        server.start();
    }

    private void start() throws IOException, InterruptedException {
        ServerSocket serverSocket = new ServerSocket(PORT_NUMBER);
        while (true) {
            Socket socket = serverSocket.accept();

            ServerThread runner = new ServerThread(socket);
            new Thread(runner).start();
        }
    }

    public class ServerThread implements Runnable {
        Socket socket;

        ServerThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            System.out.println("Starting Thread For" + socket.getInetAddress().toString());
            
            try {
                OutputStream out = socket.getOutputStream();
                InputStream in = socket.getInputStream();
                
                // Read data until no more is avaialble
                while (true) {
                    int data = in.read();

                    if (data == -1) {
                        break;
                    }

                    out.write(data);
                }

                // Flush to output just to be sure
                out.flush();

                // Close the output
                socket.shutdownOutput();

                System.out.println("Stopping Thread For" + socket.getInetAddress().toString());
            
                // Close the client socket since we're done.
                socket.close();
            }
            catch (IOException e) {
                System.out.println("Runner encountered an error:" + e.toString());
                System.out.println("Terminating Thread");
            }
        }
    }
}