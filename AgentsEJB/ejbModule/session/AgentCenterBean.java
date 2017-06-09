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
		System.out.println(agentCenter);
		
		if(hostExists(agentCenter)) {
			System.out.println("Host already exists");
		}else {
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
		WebTarget resource = client.target("http://" + agentCenter.getAddress() + ":8080/AgentsWeb/rest/ac/agents/running");
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
		WebTarget resource = client.target("http://" + agentCenter.getAddress() + ":8080/AgentsWeb/rest/ac/nodes");
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
	
	private void informNewHostAgentTypes(AgentCenter agentCenter, AgentTypes agentTypes) {
		Client client = ClientBuilder.newClient();
		WebTarget resource = client.target("http://" + agentCenter.getAddress() + ":8080/AgentsWeb/rest/ac/agents/classes");
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
				WebTarget resource = client.target("http://" + host.getAddress() + ":8080/AgentsWeb/rest/ac/agents/classes");
				Builder request = resource.request();
				AgentTypes at = new AgentTypes();
				at.setAgentTypes(supportedAgents);
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
				WebTarget resource = client.target("http://" + host.getAddress() + ":8080/AgentsWeb/rest/ac/node");
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
		
		for(AgentCenter ac_key : hosts.keySet()) {
			if(ac_key.getAddress().equals(agentCenter.getAddress())) {
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
			AgentTypes at = response.readEntity(AgentTypes.class);
			System.out.println(at.getAgentTypes().toString());
		}
		else{
			System.out.println("Error: " + response.getStatus());
		}
		
		return agentTypes;
	}

	@POST
	@Path("agents/classes")
	@Override
	public void forwardNewAgentTypes(AgentTypes agentTypes) {
		System.out.println("I have received new agent types");
		System.out.println("AT: " + agentTypes);
		ArrayList<AgentType> myAgentTypes = Container.getInstance().getAgentTypes().getAgentTypes();
		ArrayList<AgentType> newAgentTypes = agentTypes.getAgentTypes();
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
		System.out.println("My list of agent types looks like this: " + Container.getInstance().getAgentTypes().getAgentTypes().toString());	 	
	}

	@POST
	@Path("agents/running")
	@Override
	public void forwardRunningAgents(RunningAgents ra) {
		System.out.println(ra.getRunningAgents().toString());
		boolean runningAgentExists = false;
		for(Agent newA : ra.getRunningAgents()){
			runningAgentExists = false;
			for(Agent myA : Container.getInstance().getRunningAgents()){
				if(myA.getId().equals(newA.getId()) && 
						myA.getAgentCenter().getAddress().equals(newA.getAgentCenter().getAddress()) &&
						myA.getAgentCenter().getAlias().equals(newA.getAgentCenter().getAlias())){
					runningAgentExists = true;
				}
			}
			if(!runningAgentExists)
				Container.getInstance().addRunningAgent(newA.getAgentCenter(), newA);
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

}
