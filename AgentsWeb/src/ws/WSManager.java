package ws;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

import model.ACLMessage;
import model.AID;
import model.Agent;
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
	public void onMessage(Session session, String msg) {
		try{
			if(session.isOpen()){
				JSONObject obj = new JSONObject(msg);
				String message = obj.getString("type");
				
				switch(message){

				case("RUNNING_AGENTS"):
					RunningAgents ra = new RunningAgents();
					ra.setRunningAgents(Container.getInstance().getRunningAgents());
					session.getBasicRemote().sendObject(ra);
					break;
				case("AGENT_TYPES"):
					new AgentType();

					JSONArray ja = new JSONArray(Container.getInstance().getAgentTypes());
					String jaString = ja.toString();
					String msg1 = "{\"agentTypes\":" + jaString + "}";
					session.getBasicRemote().sendObject(msg1);	
					break;
				case("PERFORMATIVES"):
					ArrayList<String> retVal = new ArrayList<>();
					for(Performative performative : Performative.values()){
						retVal.add(performative.toString());
					}
					JSONArray json = new JSONArray(retVal);
					String jsonText = json.toString();
					String m = "{\"performatives\":" + jsonText + "}";
					session.getBasicRemote().sendObject(m);
					break;
				case("SEND_MESSAGE"):
					ObjectMapper mapper = new ObjectMapper();
					JSONObject data = obj.getJSONObject("data");
					ACLMessage aclMessage = mapper.readValue(data.toString(), ACLMessage.class);
					sendMessage(aclMessage);
					break;
				case("ADD_AGENT"):
					JSONObject aData = obj.getJSONObject("data");
					System.out.println(aData.getString("1"));
					runAgent("PingPong$" + aData.getString("1"), aData.getString("2"));
				}

			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void sendMessage(ACLMessage aclMessage) {
		Context context;
		try {
			context = new InitialContext();
			ConnectionFactory factory = (ConnectionFactory)context.lookup("java:/ConnectionFactory");
			final Queue target = (Queue) context.lookup("java:jboss/exported/jms/queue/mojQueue");
			context.close();
			Connection con = factory.createConnection();
			try{
				javax.jms.Session session = con.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
				MessageProducer producer = session.createProducer(target);
				producer.send(session.createObjectMessage(aclMessage));
			}
			finally{
				con.close();
			}
			
		} catch (NamingException | JMSException e) {
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

	private void runAgent(String agentType, String agentName) {
		String host = AID.HOST_NAME;		
		AgentCenter ac = null;
		ac = new AgentCenter(host, Container.getLocalIP());
		AgentType at = new AgentType(agentName, "PingPong");
		AID aid = new AID(agentName, ac, at);
		String className = agentType.split("\\$")[1];
		try {
			Class<?> cla55 = Class.forName(className);
			Constructor<?> constructor = cla55.getConstructor(AID.class);
			Object object = constructor.newInstance(new Object[]{aid});
			Container.getInstance().addRunningAgent(ac, (Agent)object);
			
			for(AgentCenter agentCenter : Container.getInstance().getHosts().keySet()){
				if(agentCenter!=null && !agentCenter.getAddress().equals(Container.getLocalIP())){
					Client client = ClientBuilder.newClient();
					WebTarget resource = client.target("http://" + agentCenter.getAddress() + ":8080/AgentsWeb/rest/ac/agents/running");
					Builder request = resource.request();
					RunningAgents ra = new RunningAgents();
					ra.setRunningAgents(Container.getInstance().getRunningAgents());
					Response response = request.post(Entity.json(ra));
					
					if(response.getStatusInfo().getFamily() == Family.SUCCESSFUL){
						System.out.println("Forwarding new agent was successfull");
					}
					else{
						System.out.println("Error: " + response.getStatus());
					}
				}
			}
		} catch (SecurityException | ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
}
