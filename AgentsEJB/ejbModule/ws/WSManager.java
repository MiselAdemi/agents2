package ws;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value="/websocket")
public class WSManager {

	public WSManager() {
		
	}
	
	@OnOpen
	public void onOpen(Session session) {
		
	}
	
	@OnMessage
	public void onMessage(Session session, String message) {
		
	}
	
	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		
	}
	
	@OnError
	public void onError(Session session, Throwable throwable) {
		
	}
	
}
