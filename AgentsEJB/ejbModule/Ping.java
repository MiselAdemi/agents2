import model.AID;
import model.Agent;

public class Ping extends Agent {

	private static final long serialVersionUID = 1L;

	public Ping() {
		super();
	}
	
	public Ping(AID id) {
		super(id);
	}

	@Override
	public void handleMessage() {
		
	}
	
}
