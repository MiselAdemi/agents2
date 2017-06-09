import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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

@Stateful
@Remote(Agent.class)
public class Slave extends Agent {

	private static final long serialVersionUID = 1L;
	private Integer MINIMUM_DISTANCE = 3;


	public Slave(){
		super();
	}

	public Slave(AID id){
		super(id);
	}

	@Override
	public void handleMessage(ACLMessage msg){
		String documentPath = msg.getContent();
		HashMap<String, Integer> map = new HashMap<>();
		String line = null;
		try{
			FileReader fr = new FileReader(documentPath);
			BufferedReader br = new BufferedReader(fr);
			while((line=br.readLine())!=null){
				Pattern p = Pattern.compile("[\\w']+");
				Matcher m = p.matcher(line);
				while(m.find()){
					String word = line.substring(m.start(), m.end()).toLowerCase();
					if(map.containsKey(word))
						map.put(word, map.get(word)+1);
					else if(checkLevenshteinDistance(map, word)!=null){
						map.put(checkLevenshteinDistance(map, word),
								map.get(checkLevenshteinDistance(map, word))+1);
					}
					else{
						map.put(word, 1);
					}
				}
			}
			br.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		ACLMessage reply = new ACLMessage(Performative.INFORM);
		reply.addReceiver(msg.getSender());
		reply.setSender(getId());
		reply.setContent(map.toString());
		MessageBeanRemote mbr = findMB();
		mbr.sendMessage(reply);
	}

	//returns true if the distance is acceptable
	String checkLevenshteinDistance(HashMap<String, Integer> map, String rhs){
		String retVal = null;
		Iterator<Entry<String, Integer>> it = map.entrySet().iterator();

		while(it.hasNext()){
			Map.Entry<String, Integer> pair = (Map.Entry<String, Integer>)it.next();
			String lhs = pair.getKey();

			int len0 = lhs.length() + 1;                                                     
			int len1 = rhs.length() + 1;                                                     

			// the array of distances                                                       
			int[] cost = new int[len0];                                                     
			int[] newcost = new int[len0];                                                  

			// initial cost of skipping prefix in String s0                                 
			for (int i = 0; i < len0; i++) cost[i] = i;                                     

			// dynamically computing the array of distances                                  

			// transformation cost for each letter in s1                                    
			for (int j = 1; j < len1; j++) {                                                
				// initial cost of skipping prefix in String s1                             
				newcost[0] = j;                                                             

				// transformation cost for each letter in s0                                
				for(int i = 1; i < len0; i++) {                                             
					// matching current letters in both strings                             
					int match = (lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1;             

					// computing cost for each transformation                               
					int cost_replace = cost[i - 1] + match;                                 
					int cost_insert  = cost[i] + 1;                                         
					int cost_delete  = newcost[i - 1] + 1;                                  

					// keep minimum cost                                                    
					newcost[i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
				}                                                                           

				// swap cost/newcost arrays                                                 
				int[] swap = cost; cost = newcost; newcost = swap;                          
			}                                                                               

			// the distance is the cost for transforming all letters in both strings
			if(cost[len0-1]<=MINIMUM_DISTANCE){
				retVal = lhs;
				break;
			}
			//it.remove();
		}		
		return retVal;
	}

}
