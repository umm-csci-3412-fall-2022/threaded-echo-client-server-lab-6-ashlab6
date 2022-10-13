package echoserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class EchoClient {
	public static final int PORT_NUMBER = 6013;

	public static void main(String[] args) throws Throwable {
		EchoClient client = new EchoClient();
		client.start();
	}

	private void start() throws Throwable {
		Socket socket = new Socket("localhost", PORT_NUMBER);
		InputStream socketInputStream = socket.getInputStream();
		OutputStream socketOutputStream = socket.getOutputStream();

        Thread sending = new Thread(new SendingThread(socketOutputStream));
        Thread receiving = new Thread(new ReceivingThread(socketInputStream));

		sending.start();
		receiving.start();

		sending.join();
		socket.shutdownOutput();

		receiving.join();
		socket.shutdownInput();

		socket.close();
	}

	public class SendingThread implements Runnable {
        OutputStream output;

        SendingThread(OutputStream output) {
            this.output = output;
        }

        @Override
        public void run() {
            try {
                int data;
				while ((data = System.in.read()) != -1) {
					output.write(data);
				}

				output.flush();
            }
            catch (IOException e) {
                System.out.println("Sending Thread encountered an error:" + e.toString());
                System.out.println("Terminating Thread");
            }
        }
    }

	public class ReceivingThread implements Runnable {
        InputStream input;

        ReceivingThread(InputStream input) {
            this.input = input;
        }

        @Override
        public void run() {
            try {
                int data;
				while ((data = input.read()) != -1) {
					System.out.write(data);
				}

				System.out.flush();
            }
            catch (IOException e) {
                System.out.println("Receiving Thread encountered an error:" + e.toString());
                System.out.println("Terminating Thread");
            }
        }
    }
}