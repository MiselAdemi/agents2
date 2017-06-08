package session;

import javax.ejb.Remote;

import model.AgentCenter;

@Remote
public interface AgentCenterBeanRemote {

	public boolean isMaster();
	
	public void registerMe(AgentCenter agentCenter);
	
	public void getAllSupportedAgents(String address);
	
	public void forwardNewAgentTypes();
	
	public void forwardRunningAgents();
	
	public void deleteNode(String alias);
	
	public void checkIfAlive();
}
