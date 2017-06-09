package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Singleton;
import javax.websocket.Session;

import model.Agent;
import model.AgentCenter;
import model.AgentType;

@Singleton
public class Container {

	private static Container instance = null;
	private ArrayList<Agent> runningAgents = new ArrayList<Agent>();
	private HashMap<AgentCenter, ArrayList<Agent>> hosts = new HashMap<>();
	private ArrayList<AgentType> agentTypes = new ArrayList<AgentType>();
	private HashMap<AgentCenter, ArrayList<Session>> sessions = new HashMap<>();
	private ArrayList<String> loggerMessages = new ArrayList<String>();
	
	private Container(){

	}
	
	public static synchronized Container getInstance(){
		if(instance == null)
			instance = new Container();
		return instance;
	}
	
	public ArrayList<Agent> getRunningAgents(){
		return runningAgents;
	}
	
	public void addHost(AgentCenter ac){		
		if(hosts.get(ac) == null){
			hosts.put(ac, new ArrayList<Agent>());
		}
		else{
			hosts.get(ac).addAll(new ArrayList<Agent>());
		}
	}
	
	public void addRunningAgent(AgentCenter ac, Agent agent){
		//adding to arraylist of runningagents
		//check if agent already exists
		boolean ae = false;
		for(Agent a : runningAgents){
			if(agentsEqual(a, agent)){
				ae = true;
				break;
			}
		}
		if(!ae){
			runningAgents.add(agent);
		}
		
		if(hosts.get(ac)==null){
			ArrayList<Agent> ra = new ArrayList<>();
			ra.add(agent);
			hosts.put(ac, ra);
		}
		else{
			//check if agent already exists
			ArrayList<Agent> ra = hosts.get(ac);
			boolean agentExists = false;
			for(Agent a : ra){
				if(agentsEqual(a, agent)){
					agentExists = true;
					break;
				}
			}
			
			if(!agentExists) {
				hosts.get(ac).add(agent);
			}
		}
	}
	
	public void removeRunningAgent(AgentCenter ac, Agent agent){
		runningAgents.remove(agent);
		hosts.get(ac).remove(agent);
	}
	
	public HashMap<AgentCenter, ArrayList<Agent>> getHosts(){
		return this.hosts;
	}
	
	public ArrayList<AgentType> getAgentTypes() {
		return agentTypes;
	}
	
	public void setAgentTypes(ArrayList<AgentType> agentTypes) {
		this.agentTypes = agentTypes;
	}
	
	public void addAgentType(AgentType agentType) {
		if(!agentTypeExists(agentType))
			this.agentTypes.add(agentType);
	}
	
	private boolean agentTypeExists(AgentType agentType) {
		boolean retVal = false;
		ArrayList<AgentType> agentTypes = this.agentTypes;
		
		for(AgentType at : agentTypes) {
			if(at.getModule().equals(agentType.getModule()) && at.getName().equals(agentType.getName())) {
				retVal = true;
				break;
			}
		}
		
		return retVal;
	}
	
	public static String getLocalIP(){
		String local = null;
		
		if(System.getProperty("local") != null)
			local = System.getProperty("local");
		else {
			int port = 8080 + Integer.parseInt(System.getProperty("jboss.socket.binding.port-offset"));
			local = "127.0.0.1:" + port;
		}
		
		System.out.println("Local: " + local);
		
		return local;
	}
	
	public static String getMasterIP(){
		String master = null;
		
		if(System.getProperty("master") != null)
			master = System.getProperty("master");
		else
			master = System.getProperty("local");
		
		System.out.println("Master: " + master);
		return master;
	}
	
	public static boolean isMaster() {
		return getLocalIP().equals(getMasterIP()) ? true : false;
	}
	
	private boolean agentsEqual(Agent myA, Agent newA) {
		return myA.getId().getName().equals(newA.getId().getName()) &&
				myA.getId().getHost().getAddress().equals(newA.getId().getHost().getAddress()) &&
				myA.getId().getHost().getAlias().equals(newA.getId().getHost().getAlias()) &&
				myA.getId().getType().getName().equals(newA.getId().getType().getName()) &&
				myA.getId().getType().getModule().equals(newA.getId().getType().getModule());
	}

	public HashMap<AgentCenter, ArrayList<Session>> getSessions() {
		return sessions;
	}

	public void setSessions(HashMap<AgentCenter, ArrayList<Session>> sessions) {
		this.sessions = sessions;
	}

	public AgentCenter findAgentCenterByIP(String IP){
		AgentCenter retVal = null;
		for(AgentCenter ac : sessions.keySet()){
			if(ac.getAddress().equals(IP)){
				retVal = ac;
				break;
			}
		}
		return retVal;
	}

	public Session findSessionByID(String sessionID){
		Session retVal = null;
		for(Map.Entry<AgentCenter, ArrayList<Session>> session: sessions.entrySet()){
			for(Session s: session.getValue()){
				if(s.getId().equals(sessionID)){
					retVal = s;
					break;
				}
			}
		}
		return retVal;
	}
	
	public ArrayList<String> getLoggerMessages() {
		return loggerMessages;
	}

	public void setLoggerMessages(ArrayList<String> loggerMessages) {
		this.loggerMessages = loggerMessages;
	}

	public void log(String message){
		this.loggerMessages.add(message);
	}
	
}
