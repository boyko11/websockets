package endpoints;

import model.WebsocketTCPSocketPair;
import org.apache.commons.io.IOUtils;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by boyko on 12/30/16.
 */
@ServerEndpoint("/echo")
public class EchoEndpoint {

    private static final ConcurrentHashMap<String, WebsocketTCPSocketPair>
        websocketTCPSocketsPairs = new ConcurrentHashMap<>();

    private static final String serverSocketURL = "localhost";
    private static final int serverSocketPort = 8183;

    @OnOpen
    public void onOpen(Session session, EndpointConfig conf) throws IOException {

        String websocketClientSessionId = session.getId();
        System.out.println("Session onOpen. session id: " + websocketClientSessionId);

        //connect to server emulator
        Socket socket = new Socket(serverSocketURL, serverSocketPort);
        WebsocketTCPSocketPair websocketTCPSocketPair = new WebsocketTCPSocketPair();
        websocketTCPSocketPair.websocketSession = session;
        websocketTCPSocketPair.tcpSocket = socket;
        websocketTCPSocketsPairs.put(websocketClientSessionId, websocketTCPSocketPair);

        listenForMessagesFromServer(websocketTCPSocketPair);

        System.out.println("EchoEndpoint.onOpen. number of websocket sessions: " + websocketTCPSocketsPairs.size());
    }

    private void listenForMessagesFromServer(final WebsocketTCPSocketPair websocketTCPSocketPair) {

            Runnable serverMessageListener = () -> {
                System.out.println("EchoEndpoint.listenForMessagesFromServer websocket " +
                        websocketTCPSocketPair.websocketSession.getId() +
                        "  opened a server socket and is listening for messages from it.");
                String messageFromServer = "";
                while(false == "ConnectionClosed".equalsIgnoreCase(messageFromServer)) {


                    try {

                        System.out.println("EchoEndpoint.listenForMessagesFromServer: waiting for a message FROM SERVER: ");
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(websocketTCPSocketPair.tcpSocket.getInputStream()));

                        messageFromServer = reader.readLine();

                        System.out.println("EchoEndpoint.listenForMessagesFromServer: received message FROM SERVER: " +
                                messageFromServer);

                        websocketTCPSocketPair.websocketSession.getBasicRemote()
                                .sendText(messageFromServer);

                    } catch (Exception e) {
                        e.printStackTrace();
                        messageFromServer = "ConnectionClosed";
                    }

                }
            };
            new Thread(serverMessageListener).start();

    }

    @OnMessage
    public void onMessage(Session session, String messageFromClient) {

        String websocketClientSessionId = session.getId();
        System.out.println("Session onMessage. session id: " + websocketClientSessionId);
        Socket serverSocket = websocketTCPSocketsPairs.get(websocketClientSessionId).tcpSocket;

        try {
            PrintWriter out =
                    new PrintWriter(serverSocket.getOutputStream(), true);
            out.println(messageFromClient);
            //out.close();
            System.out.println("EchoEndpoint.onMessage. sent message to tcp sockets server: " + messageFromClient + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnError
    public void onError(Session session,
                      Throwable error) {

        System.out.println("Session onError. session id: " + session.getId());
        error.printStackTrace();
    }


    @OnClose
    public void onClose(Session session,
                      CloseReason reason) {

        System.out.println("Session onClose. session id: " + session.getId());
        websocketTCPSocketsPairs.remove(session.getId());
        System.out.println("EchoEndpoint.onClose reasonPhrase: " + reason.getReasonPhrase());
        System.out.println("EchoEndpoint.onClose closeCode: " + reason.getCloseCode());

    }
}
