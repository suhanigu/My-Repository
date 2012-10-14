package database;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import database.DataFile;
import database.bplusTree.*;

/**
 * @author Amitabh Sural
 * @author Suhani Gupta
 * @author Syed Jibran Uddin
 * 
 */

public class Index {
	BpTree temptree = new BpTree();
	BpTree newBpTree = new BpTree();
	String indexName;
	public String column;
	String filenm;
	String tempPrint = "";
	static boolean flag1 = true;

	public Index(String fn, String indnm, String col) {
		filenm = fn;
		indexName = indnm;
		column = col;
	}

	public Index() {

	}

	// extra credit function

	public void recreateBPTree() {
		List<String> hm = new ArrayList<String>();
		hm = temptree.createnewTree();
		for (int i = 0; i < hm.size(); i++) {
			// Map.Entry entry = (Map.Entry) iter.next();
			String[] temp = { null, null };
			int k = 0;
			StringTokenizer tokens = new StringTokenizer(hm.get(i), "$");
			while (tokens.hasMoreTokens()) {
				String token = tokens.nextToken();
				temp[k++] = token;
			}
			// System.out.println("Key: " + temp[0]);
			String r_value = temp[0];
			// System.out.println("Record ID:" + temp[1]);
			Integer r_id = Integer.parseInt(temp[1]);
			newBpTree.insert(r_value, r_id);
		}
		temptree = newBpTree;
		newBpTree = null;
	}

	public void dumpIndex() {

		String index_file;
		boolean a = filenm.equals("");
		boolean b = indexName.equals("");
		FileOutputStream fops = null;
		ObjectOutputStream oos = null;

		if ((filenm != null) && (indexName != null) && (a == false)
				&& (b == false)) {

			index_file = filenm + "." + indexName;
			try {
				fops = new FileOutputStream(index_file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			BufferedOutputStream bos = new BufferedOutputStream(fops);

			try {
				oos = new ObjectOutputStream(bos);

			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				oos.writeObject(column);
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				oos.writeObject(temptree);
				bos.close();
				fops.close();
				oos.close();

			} catch (IOException e) {
				e.printStackTrace();

			}
		}
	}// End of dumpIndex Function

	public String viewIndex() {

		// System.out.println("Inside view index");
		temptree.myPrint = new StringBuffer();

		temptree.myPrint.append("Index Name = \t " + indexName
				+ " \t and the Column over which index is created is  "
				+ column + "\n");
		temptree.myPrint.append("Index Name = \t " + indexName
				+ " \t and the Column over which index is created is  "
				+ column + "\n");

		tempPrint = temptree.bfsTraversal(temptree.root, 0);

		return tempPrint;

	}// end of viewIndex function

	public Iterator<Integer> iterator(String key) {

		IndexIterator index_it = new IndexIterator(key);
		return index_it;
	}// End of iterator function

	private class IndexIterator implements Iterator<Integer> {

		int cur_pos, total_records;
		boolean flag2;
		ArrayList<Integer> lengthIndex = new ArrayList<Integer>();
		BNode bnode;
		boolean flagnext;
		Comparable key1;
		BNode tempnode;
		int m;
		int count = 0;
		int index = 0;
		int tot = 0, index_start = 0, j = 0;

		IndexIterator(Comparable key) {

			cur_pos = 0;
			key1 = key;
			bnode = temptree.search(key1);

			// --------------------------------------------
			DataManager dm = new DataManager();
			int i = 0;
			while (dm.dataFileList.get(i) != null) {
				if (dm.dataFileList.get(i).fileName == filenm)
					break;
				i++;
			}
			DataFile df = dm.dataFileList.get(i);
			// --------------------------------------------

			while ((bnode.left != null)
					&& (bnode.left.keyvalues
							.get(bnode.left.keyvalues.size() - 1) == key1)) {
				bnode = bnode.left;
			}

			while (j < bnode.keyvalues.size()) {
				if (key1.compareTo(bnode.keyvalues.get(j)) < 0) {
					break;
				}
				// -------------------------------------------------------
				if (key1.compareTo(bnode.keyvalues.get(j)) == 0
						&& !bnode.data.get(j).isDeleted()) {
					if (tot == 0) {
						index_start = j;
						tempnode = bnode;
					}
					tot = tot + 1;
				}
				if (bnode.keyvalues.size() == (j + 1)) {
					if (bnode.right != null) {
						j = -1;
						bnode = bnode.right;
					}
				}
				j++;
			} // end of while loop

			index = index_start;
			total_records = tot;
			flag2 = true;
			flag1 = true;
			flagnext = true;
			cur_pos = index;
			count = 0;
			bnode = tempnode;
			m = cur_pos;
		}

		@Override
		public boolean hasNext() {
			// -------------------------------------------
			DataManager dm = new DataManager();
			int i = 0;
			while (dm.dataFileList.get(i) != null) {
				if (dm.dataFileList.get(i).fileName == filenm)
					break;
				i++;
			}
			DataFile df = new DataFile();
			df = dm.dataFileList.get(i);
			// --------------------------------------------

			if (total_records > 0) {
				while (cur_pos < bnode.keyvalues.size()
						&& (df.records.get(bnode.data.get(cur_pos).getValue())
								.get("delflag") == "true" || bnode.data.get(
								cur_pos).isDeleted()) && count < total_records) {
					count++;
					if (m < bnode.keyvalues.size()) {
						m++;
						cur_pos++;
					} else {
						m = 0;
						cur_pos = 0;
						bnode = bnode.right;
					}
				}
				if (count < total_records) {
					count++;
					return true;
				} else
					return false;
			} else
				return false;
		}

		@Override
		public Integer next() {

			Integer ret = 0;

			if (flagnext == false)
				flag1 = flag2;

			if (flag1 != flag2) {
				// System.out.println("Concurrent Modification Exception");
				throw new ConcurrentModificationException();
			}

			if (m < bnode.keyvalues.size()) {
				m++;
				ret = bnode.data.get(cur_pos++).getValue();
				return ret;
			} else {
				m = 1;
				bnode = bnode.right;
				cur_pos = 0;
				ret = bnode.data.get(cur_pos++).getValue();
				return ret;
			}
		}// End of next function

		@Override
		public void remove() {

			Integer ret;

			if (flag1 != flag2) {
				// System.out.println("Concurrent Modification Exception");
				throw new ConcurrentModificationException();
			}
			flag1 = false;
			flag2 = false;
			flagnext = false;
			// -------------------------------------------------------
			DataManager dm = new DataManager();
			int i = 0;
			while (dm.dataFileList.get(i) != null) {
				if (dm.dataFileList.get(i).fileName == filenm)
					break;
				i++;
			}
			DataFile df = new DataFile();
			df = dm.dataFileList.get(i);

			df.records.get(bnode.data.get(cur_pos - 1).getValue()).put(
					"delflag", "true");
			bnode.data.get(cur_pos - 1).setDeleted(true);
			// ---------------------------------------------------------

		}

	}// End of Class IndexIterator

	public void insertTree(String key, int recordid) {
		temptree.insert(key, recordid);// insert function defined in BPTree

	}

	public void delete(String recordvalue, int recordid) {
		temptree.delete_one(recordvalue, recordid);
	}
	
	public List<Integer> print() {
		
		List<Integer> list = new ArrayList<Integer>();
		list = temptree.printleaves();
		return list;
	}

	public int fetchid_asc(int count) {
		// TODO Auto-generated method stub
		int id = temptree.traverse_asc(count);
		return id;
	}
	
	public int fetchid_desc(int count) {
		// TODO Auto-generated method stub
		int id = temptree.traverse_desc(count);
		return id;
	}

}