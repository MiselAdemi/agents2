package session;

import java.util.ArrayList;

import javax.ejb.Remote;

import model.AgentCenter;
import model.AgentType;

@Remote
public interface AgentCenterBeanRemote {

	public boolean isMaster();
	
	public void registerMe(AgentCenter agentCenter);
	
	public ArrayList<AgentType> getAllSupportedAgents(String address);
	
	public void forwardNewAgentTypes(AgentTypes agentTypes);
	
	public void forwardRunningAgents(RunningAgents ra);
	
	public void deleteNode(String alias);
	
	public void checkIfAlive();
	
	public void registerHosts(AgentHosts hosts);
}
