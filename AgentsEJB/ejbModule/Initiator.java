import javax.ejb.Remote;
import javax.ejb.Stateful;

import model.ACLMessage;
import model.AID;
import model.Agent;
import model.Performative;
import session.AgentBeanRemote;
import session.MessageBeanRemote;

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
			AID[] participants = createParticipants();
			ACLMessage message = new ACLMessage(Performative.CALL_FOR_PROPOSAL);
			message.setSender(getId());
			message.setReceivers(participants);
			MessageBeanRemote mbr = findMB();
			mbr.sendMessage(message);
			pendingProposals = participants.length;
			break;
		case ACCEPT_PROPOSAL:
			--pendingProposals;
			if(pendingProposals == 0){
				System.out.println("All proposals have been accepted!");
				break;
			}
		default:
			System.out.println("Message not understood: " + msg);
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
