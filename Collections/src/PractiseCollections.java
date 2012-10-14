import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.text.html.HTMLDocument.Iterator;


public class PractiseCollections implements Comparable<PractiseCollections> {
	
	static LinkedList<Integer> list = new LinkedList<Integer>();
	static Map<Integer,String> hashmap = new HashMap<Integer,String>();
	static Set<Integer> set = new HashSet<Integer>();
	static Map<Integer,String> linkedhashmap = new LinkedHashMap<Integer,String>();
	static Map<Integer,String> treemap = new TreeMap<Integer,String>();
	static Queue<Integer> priorityq = new PriorityQueue<Integer>();
	int var;
	
	PractiseCollections(Integer x){
		this.var = x;
	}
	
	@Override
	public int compareTo(PractiseCollections pc) {
		return this.var*(-1) - pc.var*(-1)	;
	}
	
	public boolean equals(Object obj){
		PractiseCollections pc = (PractiseCollections)obj;
		if(pc.var == this.var){
			return true;
		}
		return false;	
	}
	
	public int hashcode(){
		return (int) this.var + 10;
	}
	
	public static void printlist(){
		ListIterator li = list.listIterator();
		while(li.hasNext()){
			System.out.println(li.next());
		}
	}
	
	public static void printhashmap(){
		java.util.Iterator<Entry<Integer, String>> hmi = hashmap.entrySet().iterator();
		while(hmi.hasNext()){
			Entry<Integer, String> entry = hmi.next();
			System.out.println(entry.getKey() + "," + entry.getValue());
		}
		
	}
	
	public static void printset(){
		java.util.Iterator<Integer> hsi = set.iterator();
		while(hsi.hasNext()){
			System.out.println(hsi.next());
		}
	}
	
	public static void printlinkedhashmap(Map<Integer, String> copy){
		java.util.Iterator<Entry<Integer, String>> lhmi = copy.entrySet().iterator();
		while(lhmi.hasNext()){
			Entry<Integer, String> entry = lhmi.next();
			System.out.println(entry.getKey() + "," + entry.getValue());
		}
	}
	
	public static void createcopy(Map<Integer,String> hashmap){
		Map<Integer,String> copy = new LinkedHashMap<Integer,String>(hashmap);
		printlinkedhashmap(copy);
	}
	
	public static void printtreemap(){
		java.util.Iterator<Entry<Integer, String>> tmi = treemap.entrySet().iterator();
		while(tmi.hasNext()){
			Entry<Integer, String> entry = tmi.next();
			System.out.println(entry.getKey() + "," + entry.getValue());
		}
	}
	
	public static void printpriorityq(){
		java.util.Iterator<Integer> pqi = priorityq.iterator();
		while(pqi.hasNext()){
			System.out.println(pqi.next());
		}
	}
	
	public static void printcomplist(List<PractiseCollections> pclist){
		java.util.Iterator<PractiseCollections> pclisti = pclist.iterator();
		while(pclisti.hasNext()){
			System.out.println(pclisti.next().var);
		}
	}
	
	public static void main(String[] args){
		
		//Comparable
		List<PractiseCollections> pclist = new ArrayList<PractiseCollections>();
		pclist.add(new PractiseCollections(1));
		pclist.add(new PractiseCollections(5));
		pclist.add(new PractiseCollections(2));
		pclist.add(new PractiseCollections(7));
		pclist.add(new PractiseCollections(4));
		printcomplist(pclist);
		Collections.sort(pclist);
		printcomplist(pclist);
		
		//Priorityqueue
//		priorityq.add(4);
//		priorityq.add(1);
//		priorityq.add(7);
//		System.out.println("first element " + priorityq.peek());
//		priorityq.poll();
//		printpriorityq();
		
		
		//testing equals and hashcode overriding methods
//		PractiseCollections pc1 = new PractiseCollections(5);
//		PractiseCollections pc2 = new PractiseCollections(6);
//		PractiseCollections pc3 = new PractiseCollections(5);
//		
//		if(pc1.equals(pc2)){
//			System.out.println("PC1 equals PC2");
//		}
//		else if(pc1.equals(pc3)){
//			System.out.println("PC1 equals PC3");
//		}
//		else{
//			System.out.println("PC1 is neither equal to PC2 nor PC3");
//		}
		
		//hashmap
//		hashmap.put(1, "Suhani");
//		hashmap.put(3, "Ankita");
//		hashmap.put(2, "Anil");
//		hashmap.put(null,null);
//		printhashmap();
//				
		//linkedhashmap
//		linkedhashmap.put(1,"Suhani");
//		linkedhashmap.put(3, "Ankita");
//		linkedhashmap.put(2, "Anil");
//		printlinkedhashmap(linkedhashmap);
//		createcopy(hashmap);
		
		//treemap
//		treemap.put(1,"Suhani");
//		treemap.put(5,"Ankita");
//		treemap.put(2,"Anil");
//		treemap.put(4,"Suriti");
//		treemap.put(3,null);
//		treemap.put(5,"tondu");
//		printtreemap();

		
		//hashset
//		set.add(1);
//		set.add(4);
//		set.add(2);
//		set.add(5);
//		set.add(3);
//		set.add(1);
//		printset();
		
		//linkedlist
//		list.add(1);
//		list.add(2);
//		list.add(5);
//		printlist();
//		list.remove(2);
//		printlist();
//		list.clear();
	}

	

}
