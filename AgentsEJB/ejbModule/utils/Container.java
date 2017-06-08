package utils;

import java.util.ArrayList;
import java.util.HashMap;

import javax.ejb.Singleton;

import model.Agent;
import model.AgentCenter;

@Singleton
public class Container {

	private static Container instance = null;
	private ArrayList<Agent> runningAgents = new ArrayList<Agent>();
	private HashMap<AgentCenter, ArrayList<Agent>> hosts = new HashMap<>();
	private Container(){

	}
	
	public static Container getInstance(){
		if(instance == null)
			instance = new Container();
		return instance;
	}
	
	public ArrayList<Agent> getRunningAgents(){
		return runningAgents;
	}
	
	public void addRunningAgent(AgentCenter ac, Agent agent){
		runningAgents.add(agent);
		if(hosts.get(ac)==null){
			ArrayList<Agent> ra = new ArrayList<>();
			ra.add(agent);
			hosts.put(ac, ra);
		}
		else{
			hosts.get(ac).add(agent);
		}
	}
	
	public void removeRunningAgent(AgentCenter ac, Agent agent){
		runningAgents.remove(agent);
		hosts.get(ac).remove(agent);
	}
	
	public HashMap<AgentCenter, ArrayList<Agent>> getHosts(){
		return hosts;
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
	
}
