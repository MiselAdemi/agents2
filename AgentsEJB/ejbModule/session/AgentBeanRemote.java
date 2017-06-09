package session;

import java.util.ArrayList;

import javax.ejb.Remote;

import model.AID;
import model.Agent;

@Remote
public interface AgentBeanRemote {

	/**
	 * Get list of all agent types
	 * @return
	 */
	public AgentTypes getAllAgentTypes();
	
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
	public void runAgent(String agentType, String agentName);

	/**
	 * Stop agent
	 * @param aid
	 */
	public void stopRunningAgent(AID aid);
}
