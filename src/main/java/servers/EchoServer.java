package servers;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by boyko on 12/30/16.
 */
@WebListener
public class EchoServer implements ServletContextListener {

    private static boolean running = false;

    public void runServer(final ServerSocket serverSocket) throws IOException {

        System.out.println("EchoServer.run: Echo server running.");
        final Socket clientSocket = serverSocket.accept();

        System.out.println("EchoServer.run: accepted connection.");
        Runnable serverSocketThread = () -> {


            try {
                final PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                //start a thread that would emulate sending messages to client
                new Thread(() -> {
                    while(true) {
                        try {
                            System.out.println("EchoServer.runServer: waiting for 10 seconds before sending a message to client.");
                            Thread.sleep(10000);
                            String messageToClient = "Emulated Message from server to client " +
                                    ThreadLocalRandom.current().nextInt(1, 10000000);
                            out.println(messageToClient);
                            System.out.println("EchoServer.run: sent Emulated message TO CLIENT: " + messageToClient);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                String messageFromClient = null;
                while(false == "CloseConnection".equalsIgnoreCase(messageFromClient)) {
                    try {

                        System.out.println("EchoServer.run: waiting for a message FROM CLIENT: ");
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(clientSocket.getInputStream()));

                        messageFromClient = reader.readLine();
                        System.out.println("EchoServer.run: received message FROM CLIENT: " + messageFromClient);

                        out.println(messageFromClient);
                        System.out.println("EchoServer.run: ECHOED message TO CLIENT: " + messageFromClient);


                    } catch (Exception e) {
                        e.printStackTrace();
                        //clientSocket.close();
                        messageFromClient = "CloseConnection";
                    }
                }

                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        new Thread(serverSocketThread).start();
        System.out.println("EchoServer.run. STARTED one socket thread.");
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
            Runnable socketServer = () -> {
                try {
                    final ServerSocket serverSocket = new ServerSocket(8183);
                    while(true) {
                        runServer(serverSocket);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };
        new Thread(socketServer).start();
        System.out.println("EchoServer.contextInitialized: started server emulator.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
