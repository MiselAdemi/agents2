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
		Container.getInstance().log("Message to Ping: " + message);
		
		if(message.getPerformative().equals(Performative.REQUEST)) {
			ACLMessage msgToPong = new ACLMessage(Performative.REQUEST);
			msgToPong.setSender(getId());
			msgToPong.addReceiver(message.getReceivers()[0]);
			MessageBeanRemote messageBean = findMB();
			messageBean.sendMessage(msgToPong);
		}
		else if(message.getPerformative().equals(Performative.INFORM)) {
			//reply to the original sender (if any)
			if(message.getSender() != null || message.getReplyTo() != null){
				ACLMessage reply = new ACLMessage(Performative.CONFIRM);
				reply.addReceiver(message.getReplyTo()!=null? message.getReplyTo() : message.getSender());
				reply.setSender(getId());
				reply.setContent("Message received. PingPong successfully completed.");
				MessageBeanRemote messageBean = findMB();
				messageBean.sendMessage(reply);
			}
		}
	}
	
}
