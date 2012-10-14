package queryparser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import database.DataFile;
import database.DataManager;
import database.Index;
import database.bplusTree.BpTree;
import queryparser.QueryParser;

public class QueryEval {

	static Map<String, Integer> desc;
	static DataFile df1 = null;
	// static DataFile di1 = null;
	static Index i1 = null;
	static Index i2 = null;
	BpTree btree = null;

	public void createTable() {
		desc = new HashMap<String, Integer>();
		System.out.println("Inside the CreateTable Method");

		for (int i = 0; i < QueryParser.finalValues.size(); i++) {
			desc.put(QueryParser.finalValues.get(i), 20);
		}

		df1 = DataManager.createFile(QueryParser.tableName, desc);

	}

	public void insertRecord() {

		Map<String, String> tuple = new HashMap<String, String>();
		//Map<Integer, Map<String, String>> records = new HashMap<Integer, Map<String, String>>();

		for (int i = 0; i < QueryParser.finalValues.size(); i++) {
			tuple.put(QueryParser.finalValues.get(i), QueryParser.values.get(i));
		}

		df1.insertRecord(tuple);

	}

	public void create_index() {
		//DataFile df = null;
		//df1 = DataManager.createFile(QueryParser.tableName, desc);
		df1.createIndex(QueryParser.createIndex ,QueryParser.columnForIndex);
	}

	
	class ValueComparator_asc implements Comparator {

		  Map<Integer, Integer> base;
		  public ValueComparator_asc(Map<Integer, Integer> base) {
		      this.base = base;
		  }

		  public int compare(Object a, Object b) {

		    if(base.get(a) < base.get(b)) {
		      return 1;
		    } else if(base.get(a) == base.get(b)) {
		      return 0;
		    } else {
		      return -1;
		    }
		  }
	}
	
	
	class ValueComparator_desc implements Comparator {

		  Map<Integer, Integer> base;
		  public ValueComparator_desc(Map<Integer, Integer> base) {
		      this.base = base;
		  }

		  public int compare(Object a, Object b) {

		    if(base.get(a) > base.get(b)) {
		      return 1;
		    } else if(base.get(a) == base.get(b)) {
		      return 0;
		    } else {
		      return -1;
		    }
		  }
	}
	
	static <K,V extends Comparable<? super V>>
	SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
	    SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
	        new Comparator<Map.Entry<K,V>>() {
	            @Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
	                return e1.getValue().compareTo(e2.getValue());
	            }
	        }
	    );
	    sortedEntries.addAll(map.entrySet());
	    return sortedEntries;
	}
	
public void naive_asc() throws InterruptedException{
		
		LimitDS lds = new LimitDS();
		ArrayList<LimitDS> queue = new ArrayList<LimitDS>(5);
		int i=0,j=0;
		int sum=0,size=0;
		int limit = QueryParser.limit;
		int max = 0, maxkey = 0;
		List<Index> indexlist = new ArrayList<Index>();
		Map<String, String> record = new HashMap<String, String>();
//		Map<Integer, Integer> final_list = new TreeMap<Integer, Integer>();
//		
//		ValueComparator_asc bvc =  new ValueComparator_asc(final_list);
//        TreeMap<Integer,Integer> sorted_map = new TreeMap<Integer, Integer>(bvc);
//		
//        List<Integer> mylist = new ArrayList<Integer>();
//        Comparator<Integer> comparator = Collections.reverseOrder();
        
        for(j=0;j<QueryParser.orderByIndexes.size();j++){
			if(df1.columnindexMap.containsKey(QueryParser.orderByIndexes.get(j)))
				indexlist.add(df1.indexMap.get(df1.columnindexMap.get(QueryParser.orderByIndexes.get(j))));
		}
        
        System.out.println("No. of records in file: " + df1.records.size());
		for(i=1;i<=df1.records.size();i++){
			sum = 0;
			if(queue.size() < limit){
				record = df1.records.get(i);
				size = indexlist.size()-1;
				while(size >= 0){
					sum += (Integer.parseInt(record.get(QueryParser.orderByIndexes.get(size))) * QueryParser.orderByCoeff.get(size));
					size--;
				}
				LimitDS limitds = new LimitDS();
				limitds.key = i;
				limitds.value = sum;
				queue.add(limitds);
				queue = Sort(queue);
				if(sum > max){
					maxkey = sum;
					max = sum;
				}
			}
			
			else{
				record = df1.records.get(i);
				size = indexlist.size()-1;
				while(size >= 0){
					sum += (Integer.parseInt(record.get(QueryParser.orderByIndexes.get(size))) * QueryParser.orderByCoeff.get(size));
					size--;
				}
				if(max > sum){
					int head = (queue.remove(0)).value;
					System.out.println("head of queue: " + head);
					LimitDS limitds = new LimitDS();
					limitds.key = i;
					limitds.value = sum;
					queue.add(limitds);
					queue = Sort(queue);
					
						max = queue.get(0).value;
						maxkey = queue.get(0).key;
				}
			
		}
	}
		
		int m=0;
		queue = Sort(queue);
		System.out.println("size of queue: " + queue.size());
		System.out.println("record is: ");
		int k = queue.size();
		for(i=0;i<k;i++){
			System.out.println("key : " + queue.get(i).key + "and value is: " + queue.get(i).value );
			int key = queue.get(i).key;
			for(j=0;j<QueryParser.columnNames.size();j++)
				System.out.println(df1.getRecord(key).get(QueryParser.columnNames.get(j)) + " ");
			System.out.println("\n");
		}
}
	
	
	private ArrayList<LimitDS> Sort(ArrayList<LimitDS> queue) {
		
		int i=0,k=queue.get(queue.size()-1).value;
		int j=0;
		int key = queue.get(queue.size()-1).key;
		int value = queue.get(queue.size()-1).value;
		for(i=0;i<queue.size();i++){
			if(k<queue.get(i).value){
				continue;
			}
			else{
				for(j=queue.size()-2;j>=i;j--){
					queue.get(j+1).key = queue.get(j).key;
					queue.get(j+1).value = queue.get(j).value;
				}
				queue.get(i).key = key;
				queue.get(i).value = value;
				return queue;
			}
		}
		return queue;
	
		
	
}

public void naive_desc() throws InterruptedException{
		
		LimitDS lds = new LimitDS();
		ArrayList<LimitDS> queue = new ArrayList<LimitDS>(5);
		int i=0,j=0;
		int sum=0,size=0;
		int limit = QueryParser.limit;
		int min = 1000000, minkey = 0;
		List<Index> indexlist = new ArrayList<Index>();
		Map<String, String> record = new HashMap<String, String>();
//		Map<Integer, Integer> final_list = new TreeMap<Integer, Integer>();
//		
//		ValueComparator_asc bvc =  new ValueComparator_asc(final_list);
//        TreeMap<Integer,Integer> sorted_map = new TreeMap<Integer, Integer>(bvc);
//		
//        List<Integer> mylist = new ArrayList<Integer>();
//        Comparator<Integer> comparator = Collections.reverseOrder();
        
        for(j=0;j<QueryParser.orderByIndexes.size();j++){
			if(df1.columnindexMap.containsKey(QueryParser.orderByIndexes.get(j)))
				indexlist.add(df1.indexMap.get(df1.columnindexMap.get(QueryParser.orderByIndexes.get(j))));
		}
        
        System.out.println("No. of records in file: " + df1.records.size());
		for(i=1;i<=df1.records.size();i++){
			sum = 0;
			if(queue.size() < limit){
				record = df1.records.get(i);
				size = indexlist.size()-1;
				while(size >= 0){
					sum += (Integer.parseInt(record.get(QueryParser.orderByIndexes.get(size))) * QueryParser.orderByCoeff.get(size));
					size--;
				}
				LimitDS limitds = new LimitDS();
				limitds.key = i;
				limitds.value = sum;
				queue.add(limitds);
				queue = Sort_Desc(queue);
				if(sum > min){
					minkey = sum;
					min = sum;
				}
			}
			
			else{
				record = df1.records.get(i);
				size = indexlist.size()-1;
				while(size >= 0){
					sum += (Integer.parseInt(record.get(QueryParser.orderByIndexes.get(size))) * QueryParser.orderByCoeff.get(size));
					size--;
				}
				if(min > sum){
					int head = (queue.remove(0)).value;
					System.out.println("head of queue: " + head);
					LimitDS limitds = new LimitDS();
					limitds.key = i;
					limitds.value = sum;
					queue.add(limitds);
					queue = Sort_Desc(queue);
					
						min = queue.get(0).value;
						minkey = queue.get(0).key;
				}
			
		}
	}
		
		int m=0;
		queue = Sort_Desc(queue);
		System.out.println("size of queue: " + queue.size());
		System.out.println("record is: ");
		int k = queue.size();
		for(i=0;i<k;i++){
			System.out.println("key : " + queue.get(i).key + "and value is: " + queue.get(i).value );
			int key = queue.get(i).key;
			for(j=0;j<QueryParser.columnNames.size();j++)
				System.out.println(df1.getRecord(key).get(QueryParser.columnNames.get(j)) + " ");
			System.out.println("\n");
		}
}
	

private ArrayList<LimitDS> Sort_Desc(ArrayList<LimitDS> queue) {
	
	int i=0,k=queue.get(queue.size()-1).value;
	int j=0;
	int key = queue.get(queue.size()-1).key;
	int value = queue.get(queue.size()-1).value;
	for(i=0;i<queue.size();i++){
		if(k>queue.get(i).value){
			continue;
		}
		else{
			for(j=queue.size()-2;j>=i;j--){
				queue.get(j+1).key = queue.get(j).key;
				queue.get(j+1).value = queue.get(j).value;
			}
			queue.get(i).key = key;
			queue.get(i).value = value;
			return queue;
		}
	}
	return queue;

	

}
	
	
public void selectValuesAsc(){
	
		int i=0,j=0;
		int[] id = new int[100];
		int sum=0,size=0;
		int limit = QueryParser.limit;
		int new_threshold = 0, old_threshold = 0;
		List<Index> indexlist = new ArrayList<Index>();
		Map<String, String> record = new HashMap<String, String>();
		Map<Integer, Integer> final_list = new TreeMap<Integer, Integer>();
		
		ValueComparator_asc bvc =  new ValueComparator_asc(final_list);
        TreeMap<Integer,Integer> sorted_map = new TreeMap<Integer, Integer>(bvc);
		
		for(j=0;j<QueryParser.orderByIndexes.size();j++){
			if(df1.columnindexMap.containsKey(QueryParser.orderByIndexes.get(j)))
				indexlist.add(df1.indexMap.get(df1.columnindexMap.get(QueryParser.orderByIndexes.get(j))));
		}
		
		for(i=0;i<df1.records.size(); i++){
			
			for(j=0;j<indexlist.size();j++){
				
				id[j] = indexlist.get(j).fetchid_asc(i);
				record = df1.records.get(id[j]);
				new_threshold += (Integer.parseInt(record.get(QueryParser.orderByIndexes.get(j))) * QueryParser.orderByCoeff.get(j));
			}
			
		for(j=0;j<indexlist.size();j++){
			
			sum = 0;
			if(final_list.size() < limit){
				record = df1.records.get(id[j]);
				size = indexlist.size()-1;
				while(size >= 0){
					sum += (Integer.parseInt(record.get(QueryParser.orderByIndexes.get(size))) * QueryParser.orderByCoeff.get(size));
					size--;
				}
				final_list.put(id[j], sum);
			}
			
			else{
				record = df1.records.get(id[j]);
				size = indexlist.size()-1;
				while(size >= 0){
					sum += (Integer.parseInt(record.get(QueryParser.orderByIndexes.get(size))) * QueryParser.orderByCoeff.get(size));
					size--;
				}
				final_list.put(id[j], sum);
				if(new_threshold > sum){
					System.out.println("Breaking point reached at " + i);
					sorted_map.putAll(final_list);
					System.out.println("record is: ");
					i=0;
					 Set<Integer> obKeySet=sorted_map.keySet();
			         Iterator<Integer> obIterator=obKeySet.iterator();
			         while (obIterator.hasNext()) {
			        	 int key = obIterator.next();
			        	 	if((limit-1)<i)
			        	 		break;
			        	 	i++;
			        	 	System.out.println("key in final_list is: " + key + "and value is: " + final_list.get(key));
			        	 	for(j=0;j<QueryParser.columnNames.size();j++)
			        	 		System.out.println(df1.getRecord(key).get(QueryParser.columnNames.get(j)) + " ");                 
			           }
			         System.out.println("\n");
					return;
				}
				//old_threshold = new_threshold;
			}
		}
	}
}
	

public void selectValuesDesc(){
		
		int i=0,j=0;
		int[] id = new int[100];
		int sum=0,size=0;
		int limit = QueryParser.limit;
		int threshold = 0;
		List<Index> indexlist = new ArrayList<Index>();
		Map<String, String> record = new HashMap<String, String>();
		Map<Integer, Integer> final_list = new TreeMap<Integer, Integer>();
		
		ValueComparator_desc bvc =  new ValueComparator_desc(final_list);
        TreeMap<Integer,Integer> sorted_map = new TreeMap<Integer, Integer>(bvc);
		
		for(j=0;j<QueryParser.orderByIndexes.size();j++){
			if(df1.columnindexMap.containsKey(QueryParser.orderByIndexes.get(j)))
				indexlist.add(df1.indexMap.get(df1.columnindexMap.get(QueryParser.orderByIndexes.get(j))));
		}
		
		for(i=0;i<df1.records.size(); i++){
			
			for(j=0;j<indexlist.size();j++){
				
				id[j] = indexlist.get(j).fetchid_asc(i);
				record = df1.records.get(id[j]);
				threshold += Integer.parseInt(record.get(QueryParser.orderByIndexes.get(j)));
			}
			
		for(j=0;j<indexlist.size();j++){
			
			sum = 0;
			if(final_list.size() < limit){
				record = df1.records.get(id[j]);
				size = indexlist.size()-1;
				while(size >= 0){
					sum += (Integer.parseInt(record.get(QueryParser.orderByIndexes.get(size))) * QueryParser.orderByCoeff.get(size));
					size--;
				}
				final_list.put(id[j], sum);
			}
			
			else{
				record = df1.records.get(id[j]);
				size = indexlist.size()-1;
				while(size >= 0){
					sum += (Integer.parseInt(record.get(QueryParser.orderByIndexes.get(size))) * QueryParser.orderByCoeff.get(size));
					size--;
				}
				final_list.put(id[j], sum);
				
				if(threshold < sum){
					System.out.println("Breaking point reached");
					sorted_map.putAll(final_list);
					System.out.println("record is: ");
					i=0;
					 Set<Integer> obKeySet=sorted_map.keySet();
			         Iterator<Integer> obIterator=obKeySet.iterator();
			         while (obIterator.hasNext()) {
			        	 int key = obIterator.next();
			        	 System.out.println("key in final_list is: " + key + "and value is: " + final_list.get(key));
			        	 	if((limit-1)<i)
			        	 		break;
			        	 	i++;
			        	 	for(j=0;j<QueryParser.columnNames.size();j++)
			        	 		System.out.println(df1.getRecord(key).get(QueryParser.columnNames.get(j)) + " ");                 
			           }
			         System.out.println("\n");
			         return;
				}
			}
		}
	}
}		
}