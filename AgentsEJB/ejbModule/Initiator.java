import javax.ejb.Remote;
import javax.ejb.Stateful;

import model.ACLMessage;
import model.AID;
import model.Agent;
import model.Performative;
import session.AgentBeanRemote;
import session.MessageBeanRemote;
import utils.Container;

@Stateful
@Remote(Agent.class)
public class Initiator extends Agent {

	private static final long serialVersionUID = 1L;
	private int pendingProposals;
	private static final int NUM_PARTICIPANTS = 16;

	public Initiator(){
		super();
	}

	public Initiator(AID id){
		super(id);
	}

	@Override
	public void handleMessage(ACLMessage msg){
		switch(msg.getPerformative()){
		case REQUEST:
			Container.getInstance().log("[REQUEST]Initiator has received a message: "/* + msg*/);
			AID[] participants = createParticipants();
			ACLMessage message = new ACLMessage(Performative.CALL_FOR_PROPOSAL);
			message.setSender(getId());
			message.setReceivers(participants);
			MessageBeanRemote mbr = findMB();
			mbr.sendMessage(message);
			pendingProposals = participants.length;
			break;
		case ACCEPT_PROPOSAL:
			Container.getInstance().log("[ACCEPT_PROPOSAL]Initiator has received a message: "/* + msg*/);
			--pendingProposals;
			if(pendingProposals == 0){
				Container.getInstance().log("All proposals have been accepted!");
			}
			break;
		default:
			Container.getInstance().log("Message not understood " /*+ msg*/);
		}
	}

	private AID[] createParticipants() {
		AID[] participants = new AID[NUM_PARTICIPANTS];
		//kreiraj agente
		AgentBeanRemote abr = findAB();

		for(int i=0; i<NUM_PARTICIPANTS; i++){
			Agent a = abr.runAgent("ContractNet$Participant", "Participant" + i);
			participants[i] = a.getId();
		}

		return participants;
	}

}
