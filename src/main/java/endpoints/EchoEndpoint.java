package endpoints;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by boyko on 12/30/16.
 */
@ServerEndpoint("/echo")
public class EchoEndpoint {

    private static final ConcurrentHashMap<String, Session>
        websocketClients = new ConcurrentHashMap<String, Session>();

    @OnOpen
    public void onOpen(Session session, EndpointConfig conf) {

        String websocketClientSessionId = session.getId();
        System.out.println("Session onOpen. session id: " + websocketClientSessionId);
        websocketClients.put(websocketClientSessionId, session);
        System.out.println("EchoEndpoint.onOpen. number of websocket sessions: " + websocketClients.size());
    }

    @OnMessage
    public void onMessage(Session session, String msg) {

        String websocketClientSessionId = session.getId();
        System.out.println("Session onMessage. session id: " + websocketClientSessionId);
        Session storedSession = websocketClients.get(websocketClientSessionId);

        try {
            storedSession.getBasicRemote().sendText("I heard you! You said '" + msg + "'. Anything else?");
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
        websocketClients.remove(session.getId());
        System.out.println("EchoEndpoint.onClose reasonPhrase: " + reason.getReasonPhrase());
        System.out.println("EchoEndpoint.onClose closeCode: " + reason.getCloseCode());

    }
}
