package session;

import java.util.ArrayList;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import model.AID;
import model.Agent;
import model.AgentType;

@Stateless
@LocalBean
@Path("agents")
public class AgentBean implements AgentBeanRemote {

	@GET
	@Path("test")
	@Produces(MediaType.TEXT_PLAIN)
	public String test() {
		return "test";
	}
	
	@GET
	@Path("classes")
	@Override
	public ArrayList<AgentType> getAllAgentTypes() {
		AgentType agentType = new AgentType();
		return agentType.getAgentTypes();
	}

	@GET
	@Path("running")
	@Override
	public ArrayList<Agent> getAllRunningAgents() {
		// TODO Auto-generated method stub
		return null;
	}

	@PUT
	@Path("running/{type}/{name}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Override
	public void runAgent(@PathParam("type")String agentType, @PathParam("name")String agentName) {
		System.out.println("running agent");
		System.out.println(agentType);
		System.out.println(agentName);
	}

/*	@DELETE
	@Path("running/{aid}")
	@Override
	public void stopRunningAgent(@PathParam("aid")AID aid) {
		// TODO Auto-generated method stub
		
	}*/

}
