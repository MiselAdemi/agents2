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
public class Pong extends Agent {

	private static final long serialVersionUID = 1L;

	public Pong() {
		super();
	}

	public Pong(AID id) {
		super(id);
		Container.getInstance().log("Pong created");
	}

	@Override
	public void handleMessage(ACLMessage message) {
		if(message.getPerformative().equals(Performative.REQUEST)){
			Container.getInstance().log("[REQUEST] to Pong: " + message.getContent());
			
			ACLMessage reply = new ACLMessage(Performative.INFORM);
			reply.addReceiver(message.getSender());
			reply.setSender(getId());
			reply.setContent("Pong get message successfully from Ping!");
			Container.getInstance().log("Pong is replying to Ping...");
			MessageBeanRemote messageBean = findMB();
			messageBean.sendMessage(reply);
		}
	}

}
