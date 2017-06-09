package ws;

import java.util.ArrayList;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.json.JSONArray;

import model.AgentCenter;
import model.AgentType;
import model.Performative;
import session.RunningAgents;
import utils.Container;

@ServerEndpoint(value="/websocket", encoders={RunningAgentsEncoder.class, AgentTypesEncoder.class})
public class WSManager {

	public WSManager() {

	}

	@OnOpen
	public void onOpen(Session session) {
		AgentCenter ac = Container.getInstance().findAgentCenterByIP(Container.getLocalIP());
		if(ac!=null){
			if(!Container.getInstance().getSessions().get(ac).contains(session))
				Container.getInstance().getSessions().get(ac).add(session);
		}
		else{
			AgentCenter acc = new AgentCenter("local_" + Container.getLocalIP(), Container.getLocalIP());
			ArrayList<Session> sessions = new ArrayList<>();
			sessions.add(session);
			Container.getInstance().getSessions().put(acc, sessions);
		}
	}

	@OnMessage
	public void onMessage(Session session, String message) {
		System.out.println("Message from " + session.getId() + ":" + message);
		try{
			if(session.isOpen()){
				switch(message){

				case("RUNNING_AGENTS"):
					RunningAgents ra = new RunningAgents();
					ra.setRunningAgents(Container.getInstance().getRunningAgents());
					session.getBasicRemote().sendObject(ra);
				case("AGENT_TYPES"):
					new AgentType();
					System.out.println(Container.getInstance().getAgentTypes());
					session.getBasicRemote().sendObject(Container.getInstance().getAgentTypes());				
				case("PERFORMATIVES"):
					ArrayList<String> retVal = new ArrayList<>();
					for(Performative performative : Performative.values()){
						retVal.add(performative.toString());
					}
					JSONArray json = new JSONArray(retVal);
					String jsonText = json.toString();
					String msg = "{\"performatives\":" + jsonText + "}";
					System.out.println(msg);
					session.getBasicRemote().sendObject(msg);
				}


			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		System.out.println("Closing session: " + session.getId());
	}

	@OnError
	public void onError(Session session, Throwable throwable) {

	}

}
