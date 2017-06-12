import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class Slave extends Agent {

	private static final long serialVersionUID = 1L;

	public Slave(){
		super();
	}

	public Slave(AID id){
		super(id);
	}

	@Override
	public void handleMessage(ACLMessage msg){
		String documentPath = msg.getContent();
		Map<Character, Integer> mapReduce = new HashMap<>();
		
		try (BufferedReader br = new BufferedReader(new FileReader(documentPath))) {
			for(String line = br.readLine(); line != null; line = br.readLine()) {
				line = line.toLowerCase();
				Charset.forName("UTF-8").encode(line);
				
				for(int i = 0; i < line.length(); i++) {
					Character c = line.charAt(i);
					if(Character.isLetterOrDigit(c)) {
						if(mapReduce.containsKey(c)) {
							mapReduce.put(c, mapReduce.get(c) + 1);
						}else {
							mapReduce.put(c, 1);
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(mapReduce);
		ACLMessage reply = new ACLMessage(Performative.INFORM);
		reply.addReceiver(msg.getSender());
		reply.setSender(getId());
		reply.setContent(formReply(mapReduce));
		MessageBeanRemote mbr = findMB();
		mbr.sendMessage(reply);
		Container.getInstance().log(getId().getName() + " is sending reply...");
	}
	
	private String formReply(Map<Character, Integer> mapReduce) {
		String retVal = "Statistics:";
		for (Map.Entry<Character, Integer> entry : mapReduce.entrySet()) {
			retVal += "\n" + entry.getKey() + ":" + entry.getValue();
		}
		return retVal;
	}

}
