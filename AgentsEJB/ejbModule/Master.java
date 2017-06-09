import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;

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
public class Master extends Agent {

	private static final long serialVersionUID = 1L;
	private Map<String, Integer> map = new HashMap<>();
	private ArrayList<Agent> slaves = new ArrayList<>();

	public Master(){
		super();
	}

	public Master(AID id){
		super(id);
	}

	@Override
	public void handleMessage(ACLMessage msg){
		if(msg.getPerformative().equals(Performative.REQUEST)){
			System.out.println("Master agent: " + msg);
			String directoryPath = msg.getContent();

			//get files
			File folder = new File(directoryPath);
			File[] documents = folder.listFiles();
			System.out.println("Number of documents: " + documents.length);

			//create slaves
			for(int i=0; i<documents.length; i++){
				AgentBeanRemote abr = findAB();
				Agent a = abr.runAgent("MapReduce$Slave", "Slave"+i);
				slaves.add(a);
			}
			for(int i=0; i<documents.length; i++){
				if(documents[i].isFile()){
					ACLMessage message = new ACLMessage(Performative.REQUEST);
					AID slaveAID = slaves.get(i).getId();
					message.setSender(getId());
					message.addReceiver(slaveAID);
					String document = directoryPath + "/" + documents[i].getName();
					message.setContent(document);
					MessageBeanRemote mbr = findMB();
					mbr.sendMessage(message);
				}
			}			
		}
		else if(msg.getPerformative().equals(Performative.INFORM)){
			System.out.println("RESPONSE: " + msg);
			String stringMap = msg.getContent();
			Properties props = new Properties();
			try{
				props.load(new StringReader(stringMap.substring(1, stringMap.length()-1).replace(",", "\n")));
				for(Map.Entry<Object, Object> e: props.entrySet()){
					String key = (String)e.getKey();
					Integer value = Integer.parseInt((String)e.getValue());
					if(map.containsKey(key)){
						map.put(key, map.get(key) + value);
					}
					else{
						map.put(key, value);
					}
				}
			}catch(IOException e){
				e.printStackTrace();
			}
			TreeMap<String, Integer> sortedMap = sortMapByValue(map);
			System.out.println("---------------");
			if(sortedMap.size()<10)
				System.out.println("MAPA: " + sortedMap.toString());
			else{
				int counter = 0;
				Iterator<Entry<String, Integer>> it = sortedMap.entrySet().iterator();
				while(it.hasNext()){
					if(counter==10)
						break;
					System.out.println(it.next().toString());
					++counter;
				}
			}
			System.out.println("-------------------");
		}		
	}

	private TreeMap<String, Integer> sortMapByValue(Map<String, Integer> map2) {
		Comparator<String> comparator = this.new ValueComparator(map2);
		TreeMap<String, Integer> result = new TreeMap<String, Integer>(comparator);
		result.putAll(map2);
		return result;
	}

	private class ValueComparator implements Comparator<String>{
		HashMap<String, Integer> map = new HashMap<String, Integer>();

		public ValueComparator(Map<String, Integer> map){
			this.map.putAll(map);
		}

		@Override
		public int compare(String s1, String s2){
			if(map.get(s1)>=map.get(s2))
				return -1;
			else
				return 1;
		}
	}

}
