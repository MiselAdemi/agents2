package session;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import model.AgentCenter;
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
		if(!Container.isMaster()) {
			System.out.println("Not master node, register");
		}else {
			System.out.println("Master node");
		}
		
		System.out.println(agentCenter.toString());
	}

	@GET
	@Path("agents/classes")
	@Override
	public void getAllSupportedAgents() {
		// TODO Auto-generated method stub
		
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
