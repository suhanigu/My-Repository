package reference;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author amitabh
 * 
 */
public class BpTree implements Serializable {

	private static final long serialVersionUID = -8319581044111246013L;
	/**
	 * @param args
	 */

	public BNode root, start;
	int curval;
	int nextval;
	boolean linkleavesflag;
	int spaces = 0;

	private static final int T = 4;
	public static StringBuffer indexPrint = new StringBuffer();

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
//					else if (x.compareTo(curnode.keyvalues.get(k + 1)) == 0) {
//						curnode = curnode.children.get(k+2);
//						break;
//					} 
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
		
		if (root == null) {
			
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
		linkleavesflag = true;
		linkleaves();
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

	public void linkleaves() {
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
				if (linkleavesflag) {
					start = z;
					linkleavesflag = false;
				}
				if (!queue.isEmpty())
					z.right = queue.element();
				left = z;
			}
		}
	}

	public void printsorted() {

		BNode temp = start;
		System.out.println("Traversing the Doubly Linked List as follows ->\n");
		do {
			for (int i = 0; i < temp.keyvalues.size(); i++) 
			{
				if(!(temp.data.get(i).isDeleted()))
				System.out.print(temp.keyvalues.get(i) + "--");
			}
			temp = temp.right;
		} while (temp != null);
		System.out.println("\n------------------");
	}
	
	public String printinorder(BNode n)
    {
		int count=0;
		BNode temp = root;
		while(!temp.isLeaf)
			temp = temp.children.get(0);
			
		do {
			for (int i = 0; i < temp.keyvalues.size(); i++) 
			{
				if(!(temp.data.get(i).isDeleted())){
					indexPrint.append("\t");
					indexPrint.append(temp.keyvalues.get(i)+" ");
					indexPrint.append(temp.data.get(i).getValue());
					indexPrint.append("\n");
					count++;
				}
			}
			temp = temp.right;
		} while (temp != null);

        return indexPrint.toString();
    }

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

//	public static void main(String[] args) {
//		try {
//			BpTree mytree = new BpTree();
//			int key = 0;
//			String s;
//			char value;
//			System.out.println("Inside main method..\n");
//			System.out.println("Enter Value : ");
//
//			for (int i = 1; i <= 100; i++) {
//				System.out.println("Inserting element: " + i);
//				//Data datavalue = new Data();
//				//datavalue.setData(i);
//				//datavalue.setDeleted(false);
//				mytree.insert(i, i);
//			}
//			for (int j = 10; j < 14; j++) {
//			System.out.println("inserting element: 5 ");
//			mytree.insert(61, j);
//		}
////			for (int j = 40; j <= 45; j++) {
////				System.out.println("Deleting element: 10");
////				mytree.delete(10);
////			}
////			mytree.delete(8);
////			mytree.delete(10);
////		    mytree.delete(78);
//			
//			BufferedReader br = new BufferedReader(new InputStreamReader(
//					System.in));
//			System.out
//					.println("Value entered at console is : " + br.readLine());
//			do {
//				s = br.readLine();
//				value = s.charAt(0);
//				System.out.println("Value entered is : " + value);
//				switch (value) {
//				case 'a':
//					key = Integer.parseInt(br.readLine());
//				//	mytree.insert(key, 0);
//					break;
//
//				case 'd':
//					key = Integer.parseInt(br.readLine());
//					mytree.delete(key);
//					break;
//
//				case 's':
//					System.out.println("Inside case Printsorted");
//					mytree.printsorted();
//					break;
//				/*
//				 * case 'i': mytree.printinorder(mytree.root,0); break;
//				 */
//				default:
//					continue;
//				}
//
//				// mytree.insert(key, i);
//				mytree.curval = 1;
//				mytree.nextval = 0;
//				System.out.println("Tree after operation");
//				// mytree.print(mytree.root);
//			} while (value != 'e');
//			// mytree.print(mytree.root);
//			// System.out.println("\nEnd of Insertion\n");
//			// mytree.delete(99);
//			// mytree.delete(20);
//			// mytree.curval=1;
//			// mytree.nextval=0;
//			// System.out.println("Tree after deletion");
//			// mytree.print(mytree.root);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		// mytree.delete(20);
//		// mytree.curval=1;
//		// mytree.nextval=0;
//		// System.out.println("Tree after deletion");
//		// mytree.print(mytree.root)
//	}
}
