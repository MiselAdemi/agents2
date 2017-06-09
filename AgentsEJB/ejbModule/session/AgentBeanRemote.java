package session;

import java.util.ArrayList;

import javax.ejb.Remote;

import model.AID;
import model.Agent;
import model.AgentType;

@Remote
public interface AgentBeanRemote {

	/**
	 * Get list of all agent types
	 * @return
	 */
	public ArrayList<AgentType> getAllAgentTypes();
	
	/**
	 * Get all running agents
	 * @return
	 */
	public ArrayList<Agent> getAllRunningAgents();
	
	/**
	 * Run agent with given type and name
	 * @param agentType
	 * @param agentName
	 */
	public Agent runAgent(String agentType, String agentName);

	/**
	 * Stop agent
	 * @param aid
	 */
	public void stopRunningAgent(AID aid);
}
