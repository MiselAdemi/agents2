import model.Agent;
import model.AgentCenter;

public class Ping extends Agent {

	public Ping() {
		super();
	}
	
	public Ping(String id, AgentCenter agentCenter) {
		super(id, agentCenter);
	}

	@Override
	public void handleMessage() {
		
	}
	
}
