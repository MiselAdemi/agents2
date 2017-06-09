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
public class Participant extends Agent {

	private static final long serialVersionUID = 1L;

	public Participant(){
		super();
	}

	public Participant(AID id){
		super(id);
	}


	@Override
	public void handleMessage(ACLMessage msg){
		Container.getInstance().log(getId().getName() + " has received a message: "/* + msg*/);
		ACLMessage reply = new ACLMessage(Performative.ACCEPT_PROPOSAL);
		reply.addReceiver(msg.getReplyTo()!=null? msg.getReplyTo(): msg.getSender());
		reply.setProtocol("ContractNet");
		MessageBeanRemote mbr = findMB();
		mbr.sendMessage(reply);
	}

}
