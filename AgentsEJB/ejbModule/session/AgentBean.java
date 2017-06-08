package session;

import java.util.ArrayList;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import model.AID;
import model.Agent;
import model.AgentType;

@Stateless
@Path("agents")
@LocalBean
public class AgentBean implements AgentBeanRemote {

	@GET
	@Path("classes")
	@Override
	public ArrayList<AgentType> getAllAgentTypes() {
		// TODO Auto-generated method stub
		return null;
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
	@Override
	public void runAgent(@PathParam("type")AgentType agentType, @PathParam("name")String agentName) {
		// TODO Auto-generated method stub
		
	}

	@DELETE
	@Path("running/{aid}")
	@Override
	public void stopRunningAgent(@PathParam("aid")AID aid) {
		// TODO Auto-generated method stub
		
	}

}
