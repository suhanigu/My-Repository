package database;

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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import database.bplusTree.*;

/**
 * @author Amitabh Sural
 * @author Suhani Gupta
 * @author Syed Jibran Uddin
 * 
 */

public class DataFile implements Serializable {

	private static final long serialVersionUID = 4298564775253437099L;

	public String fileName;
	public Map<String, Integer> descriptor = new HashMap<String, Integer>();
	public Map<String, Index> indexMap = new TreeMap<String, Index>();
	@SuppressWarnings("rawtypes")
	public Map<Integer, Map<String, String>> records = new HashMap<Integer, Map<String, String>>();
	public Map<String, String> columnindexMap = new TreeMap<String, String>();
	int recordnum = 0;
	int minreckey = 0;

	public DataFile(String filename, Map<String, Integer> descriptor) {
		this.fileName = filename;
		this.descriptor = descriptor;
	}

	public DataFile(String filename) {
		this.fileName = filename;
	}

	public DataFile() {

	}

	public Index createIndex(String indexName, String column) {
		int totalrecords = 0;
		String recordvalue = "";
		Index index = null;
		Map<String, String> temporaryrecord = new TreeMap<String, String>();

		if (indexMap != null && indexMap.containsKey(indexName))
			throw new IllegalArgumentException();
		if (descriptor.containsKey(column)) {
			if (descriptor.get(column) > 24)
				throw new IllegalArgumentException();

			index = new Index(fileName, indexName, column);
			totalrecords = records.size();
			int tr = 1;
			int count = 0;
			while (totalrecords >= tr) {
				temporaryrecord = records.get(tr);
				recordvalue = temporaryrecord.get(column);
				if (recordvalue == null || recordvalue == "")
					throw new IllegalArgumentException();
				else {
					index.insertTree(recordvalue, tr);
				}
				tr++;
			}
			indexMap.put(indexName, index);
			columnindexMap.put(column, indexName);
		}

		return index;

	}

	public Index rebuildIndex(String indexName) {

		if (indexMap != null && !indexMap.containsKey(indexName))
			return null;

		Index index = new Index();
		index = indexMap.get(indexName);
		index.recreateBPTree();
		return index;

	}

	public Map<String, String> getRecord(int recordid) {
		Map<String, String> record = new HashMap<String, String>();
		Map<String, String> record_copy = new HashMap<String, String>();

		if (recordnum < recordid)
			return null;

		if (records.get(recordid).get("delflag") == "true")
			return null;
		record = records.get(recordid);
		record_copy.putAll(record);
		record_copy.remove("delflag");
		return record_copy;
	}

	public int insertRecord(Map<String, String> record) {
		int indexsize = 0;
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

		if (record_copy == null)
			throw new IllegalArgumentException();

		recordnum++;
		recordid = recordnum;

		recordset = record_copy.keySet();
		recorditerator = recordset.iterator();
		while (recorditerator.hasNext()) {
			recordkey = recorditerator.next();
			recordvalue = record_copy.get(recordkey);
			if (recordvalue.length() > 24)
				throw new IllegalArgumentException();
			if (!descriptor.containsKey(recordkey)) {
				throw new IllegalArgumentException();
			}

			indexsize = indexMap.size();
			while (indexsize > 0) {
				indexname = columnindexMap.get(recordkey);
				if (indexname != null) {
					indexsize = 0;
					index = indexMap.get(indexname);
					if (recordvalue != null && !recordvalue.equals("")) {
						index.insertTree(recordvalue, recordid);
					}
					indexsize--;
				} else
					break;
			}

		}
		// deletestatus.put(Integer.toString(recordnum), "false");
		record_copy.put("delflag", "false");
		records.put(recordnum, record_copy);

		// record_del.put(recordnum, record_copy);
		return recordnum;
	}

	public void dumpFile() {
		ObjectOutputStream outputFileObj = null;
		try {
			if (fileName != null) {
				outputFileObj = new ObjectOutputStream(
						new BufferedOutputStream(new FileOutputStream(
								this.fileName)));
				outputFileObj.writeObject(this);
				outputFileObj.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String viewFile() {
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
		int i = 0,j=0;
		List<String> temp = new ArrayList<String>();
		descriptorsize = descriptor.size();
		descriptorset = descriptor.keySet();
		descriptoriterator = descriptorset.iterator();
		while (descriptoriterator.hasNext()) {
			temp.add(descriptoriterator.next());
			//attributes.add(descriptoriterator.next());
			i++;
		}
		for(j=0;j<i;j++){
			attributes.add(temp.get(j));
		}
//		attributes.add(temp.get(0));
//		attributes.add(temp.get(3));
//		attributes.add(temp.get(2));
//		attributes.add(temp.get(1));
		recordno = records.size();
		while (recordno >= recordnumber) {
			filecontent = filecontent + recordnumber + ":" + "\n";
			record = records.get(recordnumber);
			recordlist.put(recordnumber, record);
			descsize = 0;
			while (descsize < descriptorsize) {
				recordvalue = record.get(attributes.get(descsize));
				filecontent = filecontent + "\t" + attributes.get(descsize)
						+ ":" + recordvalue + "\n";
				descsize++;
			}
			recordnumber++;
		}
		return filecontent;

	}

	public void dropFile() {
		int indexcount = 0;
		int indcnt = 0;
		Index index = null;
		String indexname = "";
		String columnname = "";
		String recordkey = "";
		Set<String> recordset = new HashSet<String>();
		Iterator<String> recorditerator = null;
		Map<String, String> record = new TreeMap<String, String>();
		File file = null;

		indexcount = indexMap.size();
		while (indcnt < indexcount) {
			record = records.get(indcnt);
			recordset = record.keySet();
			recorditerator = recordset.iterator();
			while (recorditerator.hasNext()) {
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
		if (file.exists())
			file.delete();
	}

	public Index restoreIndex(String indexName) throws ClassNotFoundException {
		File indexfile = null;
		String file_name = fileName + "." + indexName;
		ObjectInputStream inputFileObj = null;
		Index index = null;

		if (indexMap != null && indexMap.containsKey(indexName))
			throw new IllegalArgumentException();

		indexfile = new File(file_name);
		if (!indexfile.exists())
			throw new IllegalArgumentException();

		try {
			inputFileObj = new ObjectInputStream(new BufferedInputStream(
					new FileInputStream(file_name)));
			index.filenm = file_name;
			index.indexName = indexName;
			index.column = (String) inputFileObj.readObject();
			index.temptree = (BpTree) inputFileObj.readObject();
			inputFileObj.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public void dropIndex(String indexName) {
		indexMap.remove(indexName);
		columnindexMap.remove(indexName);

	}

	public void rebuildfile() {
		int recordsize = records.size();
		String delflag = "delflag";
		int size = 1;
		int i = 0;
		while (size < recordsize) {
			if (records.get(size).get(delflag) == "true") {
				records.remove(size);
				i++;
			}
			size++;
		}
		System.out.println("size: " + i);
		minreckey = i;
	}

	public Iterator<Integer> iterator() {
		//int size = 1;
		int recordsize = records.size();
		FileIterator fileiterator = new FileIterator(recordsize);
		return fileiterator;
	}

	private class FileIterator implements Iterator<Integer> {

		int cursor = 0;
		int recordremaining = minreckey;
		String delflag = "delflag";
		int delete = 1;
		int recordsize = 0;

		public FileIterator(int size) {
			cursor = size;
		}

		@Override
		public boolean hasNext() {
			// System.out.println("recordremaining: " + recordremaining);
			recordremaining++;
			while (delete == 1) {
				if (records.get(recordremaining) != null) {
					if (cursor > 0
							&& records.get(recordremaining).get(delflag) == "false") {
						return true;
					} else if (cursor > 0
							&& records.get(recordremaining).get(delflag) == "true") {
						cursor--;
						recordremaining++;
						delete = 1;
					} else
						return false;
				} else if (records.get(recordremaining) == null && cursor <= 0) {
					return false;
				} else {
					// System.out.println("record deleted: " + recordremaining);
					recordremaining++;
					cursor--;
				}
			}
			return false;
		}

		@Override
		public Integer next() {
			cursor--;
			return recordremaining;

		}

		public Index rebuildIndex(String indexName) {

			if (indexMap != null && !indexMap.containsKey(indexName))
				return null;
			// throw new IllegalArgumentException();
			Index index = new Index();
			index = indexMap.get(indexName);
			index.recreateBPTree();
			return index;

		}

		@Override
		public void remove() {
			// ---------------------------------------------------------
			int indexsize = 0;
			int recordid = 0;
			String recordkey = "";
			String recordvalue = "";
			Index index = null;
			String indexname = "";
			String filename = "";
			Set<String> recordset = new HashSet<String>();
			Iterator<String> recorditerator = null;
			Map<String, String> record_copy = new HashMap<String, String>();

			// while(records.get(recordremaining)==null)
			// {
			// recordremaining++;
			// }

			record_copy.putAll(records.get(recordremaining));
			record_copy.remove("delflag");

			recordid = recordremaining;

			if (record_copy == null)
				throw new IllegalArgumentException();

			recordset = record_copy.keySet();
			recorditerator = recordset.iterator();
			while (recorditerator.hasNext()) {
				recordkey = recorditerator.next();
				recordvalue = record_copy.get(recordkey);
				if (recordvalue.length() > 24)
					throw new IllegalArgumentException();
				if (!descriptor.containsKey(recordkey)) {
					throw new IllegalArgumentException();
				}

				indexsize = indexMap.size();
				while (indexsize > 0) {
					indexname = columnindexMap.get(recordkey);
					if (indexname != null) {
						indexsize = 0;
						index = indexMap.get(indexname);
						if (recordvalue != null && !recordvalue.equals("")) {
							index.delete(recordvalue, recordid);
						}
						indexsize--;
					} else
						break;
				}

			}

			// recordnum--;
			// deletestatus.remove(Integer.toString(recordid));
			// deletestatus.put(Integer.toString(recordid), "true");
			// record_del.get(recordid).remove("delflag");
			records.get(recordid).put("delflag", "true");

			// records.remove(recordid);

		}

	}
}
