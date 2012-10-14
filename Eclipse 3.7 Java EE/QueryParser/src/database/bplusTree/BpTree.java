package database.bplusTree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @author Amitabh Sural
 * @author Suhani Gupta
 * @author Syed Jibran Uddin
 * 
 */
public class BpTree {

	@SuppressWarnings("unused")
	private static final long serialVersionUID = -8319581044111246013L;

	public BNode root, start;
	//----------------------------------------
	public BNode mynode;
	int nodeindex = 0;
	//----------------------------------------
	int curval;
	int nextval;

	private static final int T = 4;
	public static StringBuffer myPrint = new StringBuffer();

	@SuppressWarnings("unchecked")
	public BNode search(Comparable x) {
		BNode curnode;
		curnode = root;
		while (curnode != null) {
			if (curnode.isLeaf) {
				return curnode;
			}
			
			if (!curnode.isLeaf) {
				for (int k = 0; k < curnode.keyvalues.size(); k++) {
					if (k == (curnode.keyvalues.size() - 1) && x.compareTo(curnode.keyvalues.get(k)) < 0) {
						curnode = curnode.children.get(k);
						break;
					} 
					else if(k == (curnode.keyvalues.size() - 1) && x.compareTo(curnode.keyvalues.get(k)) >= 0)
					{
						curnode = curnode.children.get(k+1);
						break;	
					}
					else if(x.compareTo(curnode.keyvalues.get(k)) < 0)
					{
						curnode = curnode.children.get(k);
						break;	
					}
					else if(x.compareTo(curnode.keyvalues.get(k)) == 0)
					{
						curnode = curnode.children.get(k+1);
						break;	
					}

					else if (x.compareTo(curnode.keyvalues.get(k)) >= 0 && x.compareTo(curnode.keyvalues.get(k + 1)) < 0) {
						curnode = curnode.children.get(k+1);
						break;
					} 
					else
						continue;
				}
			}
		}

		return curnode;

	}

	@SuppressWarnings("unchecked")
	public void insert(Comparable x, int data) {
		BNode inserthere;
		Data dataobj = new Data();
		dataobj.setValue(data);
		dataobj.setDeleted(false);
		
		if (root == null) 
		{
			
			root = new BNode();
			root.parent = null;
			root.keyvalues.add(0, x);
			root.data.add(0, dataobj);

		} else {
			inserthere = search(x);
			int index = 0;
			for (int j = 0; j < inserthere.keyvalues.size(); j++) {
				if (j == (inserthere.keyvalues.size() - 1) && x.compareTo(inserthere.keyvalues.get(j)) < 0) {
					index = j;
				} 
				else if(j == (inserthere.keyvalues.size() - 1) && x.compareTo(inserthere.keyvalues.get(j)) >= 0)
				{
					index = j+1;	
				}
				else if (x.compareTo(inserthere.keyvalues.get(j)) >= 0
						&& x.compareTo(inserthere.keyvalues.get(j + 1)) < 0) {
					index = j + 1;
					break;
				} 
				else if(x.compareTo(inserthere.keyvalues.get(j)) < 0){
					index = j;
					break;
				}
			else
					continue;
			}


			inserthere.keyvalues.add(index, x);
			if (inserthere.isLeaf)
				inserthere.data.add(index, dataobj);

			if (inserthere.keyvalues.size() == (2 * T)) {
				if (inserthere == root) {
					BNode rootNode = new BNode();
					BNode myNode = new BNode();
					myNode = root;
					root = rootNode;
					root.children.add(0, inserthere);
					inserthere.parent = root;
					root.isLeaf = false;
					root.isRoot = true;
					splitNode(root, 0, inserthere);
				} else {
					int pos = inserthere.parent.children.indexOf(inserthere);
					splitNode(inserthere.parent, pos, inserthere);
				}
				while (inserthere.parent.keyvalues.size() == (2 * T)) {
					inserthere = inserthere.parent;
					if (inserthere == root) {
						BNode rootNode = new BNode();
						BNode myNode = new BNode();
						myNode = root;
						root = rootNode;
						root.children.add(0, inserthere);
						inserthere.parent = root;
						root.isLeaf = false;
						root.isRoot = true;
						splitNode(root, 0, inserthere);
					} else {
						int pos = inserthere.parent.children
								.indexOf(inserthere);
						splitNode(inserthere.parent, pos, inserthere);
					}
				}
			}
		}
		linktheleaves();
	}


	void splitNode(BNode parentNode, int i, BNode node) {
		BNode newNode = new BNode();
		newNode.isLeaf = node.isLeaf;
		newNode.parent = parentNode;

		for (int j = 0; j < T; j++) {
			newNode.keyvalues.add(j, node.keyvalues.get(j + T));
			if (newNode.isLeaf)
				newNode.data.add(j, node.data.get(j + T));
		}
		if (!newNode.isLeaf) {
			for (int j = 0; j < T + 1; j++) {
				newNode.children.add(j, node.children.get(j + T));
				newNode.children.get(j).parent = newNode;
			}
			parentNode.children.add(i + 1, newNode);
			parentNode.keyvalues.add(i, node.keyvalues.get(T - 1));

			int j = T + 1;
			while (j > 0) {
				node.children.remove(T);
				j--;
			}

			int k = T + 1;
			while (k > 0) {
				node.keyvalues.remove(T - 1);
				if (node.isLeaf)
					node.data.remove(T - 1);
				k--;
			}
		} else {
			int k = node.keyvalues.size() - T;
			while (k > 0) {
				node.keyvalues.remove(T);
				if (node.isLeaf)
					node.data.remove(T);
				k--;
			}

			parentNode.children.add(i + 1, newNode);
			parentNode.keyvalues.add(i, newNode.keyvalues.get(0));
		}

	}

	public void linktheleaves() {
		BNode n = root;
		BNode left = null;
		Queue<BNode> queue = new LinkedList<BNode>();
		queue.add(n);
		while (!queue.isEmpty()) {
			BNode z = queue.element();
			for (int i = 0; i < z.children.size(); i++)
				queue.add(z.children.get(i));
			queue.remove();
			if (z.isLeaf) {
				z.left = left;
				if (!queue.isEmpty())
					z.right = queue.element();
				left = z;
			}
		}
	}
	
	
	public String bfsTraversal(BNode n,int tabs)
    { 	
		if(!n.isLeaf)
    {
        for(int k=0;k<n.children.size();k++)
        {
            bfsTraversal(n.children.get(k), tabs+1);
            if(k<n.keyvalues.size())
            {
                for(int j=0;j<tabs;j++)
                {
                	myPrint.append("\t");
                }
                myPrint.append(n.keyvalues.get(k)+"\n");

            }
        }
    }
    else
    {
        for(int i=0;i<n.keyvalues.size();i++)
        {
            for(int j=0;j<tabs;j++)
            {
            	 myPrint.append("\t");
            }
            myPrint.append(n.keyvalues.get(i)+" ");
            if(n.isLeaf)
            {
            	myPrint.append(n.data.get(i).getValue());
            }
            myPrint.append("\n");
        }
    }
    return myPrint.toString();}


	public void delete_one(Comparable x, int id) {
		BNode node;
		node = search(x);
		int flag = 0;
		
		while ((node.left != null)
				&& (node.left.keyvalues.get(node.left.keyvalues.size() - 1) == x)) {
			node = node.left;
		}
		
		while(node!=null){
		for (int i = 0; i < node.keyvalues.size(); i++) {
			if (x.compareTo(node.keyvalues.get(i)) == 0){
				if(node.data.get(i).getValue()==id) {
				flag = 1;
			    node.data.get(i).setDeleted(true);
			    node = null;
			    break;
			}
		}
		}
		if(node!=null)
		node = node.right;
		}
		
		if (flag == 0)
			System.out.println("\tKey Not Found");

	}
	
	public void delete_two(Comparable x) {
		BNode node;
		node = search(x);
		int flag = 0;
		
		while ((node.left != null)
				&& (node.left.keyvalues.get(node.left.keyvalues.size() - 1) == x)) {
			node = node.left;
		}
		
		while(node!=null){
		for (int i = 0; i < node.keyvalues.size(); i++) {
			if (x.compareTo(node.keyvalues.get(i)) == 0) {
				flag = 1;
			    node.data.get(i).setDeleted(true);
			}
			else
				break;
		}
		node = node.right;
		
		}
		if (flag == 0)
			System.out.println("\tKey Not Found");
		

	}
	
	public List<String> createnewTree(){
		BNode n = new BNode();
		n = root;
		while (!n.isLeaf)
			n=n.children.get(0);
		BNode temp1 = n;
		List <String> list = new ArrayList <String>();
		do {
			for (int i = 0; i < temp1.keyvalues.size(); i++) {
				if (!(temp1.data.get(i).isDeleted())) {
					String key = (String) temp1.keyvalues.get(i);
					String value = Integer.toString(temp1.data.get(i).getValue());
					String finaldata = key+"$"+value;
					list.add(finaldata);
	
				}
			}
			temp1 = temp1.right;
		} while (temp1 != null);
		//System.out.println("The size of the undeleted items:" + list.size());
		return list;

	}
	
	
	public  List<Integer> printleaves(){
		BNode n = new BNode();
		n = root;
		while (!n.isLeaf)
			n=n.children.get(0);
		
		
		BNode temp1 = n;
		List<Integer> list = new ArrayList<Integer>();
		do {
			for (int i = 0; i < temp1.keyvalues.size(); i++) {
				if (!(temp1.data.get(i).isDeleted())) {
//					String key = (String) temp1.keyvalues.get(i);
//					String value = Integer.toString(temp1.data.get(i).getValue());
//					String finaldata = key+"$"+value;
					list.add(temp1.data.get(i).getValue());
	
				}
			}
			temp1 = temp1.right;
		} while (temp1 != null);
		return list;
		
	}

	public Integer traverse_asc(int count) {
		
		BNode n = new BNode();
		if(count == 0){
		n = root;
		nodeindex = 0;
		while (!n.isLeaf)
			n=n.children.get(0);
		mynode = n;
		return n.data.get(nodeindex).getValue();
		}
		else{
			n = mynode;
			if(nodeindex < n.data.size()-1){
				nodeindex++;
				return n.data.get(nodeindex).getValue();
			}
			else{
				mynode = mynode.right;
				nodeindex=0;
				return n.data.get(nodeindex).getValue();
			}
		}
	}
	
	
public Integer traverse_desc(int count) {
		
		BNode n = new BNode();
		if(count == 0){
		n = root;
		while (!n.isLeaf){
			int x = n.data.size();
			n=n.children.get(x);
		}
		nodeindex = n.data.size() - 1;
		mynode = n;
		return n.data.get(nodeindex).getValue();
		}
		else{
			n = mynode;
			if(nodeindex > 0){
				nodeindex--;
				return n.data.get(nodeindex).getValue();
			}
			else{
				if(mynode.left==null)
					return 0;
				mynode = mynode.left;
				nodeindex = mynode.data.size();
				return n.data.get(nodeindex).getValue();
			}
		}
	}


}
