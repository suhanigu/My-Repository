package reference;

import java.util.ArrayList;

public class BNode {

	boolean isLeaf; // is the node a leaf
	boolean isRoot; // is the node the root
//	boolean isDeleted; // Has the Node been marked Deleted
	int order; // The order of the Node
	int numKeys = 0; // number of keys stored in node
	// KeyNode kArray[]; // array where keys are stored
	// BTNode btnArray[]; // array where references to the next BTNodes is stored
	
	
	public BNode parent, right, left;

	public BNode() {
		order = 4;
		parent = null;
		right = null;
		left = null;
		isLeaf = true;
	}

	public ArrayList<Data> data = new ArrayList<Data>();
	public ArrayList<Comparable> keyvalues = new ArrayList<Comparable>();
	public ArrayList<String> delflaglist = new ArrayList<String>();
	public ArrayList<BNode> children = new ArrayList<BNode>();
	
	//public boolean leaf;
	// Comparable [] keyvalues= new Comparable[9];
		// BNode [] children=new BNode[10];
	
	public int compareTo(Object o) {
		BNode n = (BNode) o;
		return keyvalues.get(0).compareTo(n.keyvalues.get(0));

	}

}
