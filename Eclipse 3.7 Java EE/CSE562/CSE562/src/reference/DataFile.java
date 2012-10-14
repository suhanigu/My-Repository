package reference;
 
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import reference.*;


/* Author: Suhani Gupta */


public class DataFile implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4298564775253437099L;
	
	public String fileName;
	public Map<String, Integer> descriptor = new HashMap<String, Integer>();
	public Map<String, Index> indexMap = new TreeMap<String, Index>();
	@SuppressWarnings("rawtypes")
	public Map<Integer, Map<String, String>> records = new HashMap<Integer, Map<String,String>>();
	//public Map<Integer, Map<String, String>> record_del = new HashMap<Integer, Map<String,String>>();
	public Map<String,String> columnindexMap = new TreeMap<String, String>();
	int recordnum = 0;
	
	public DataFile(String filename, Map<String, Integer> descriptor)
	{
		this.fileName = filename;
		this.descriptor = descriptor;
	}
	
	public DataFile(String filename)
	{
		this.fileName = filename;
	}
	
	public DataFile() {
		
	}

	public Index createIndex(String indexName, String column) 
	{
		int totalrecords = 0;
		String recordvalue = "";
		Index index = null;
		Map<String, String> temporaryrecord = new TreeMap<String, String>();
		
		if(indexMap!=null && indexMap.containsKey(indexName))
			throw new IllegalArgumentException();
		if(descriptor.containsKey(column))
		{
			if(descriptor.get(column)>24)
				throw new IllegalArgumentException();
		
			index = new Index(fileName, indexName, column);
			totalrecords = records.size();
			int tr = 1;
			int count = 0;
			while(totalrecords >= tr)
			{
				temporaryrecord = records.get(tr);
				recordvalue = temporaryrecord.get(column);
				if(recordvalue==null || recordvalue=="")
					throw new IllegalArgumentException();
				else{
				index.insertIntoBPTree(recordvalue, tr);
				}
				tr++;
			}
			indexMap.put(indexName, index);
			columnindexMap.put(column,indexName);
		}
		
		return index;
		
	}
	
	
	public Map<String, String> getRecord(int recordid)
	{
		Map<String, String> record = new HashMap<String, String>();
		Map<String, String> record_copy = new HashMap<String, String>();
		
		int status = 1;
		
		if(recordnum < recordid)
			return null;
		
//		while(status==1){
		if(records.get(recordid).get("delflag") == "true")
			return null;
//		else{
		record = records.get(recordid);
		record_copy.putAll(record);
		record_copy.remove("delflag");
		//record.remove("delflag");
//		status = 0;
//		break;
//			}
//		}
		//record = records.get(recordid);
		return record_copy;
	}
	
	public int insertRecord(Map<String, String> record) 
	{
		int indexsize=0;
		int recordid = 0;
		String recordkey = "";
		String recordvalue = "";
		Index index = null;
		String indexname = "";
		String filename = "";
		Set<String> recordset = new HashSet<String>();
		Iterator<String> recorditerator = null;
		Map<String, String> record_copy = new HashMap<String, String>();
		
		record_copy.putAll(record);
		
		
		if(record_copy == null)
			throw new IllegalArgumentException();
		
		recordnum++;
		recordid = recordnum;
		
		recordset = record_copy.keySet();
		recorditerator = recordset.iterator();
		while(recorditerator.hasNext())
		{
			recordkey = recorditerator.next();
			recordvalue = record_copy.get(recordkey);
			if(recordvalue.length() > 24)
				throw new IllegalArgumentException();
			if(!descriptor.containsKey(recordkey))
			{
				throw new IllegalArgumentException();
			}
		
		indexsize = indexMap.size();
		while(indexsize>0)
		{
		indexname = columnindexMap.get(recordkey);
		if(indexname !=null ){
			indexsize = 0;
		index = indexMap.get(indexname);
		if(recordvalue != null && !recordvalue.equals("")){
			index.insertIntoBPTree(recordvalue, recordid);
		}
		indexsize--;
		}
		else
			break;
		}
		
		}
		//deletestatus.put(Integer.toString(recordnum), "false");
		record_copy.put("delflag","false");
		records.put(recordnum, record_copy);
		
		//record_del.put(recordnum, record_copy);
		return recordnum;
	}
	
	
	public void dumpFile()
	{
		ObjectOutputStream outputFileObj = null;
		try {
			if(fileName != null){
			outputFileObj = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(this.fileName)));
			outputFileObj.writeObject(this);
			outputFileObj.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}
	
	public Map<Integer, Map<String, String>> viewFile()
	{
		String filecontent = "";
		int descriptorsize = 0;
		int recordno = 0;
		int recordnumber = 1;
		int descsize = 0;
		String recordvalue = "";
		Set<String> descriptorset = new HashSet<String>();
		Iterator<String> descriptoriterator = null;
		ArrayList<String> attributes = new ArrayList<String>();
		Map<String, String> record = new TreeMap<String, String>();
		Map<Integer, Map<String, String>> recordlist = new HashMap<Integer, Map<String, String>>();
		int i = 0;
		
		descriptorsize = descriptor.size();
		descriptorset = descriptor.keySet();
		descriptoriterator = descriptorset.iterator();
		while(descriptoriterator.hasNext())
		{
//			if(i==(descriptorsize-1))
//				break;
			attributes.add(descriptoriterator.next());
			i++;
		}
		
		recordno = records.size();
		while(recordno>=recordnumber)
		{
			record = records.get(recordnumber);
			record.remove("delflag");
			recordlist.put(recordnumber, record);
			descsize = 0;
			while(descsize<descriptorsize)
			{
				recordvalue = record.get(attributes.get(descsize));
				filecontent = filecontent + recordvalue +":" + attributes.get(descsize) +":";
				descsize++;
			}
			filecontent  = filecontent + "\n";
			recordnumber++;
		}
		
		//return filecontent;
		return recordlist;
		
	}
	
	public void dropFile()
	{
		int indexcount = 0;
		int indcnt=0;
		Index index=null;
		String indexname = "";
		String columnname = "";
		String recordkey = "";
		Set<String> recordset = new HashSet<String>();
		Iterator<String> recorditerator = null;
		Map<String, String> record = new TreeMap<String, String>();
		File file = null;
		
		indexcount = indexMap.size();
		while(indcnt < indexcount)
		{
			record = records.get(indcnt);
			recordset = record.keySet();
			recorditerator = recordset.iterator();
			while(recorditerator.hasNext())
			{
				recordkey = recorditerator.next();
				columnname = record.get(recordkey);
			}
			indexname = columnindexMap.get(columnname);
			indexMap.remove(indexname);
			indcnt++;
		}
		indexMap = null;
		columnindexMap = null;
		descriptor = null;
		records = null;
		
		file = new File(fileName);
		if(file.exists())
			file.delete();
	}
	
	
	public Index restoreIndex(String indexName) throws ClassNotFoundException
	{
		File indexfile = null;
		String file_name = fileName+"."+indexName;
		ObjectInputStream inputFileObj = null;
		Index index = null;
		
		if(indexMap!=null && indexMap.containsKey(indexName))
			throw new IllegalArgumentException();
		
		indexfile = new File(file_name);
		if(!indexfile.exists())
			throw new IllegalArgumentException();
		
		try {
			inputFileObj = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file_name)));
			index.filenm = file_name;
			index.indexName = indexName;
			index.column = (String)inputFileObj.readObject();
			index.temptree = (BpTree)inputFileObj.readObject();
			inputFileObj.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void dropIndex(String indexName)
	{
		indexMap.remove(indexName);
		columnindexMap.remove(indexName);
		
	}
	
	public void rebuildfile()
	{
		int recordsize = records.size();
		String delflag = "delflag";
		while(recordsize>0)
		{
			if(records.get(recordsize).get(delflag) == "true")
				records.remove(recordsize);
		}
	}
	
	
	public Iterator<Integer> iterator()
	{
		int size = 1;
		int recordsize = records.size();
//		System.out.println("records size" + recordsize);
//		String delflag = "delflag"; 
//		while(recordsize > 0){
//			if(records.get(size).get(delflag) == "false")
//			size++;
//			recordsize--;
//		}
//		size--;
//		System.out.println("Size: " + size);
		FileIterator fileiterator = new FileIterator(recordsize);
		return fileiterator;
	}
	
	private class FileIterator implements Iterator<Integer>
	{

		int cursor = 0;
		int recordremaining = 0;
		String delflag = "delflag";
		int delete = 1;
		
		public FileIterator(int size) {
			cursor=size;
		}

		@Override
		public boolean hasNext() {
			recordremaining++;
			while(delete==1){
			if(cursor>0 && records.get(recordremaining).get(delflag) == "false"){
				//System.out.println("recordremaining: " + recordremaining);
				//recordremaining++;
				return true;
			}
			else if(cursor>0 && records.get(recordremaining).get(delflag) == "true"){
				cursor--;
				recordremaining++;
				delete = 1;
			}
			else
				return false;
			}
			return false;
		}

		@Override
		public Integer next() {
			cursor--;
			return recordremaining;
			
		}

		@Override
		public void remove() {
			//---------------------------------------------------------
			int indexsize=0;
			int recordid = 0;
			String recordkey = "";
			String recordvalue = "";
			Index index = null;
			String indexname = "";
			String filename = "";
			Set<String> recordset = new HashSet<String>();
			Iterator<String> recorditerator = null;
			Map<String, String> record_copy = new HashMap<String, String>();
			
			
//			while(records.get(recordremaining)==null)
//			{
//				recordremaining++;
//			}
			
			record_copy.putAll(records.get(recordremaining));
			record_copy.remove("delflag");
			
			recordid = recordremaining;
			
			if(record_copy == null)
				throw new IllegalArgumentException();
			
			recordset = record_copy.keySet();
			recorditerator = recordset.iterator();
			while(recorditerator.hasNext())
			{
				recordkey = recorditerator.next();
				recordvalue = record_copy.get(recordkey);
				if(recordvalue.length() > 24)
					throw new IllegalArgumentException();
				if(!descriptor.containsKey(recordkey))
				{
					throw new IllegalArgumentException();
				}
			
			indexsize = indexMap.size();
			while(indexsize>0)
			{
			indexname = columnindexMap.get(recordkey);
			if(indexname !=null ){
				indexsize = 0;
			index = indexMap.get(indexname);
			if(recordvalue != null && !recordvalue.equals("")){
				index.delete(recordvalue, recordid);
			}
			indexsize--;
			}
			else
				break;
			}
			
			}
			
//			recordnum--;
//			deletestatus.remove(Integer.toString(recordid));
//			deletestatus.put(Integer.toString(recordid), "true");
			//record_del.get(recordid).remove("delflag");
			records.get(recordid).put("delflag","true");
			
			//records.remove(recordid);
			
			
			
		}
		
	}
}
