
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
import BTree.BPTree;

/* Author: Suhani Gupta */


public class DataFile implements Serializable{

	public String filename = "";
	public Map<String, Integer> descriptor = new HashMap<String, Integer>();
	public Map<String, Index> indexMap = new TreeMap<String, Index>();
	public Map<Integer, Map> recordMap = new TreeMap<Integer, Map>();
	public Map<String,String> columnindexMap = new TreeMap<String, String>();
	int recordnum = 0;
	
	public DataFile(String filename, Map<String, Integer> descriptor)
	{
		this.filename = filename;
		this.descriptor = descriptor;
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
		
			index = new Index(indexName, column, filename);
			totalrecords = recordMap.size();
			while(totalrecords>0)
			{
				temporaryrecord = recordMap.get(totalrecords);
				recordvalue = temporaryrecord.get(column);
				if(recordvalue==null || recordvalue=="")
					throw new IllegalArgumentException();
				else{
				totalrecords--;
				index.insertIntoBPTree(recordvalue, totalrecords);
				}
			}
			
			indexMap.put(indexName, index);
			columnindexMap.put(column,indexName);
		}
		
		return index;
		
	}
	
	
	public Map<String, String> getRecord(int recordid)
	{
		Map<String, String> record = new TreeMap<String, String>();
		
		if(recordnum < recordid)
			return null;
		
		record = recordMap.get(recordid);
		
		return record;
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

		recordnum++;
		recordid = recordnum;
		
		recordset = record.keySet();
		recorditerator = recordset.iterator();
		while(recorditerator.hasNext())
		{
			recordkey = recorditerator.next();
			recordvalue = record.get(recordkey);
		}
		
		indexsize = indexMap.size();
		while(indexsize>0)
		{
		indexname = columnindexMap.get(recordkey);
		index = indexMap.get(indexname);
		index.insertIntoBPTree(recordvalue, recordid);
		indexMap.put(indexname, index);
		}
		
		recordMap.put(recordnum, record);
		
		return recordid;
	}
	
	
	public void dumpFile()
	{
		ObjectOutputStream outputFileObj = null;
		try {
			outputFileObj = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filename)));
			outputFileObj.writeObject(descriptor);
			outputFileObj.writeObject(indexMap);
			outputFileObj.writeObject(recordMap);
			outputFileObj.writeObject(columnindexMap);
			outputFileObj.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	public String viewFile()
	{
		String filecontent = "";
		int descriptorsize = 0;
		int recordno = 0;
		int recordnumber = 0;
		int descsize = 0;
		String recordvalue = "";
		Set<String> descriptorset = new HashSet<String>();
		Iterator<String> descriptoriterator = null;
		ArrayList<String> attributes = new ArrayList<String>();
		Map<String, String> record = new TreeMap<String, String>();
		
		descriptorsize = descriptor.size();
		descriptorset = descriptor.keySet();
		descriptoriterator = descriptorset.iterator();
		while(descriptoriterator.hasNext())
		{
			attributes.add(descriptoriterator.next());
		}
		
		recordno = recordMap.size();
		while(recordno>recordnumber)
		{
			record = recordMap.get(recordnumber);
			descsize = 0;
			while(descsize<descriptorsize)
			{
				recordvalue = record.get(attributes.get(descsize));
				filecontent = filecontent + recordvalue +"::"+attributes.get(descsize);
				descsize++;
			}
			filecontent  = filecontent + "\n";
			recordnumber++;
		}
		
		System.out.println("File content is: "+ filecontent);
		return filecontent;
		
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
			record = recordMap.get(indcnt);
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
		recordMap = null;
		
		file = new File(filename);
		if(file.exists())
			file.delete();
		
		System.out.println("Data file dropped\n");
	}
	
	
	public Index restoreIndex(String indexName) throws ClassNotFoundException
	{
		File indexfile = null;
		String file_name = filename+"."+indexName;
		ObjectInputStream inputFileObj = null;
		Index index = null;
		
		if(indexMap!=null && indexMap.containsKey(indexName))
			throw new IllegalArgumentException();
		
		indexfile = new File(file_name);
		if(!indexfile.exists())
			throw new IllegalArgumentException();
		
		try {
			inputFileObj = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file_name)));
			index.fileName = file_name;
			index.indexName = indexName;
			index.column = (String)inputFileObj.readObject();
			index.bpTree = (BPTree)inputFileObj.readObject();
			inputFileObj.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void dropIndex(String indexName)
	{
		indexMap.remove(indexName);
		columnindexMap.remove(indexName);
		
		System.out.println("Dropped Index\n");
	}
	
	public Iterator<Integer> iterator()
	{
		FileIterator fileiterator = new FileIterator(recordMap.size());
		return fileiterator;
	}
	
	private class FileIterator implements Iterator<Integer>
	{

		int cursor = 0;
		int recordremaining = -1;
		public FileIterator(int size) {
			cursor=size;
			// TODO Auto-generated constructor stub
		}

		@Override
		public boolean hasNext() {
			// TODO Auto-generated method stub
			recordremaining++;
			return recordremaining!=cursor;
		}

		@Override
		public Integer next() {
			// TODO Auto-generated method stub
			return recordremaining;
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub
			recordMap.remove(recordremaining);
			recordremaining--;
			cursor--;
		}
		
	}
}
