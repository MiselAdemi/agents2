package session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;

import model.Agent;
import model.AgentCenter;
import model.AgentType;
import utils.Container;

@Stateless
@Path("ac")
@LocalBean
public class AgentCenterBean implements AgentCenterBeanRemote {

	@GET
	@Path("isMaster")
	@Produces(MediaType.TEXT_PLAIN)
	@Override
	public boolean isMaster() {
		return Container.isMaster();
	}
	
	@POST
	@Path("nodes")
	@Consumes(MediaType.APPLICATION_JSON)
	@Override
	public void registerHosts(AgentHosts hosts) {
		for(AgentCenter host : hosts.getHosts()) {
			if(!hostExists(host)) {
				Container.getInstance().addHost(host);
			}
		}
	}
	
	@POST
	@Path("node")
	@Consumes(MediaType.APPLICATION_JSON)
	@Override
	public void registerMe(AgentCenter agentCenter) {

		if(!hostExists(agentCenter)) {
			System.out.println("Adding new host...");
			Container.getInstance().addHost(agentCenter);
			ArrayList<AgentType> supportedAgents = getAllSupportedAgents(agentCenter.getAddress());
			informNonMasterNodes(agentCenter);
			informNonMasterAgentTypes(agentCenter, supportedAgents);
			informNewHostHosts(agentCenter, Container.getInstance().getHosts().keySet());
			informNewHostAgentTypes(agentCenter, Container.getInstance().getAgentTypes());
			informNewHostRunningAgents(agentCenter, Container.getInstance().getRunningAgents());
		}

	}
	
	private void informNewHostRunningAgents(AgentCenter agentCenter, ArrayList<Agent> runningAgents) {
		Client client = ClientBuilder.newClient();
		WebTarget resource = client.target("http://" + agentCenter.getAddress() + "/AgentsWeb/rest/ac/agents/running");
		Builder request = resource.request();
		RunningAgents ra = new RunningAgents();
		ra.setRunningAgents(runningAgents);
		Response response = request.post(Entity.json(ra));

		if(response.getStatusInfo().getFamily() == Family.SUCCESSFUL){
			System.out.println("Informing non master nodes about new agent types was successfull");
		}
		else{
			System.out.println("Error: " + response.getStatus());
		}
	}

	private void informNewHostHosts(AgentCenter agentCenter, Set<AgentCenter> hosts) {
		Client client = ClientBuilder.newClient();
		WebTarget resource = client.target("http://" + agentCenter.getAddress() + "/AgentsWeb/rest/ac/nodes");
		Builder request = resource.request();
		AgentHosts ah = new AgentHosts();
		ah.setHosts(hosts);
		Response response = request.post(Entity.json(ah));

		if(response.getStatusInfo().getFamily() == Family.SUCCESSFUL){
			System.out.println("Informing non master nodes about new agent types was successfull");
		}
		else{
			System.out.println("Error: " + response.getStatus());
		}
	}
	
	private void informNewHostAgentTypes(AgentCenter agentCenter, ArrayList<AgentType> agentTypes) {
		Client client = ClientBuilder.newClient();
		WebTarget resource = client.target("http://" + agentCenter.getAddress() + "/AgentsWeb/rest/ac/agents/classes");
		Builder request = resource.request();
		Response response = request.post(Entity.json(agentTypes));		

		if(response.getStatusInfo().getFamily() == Family.SUCCESSFUL){
			System.out.println("Informing new node about new agent types was successfull");
		}
		else{
			System.out.println("Error: " + response.getStatus());
		}
	}
	
	private void informNonMasterAgentTypes(AgentCenter agentCenter, ArrayList<AgentType> supportedAgents) {
		Set<AgentCenter> hosts = Container.getInstance().getHosts().keySet();
		String masterIP = Container.getMasterIP();
		String newHostIP = agentCenter.getAddress();
		for(AgentCenter host : hosts){
			if(!host.getAddress().equals(masterIP) &&		//ukoliko nije master
					!host.getAddress().equals(newHostIP)){	//ukoliko nije novi cvor
				//obavesti ostale o novom tipovima agenata
				Client client = ClientBuilder.newClient();
				WebTarget resource = client.target("http://" + host.getAddress() + "/AgentsWeb/rest/ac/agents/classes");
				Builder request = resource.request();
				ArrayList<AgentType> at = new ArrayList<AgentType>();
				at.addAll(supportedAgents);
				Response response = request.post(Entity.json(at));

				if(response.getStatusInfo().getFamily() == Family.SUCCESSFUL){
					System.out.println("Informing non master nodes about new agent types was successfull");
				}
				else{
					System.out.println("Error: " + response.getStatus());
				}
			}
		}
	}
	
	private void informNonMasterNodes(AgentCenter agentCenter) {
		Set<AgentCenter> hosts = Container.getInstance().getHosts().keySet();
		String masterIP = Container.getMasterIP();
		String newHostIP = agentCenter.getAddress();
		for(AgentCenter host : hosts){
			if(!host.getAddress().equals(masterIP) &&		//ukoliko nije master
					!host.getAddress().equals(newHostIP)){	//ukoliko nije novi cvor
				//obavesti ostale o novom cvoru
				Client client = ClientBuilder.newClient();
				WebTarget resource = client.target("http://" + host.getAddress() + "/AgentsWeb/rest/ac/node");
				Builder request = resource.request();
				Response response = request.post(Entity.json(agentCenter));

				if(response.getStatusInfo().getFamily() == Family.SUCCESSFUL){
					System.out.println("Informing non master nodes was successfull");
				}
				else{
					System.out.println("Error: " + response.getStatus());
				}
			}
		}
	}
	
	private boolean hostExists(AgentCenter agentCenter) {
		boolean retVal = false;
		HashMap<AgentCenter, ArrayList<Agent>> hosts = Container.getInstance().getHosts();
		
		for(AgentCenter acKey : hosts.keySet()) {
			if(acKey.getAddress().equals(agentCenter.getAddress())) {
				retVal = true;
				break;
			}
		}
		
		return retVal;
	}

	@SuppressWarnings("unchecked")
	@GET
	@Path("agents/classes")
	@Override
	public ArrayList<AgentType> getAllSupportedAgents(String ip) {
		ArrayList<AgentType> agentTypes = new ArrayList<AgentType>();
		
		Client client = ClientBuilder.newClient();
		WebTarget resource = client.target("http://" + ip + ":8080/AgentsWeb/rest/agents/classes");
		Builder request = resource.request();
		Response response = request.get();

		if(response.getStatusInfo().getFamily() == Family.SUCCESSFUL){
			ArrayList<AgentType> at = response.readEntity(ArrayList.class);
			System.out.println(at.toString());
		}
		else{
			System.out.println("Error: " + response.getStatus());
		}
		
		return agentTypes;
	}

	@POST
	@Path("agents/classes")
	@Override
	public void forwardNewAgentTypes(ArrayList<AgentType> agentTypes) {
		System.out.println("I have received new agent types");
		System.out.println("AT: " + agentTypes);
		ArrayList<AgentType> myAgentTypes = Container.getInstance().getAgentTypes();
		ArrayList<AgentType> newAgentTypes = agentTypes;
		boolean typeExists = false;
		
		for(AgentType newAt: newAgentTypes){
			typeExists = false;
			for(AgentType myAt : myAgentTypes){
				if(newAt.getModule().equals(myAt.getModule()) &&
						newAt.getName().equals(myAt.getName())){
					//vec postoji
					typeExists = true;
				}
			}
			if(!typeExists){
				Container.getInstance().addAgentType(newAt);
			}
		}
		System.out.println("My list of agent types looks like this: " + Container.getInstance().getAgentTypes().toString());	 	
	}

	@POST
	@Path("agents/running")
	@Override
	public void forwardRunningAgents(RunningAgents ra) {
		boolean runningAgentExists = false;
		for(Agent newA : ra.getRunningAgents()){
			runningAgentExists = false;
			for(Agent myA : Container.getInstance().getRunningAgents()){
				if(agentsEqual(myA, newA)){
					runningAgentExists = true;
				}
			}
			
			if(!runningAgentExists) {
				Container.getInstance().addRunningAgent(newA.getId().getHost(), newA);
				System.out.println(Container.getInstance().getRunningAgents());
			}
		}
	}

	@Override
	public void deleteNode(String alias) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void checkIfAlive() {
		// TODO Auto-generated method stub
		
	}

	private boolean agentsEqual(Agent myA, Agent newA) {
		return myA.getId().getName().equals(newA.getId().getName()) &&
				myA.getId().getHost().getAddress().equals(newA.getId().getHost().getAddress()) &&
				myA.getId().getHost().getAlias().equals(newA.getId().getHost().getAlias()) &&
				myA.getId().getType().getName().equals(newA.getId().getType().getName()) &&
				myA.getId().getType().getModule().equals(newA.getId().getType().getModule());
	}

}
