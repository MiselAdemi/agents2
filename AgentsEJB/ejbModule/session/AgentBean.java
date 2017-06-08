package session;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import model.AID;
import model.Agent;
import model.AgentType;
import model.Container;

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
		System.out.println(Container.getInstance().getRunningAgents().size());
		System.out.println("RA " + Container.getInstance().getRunningAgents());
		return Container.getInstance().getRunningAgents();
	}

	@PUT
	@Path("running/{type}/{name}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Override
	public void runAgent(@PathParam("type")String agentType, @PathParam("name")String agentName) {
		String host = AID.HOST_NAME;
		System.out.println(agentType);
		AgentType at = new AgentType(agentName, "PingPong");
		AID aid = new AID(agentName, host, at);
		String className = agentType.split("\\$")[1];
		
		try {
			Class<?> cls = Class.forName(className);
			Constructor<?> constructor = cls.getConstructor(String.class);
			Object object = constructor.newInstance(new Object[]{agentType + ":  " + agentName});
			Container.getInstance().addRunningAgents((Agent) object);
		}catch (SecurityException | ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

/*	@DELETE
	@Path("running/{aid}")
	@Override
	public void stopRunningAgent(@PathParam("aid")AID aid) {
		// TODO Auto-generated method stub
		
	}*/

}
