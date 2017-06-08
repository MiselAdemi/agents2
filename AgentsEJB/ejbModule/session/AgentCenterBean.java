package session;

import java.util.ArrayList;
import java.util.HashMap;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
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
			getAllSupportedAgents(agentCenter.getAddress());
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
	public void getAllSupportedAgents(String ip) {
		Client client = ClientBuilder.newClient();
		WebTarget resource = client.target("http://" + ip + "/AgentsWeb/rest/agents/classes");
		Builder request = resource.request();
		Response response = request.get();

		if(response.getStatusInfo().getFamily() == Family.SUCCESSFUL){
			System.out.println((ArrayList<AgentType>)response.getEntity());
		}
		else{
			System.out.println("Error: " + response.getStatus());
		}
	}

	@POST
	@Path("agents/classes")
	@Override
	public void forwardNewAgentTypes() {
		// TODO Auto-generated method stub
		
	}

	@POST
	@Path("agents/running")
	@Override
	public void forwardRunningAgents() {
		// TODO Auto-generated method stub
		
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
