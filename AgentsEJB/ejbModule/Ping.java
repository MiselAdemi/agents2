import javax.ejb.Remote;
import javax.ejb.Stateful;

import model.ACLMessage;
import model.AID;
import model.Agent;
import model.Performative;
import session.MessageBeanRemote;
import utils.Container;

@Stateful
@Remote(Agent.class)
public class Ping extends Agent {

	private static final long serialVersionUID = 1L;
	
	public Ping() {
		super();
	}
	
	public Ping(AID id) {
		super(id);
		Container.getInstance().log("Ping created");
	}

	@Override
	public void handleMessage(ACLMessage message) {
		
		if(message.getPerformative().equals(Performative.REQUEST)) {
			Container.getInstance().log("[REQUEST] to <bold>Ping</bold>: " + message.getContent());
			
			AID pongAid = message.getReplyTo();
			ACLMessage msgToPong = new ACLMessage(Performative.REQUEST);
			msgToPong.setSender(getId());
			msgToPong.addReceiver(pongAid);
			msgToPong.setContent("Hello Pong");
			MessageBeanRemote messageBean = findMB();
			messageBean.sendMessage(msgToPong);
		}
		else if(message.getPerformative().equals(Performative.INFORM)) {
			Container.getInstance().log("[INFORM] to Ping: " + message.getContent());
			Container.getInstance().log("Ping received INFORM from Pong: " + message.getContent());
		}
	}
	
}
