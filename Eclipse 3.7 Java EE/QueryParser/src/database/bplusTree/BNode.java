package database.bplusTree;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Amitabh Sural
 * @author Suhani Gupta
 * @author Syed Jibran Uddin
 * 
 */

public class BNode {

	boolean isLeaf; 
	boolean isRoot; 
	
	public BNode parent, right, left;

	public BNode() 
	{
		parent = null;
		right = null;
		left = null;
		isLeaf = true;
	}

	public ArrayList<Data> data = new ArrayList<Data>();
	public ArrayList<Comparable> keyvalues = new ArrayList<Comparable>();
	public ArrayList<String> delflaglist = new ArrayList<String>();
	public ArrayList<BNode> children = new ArrayList<BNode>();
	
	public int compareTo(Object o) {
		BNode n = (BNode) o;
		return keyvalues.get(0).compareTo(n.keyvalues.get(0));

	}

}
