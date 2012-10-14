import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Queue;


public class BSTtoLL {

	static class Node{
		Node left;
		Node right;
		Node next;
		int data;
		
		public Node(int value){
			data = value;
			left=null;
			right=null;
			next = null;
		}
		public Node(){
			left=null;
			right=null;
			next = null;
		}
	}
	
	static class ListNode{
		ListNode next;
		int value;
		
		public ListNode(int v){
			value = v;
			next=null;
		}
		public ListNode(){
			next=null;
		}
	}

	protected static Node root_one;
	protected static Node root_two;
	protected static ListNode head;
	static LinkedList<Node> bstlist = new LinkedList<Node>();
	static ArrayList<Node> path = new ArrayList<Node>();
	static ArrayList<Integer> allpath = new ArrayList<Integer>();
	static Stack stack = new Stack();
	
	static int pathlen = 0;
	static int sum = 0;
	
	public static void insert(Node node, int value){
		
		if(node == null){
			Node newnode = new Node(value);
			node = newnode;
		}
		else if(value < node.data){
			if(node.left!=null)
				insert(node.left, value);
			else{
				Node newnode = new Node(value);
				node.left = newnode;
			}
		}
		else{
			if(node.right!=null){
				insert(node.right, value);
			}
			else{
				Node newnode = new Node(value);
				node.right = newnode;
			}
		}
	}
	
	public static void createbst_one(){
		root_one = new Node(8);
		insert(root_one, 5);
		insert(root_one, 10);
		insert(root_one, 3);
		insert(root_one, 4);
		insert(root_one, 12);
		insert(root_one, 1);
		insert(root_one, 11);
		insert(root_one, 7);
	}
	public static void createbst_two(){
		root_two = new Node(10);
		insert(root_two, 12);
		insert(root_two, 11);	
	}
	
	public static void createlist(){
		head = new ListNode(1);
		int i=2;
		while(i<=10){
			insert_list(head,i);
			i++;
		}
	}
	
	public static void insert_list(ListNode h,int v){
		ListNode temp = h;
		if(h==null){
			h = new ListNode(v);
		}
		else{
			while(temp.next!=null)
				temp = temp.next;
			ListNode node = new ListNode(v);
			temp.next = node;
		}
	}
	
	public static void printlist(ListNode h){
		ListNode temp = h;
		while(temp!=null){
			System.out.println(temp.value);
			temp = temp.next;
		}
	}
	
	public static void printbst(Node root){
		if(root!=null){
		printbst(root.left);
		System.out.println(root.data);
		printbst(root.right);
		}
	}
	
	public static LinkedList<Node> bsttoll(Node node){
		
		LinkedList<Node> leftlist = new LinkedList<Node>();
		LinkedList<Node> rightlist = new LinkedList<Node>();
		LinkedList<Node> ll = new LinkedList<Node>();
		LinkedList<Node> rl = new LinkedList<Node>();
		LinkedList<Node> singlenodelist = new LinkedList<Node>();
		
		if(node==null){
			return null;
		}
		

		leftlist = bsttoll(node.left);
		if(leftlist!=null)
			ll.addAll(leftlist);
		
		rightlist = bsttoll(node.right);
		if(rightlist!=null)
			rl.addAll(rightlist);
		
		bstlist.clear();
		
		if(leftlist==null && rightlist==null){
			singlenodelist.add(node);
			return singlenodelist;
		}
		else{
			if(ll!=null) {
				bstlist.addAll(ll);
			}
				bstlist.add(node);
			if(rl!=null){
				bstlist.addAll(rl);
			}
			}

		return(bstlist);
	}
	
	public static void printlist(LinkedList<Node> list){
		Iterator<Node> li = list.iterator();
		while(li.hasNext()){
			Node node = li.next();
			System.out.printf("%d, ", node.data);
		}
		System.out.printf("\n");
	}
	
	
	public static Node inplacell(Node node){
		Node ll;
		Node rl;
		
		if(node==null)
			return null;
		
		ll = inplacell(node.left);
		rl = inplacell(node.right);
		
		node.left = node;
		node.right = node;
		
		ll = append(ll,node);
		ll = append(ll,rl);
		
		return ll;
		
	}
	
	
	public static Node append(Node n1, Node n2){
		if(n1==null)
			return n2;
		if(n2==null)
			return n1;
		
		Node last1 = n1.left;
		Node last2 = n2.left;
		
		last1.right = n2;
		n2.left = last1;
		
		last2.right = n1;
		n1.left = last2;
		
		return n1;
	}
	
	 public static void printinplace(Node head) {
	        Node current = head;
	        
	        while (current != null) {
	            System.out.print(Integer.toString(current.data) + " ");
	            current = current.right;
	            if (current == head) break;
	        }
	        
	        System.out.println();
	    }
	
	 public static void levelorder(Node node){
		 Queue<Node> bstq = new LinkedList<Node>();
		 
		 bstq.add(node);
		 while(!bstq.isEmpty()){
			 Node newnode = bstq.poll();
			 System.out.println(newnode.data);
			 if(newnode.left!=null)
				 bstq.add(newnode.left);
			 if(newnode.right!=null)
				 bstq.add(newnode.right);
		 }
	 }
	 
	 public static Node[] ksumpath(Node node, int k, ArrayList<Node> path){
		 if(node==null && k!=0)
			 return null;
		 if(k == 0){
			 printksumpath(path);
		 }
		 else if(k >= node.data){
			 path.add(node);
			 k = k - node.data;
			 ksumpath(node.left, k, (ArrayList<Node>)path.clone());
			 ksumpath(node.right, k, (ArrayList<Node>)path.clone());
		 }
		 else if(k < node.data){
			 Node n = path.remove(0);
			 k = k + n.data;
			 ksumpath(node, k, (ArrayList<Node>)path);
		 }
		return null;
	 }
	 
	 public static void printksumpath(ArrayList<Node> path){
		 int i = 0;
		 while(i < path.size()){
			 System.out.printf("%d", path.get(i).data);
			 i++;
		 }
		 System.out.printf("\n");
	 }
	 
	 public static int printallpaths(Node node, ArrayList<Integer> path){
		 
		 if(node==null)
			 return 0;
			 path.add(node.data);
			 if(node.left==null && node.right==null){
				 printpath(path);
			 }
			 else{
				 printallpaths(node.left, (ArrayList<Integer>) path.clone());
				 printallpaths(node.right, (ArrayList<Integer>) path.clone());
			 }
		
		 return 0;
	 }
	 
	 public static void printpath(ArrayList<Integer> disppath){
		 int i = 0;
		 while(i < disppath.size()){
			 System.out.printf("%d", disppath.get(i));
			 i++;
		 }
		 System.out.printf("\n");
	 }
	 
	 public static void lca(Node node, int a, int b){
		 
		 if(node.left==null && node.right==null){
			 System.out.println("no common ancestor");
			 System.exit(0);
		 }
		 
		 while(node!=null){
			 if(a<node.data && b>node.data){
				 System.out.println("Common ancestor of: " + a + "and" + b + "is: " + node.data);
				 System.exit(0);
			 }
			 else if(a<node.data && b<node.data){
				 if(node.left!=null)
					 lca(node.left, a, b);
				 else{
					 System.out.println("no common ancestor");
					 System.exit(0);
				 }
			 }
			 else{
				if(node.right!=null)
					lca(node.right, a, b);
				else{
					System.out.println("no common ancestor");
					System.exit(0);
				}
			 }
		 }
	 }
	 
	 public static void inorder_iterative(Node node){
		 boolean flag = true;
		 
		 while(flag==true){
			if(node!=null){
				stack.push(node);
					node = node.left;
			}
		 
			else{
				if(stack.top!=0){
					node = (Node) stack.pop();
					System.out.println(node.data);
						node = node.right;
				}
				else
					flag=false;
		 	}
		 
		 }
	 }
	 
	public static boolean subtree(Node n1, Node n2){
		if(n2==null)
			return true;
		if(n1==null)
			return false;
		if(n1.data==n2.data){
			if(subtree(n1.left,n2.left) && subtree(n1.right,n2.right))
				return true;
		}
		else
			return(subtree(n1.left,n2) || subtree(n1.right,n2));
		return false;
	}
	 
	public static void vertical_sum(Node node,int index, int[] vsum){
		if(node==null)
			return;
		vsum[index] = vsum[index] + node.data;
		vertical_sum(node.left,index-1,vsum);
		vertical_sum(node.right,index+1,vsum);
	}
	 
	public static void print_vsum(int[] vsum){
		int i=vsum.length-1;
		while(i>=0){
			System.out.println(vsum[i]);
			i--;
		}
	}
	
	public static void delete_node(Node node, int n){
		if(node==null)
			return;
		
		if(node.data == n){
			if(node.left!=null && node.right!=null){
				Node succ = getsuccessor_rightmost(node.left);
				int d = succ.data;
				delete_node(succ, succ.data);
				node.data = d;
			}
			else if(node.left==null && node.right==null){
				Node parent = findParent(root_one, node);
				if(parent.left.data == node.data)
					parent.left = null;
				else
					parent.right = null;
				node = null;
			}
			else if(node.left==null){
				Node succ = getsuccessor_leftmost(node.right);
				int d = succ.data;
				delete_node(succ, succ.data);
				node.data = d;
			}
			else if(node.right==null){
				Node succ = getsuccessor_rightmost(node.left);
				int d = succ.data;
				delete_node(succ, succ.data);
				node.data = d;
			}
			
		}
		else if(node.data > n){
			node = node.left;
			delete_node(node, n);
		}
		else{
			node = node.right;
			delete_node(node, n);
		}
		
	}
	
	public static Node getsuccessor_rightmost(Node node){
		Node succ = node;
		
		while(succ.right!=null){
			succ = succ.right;
		}
		return succ;
	}
	
	public static Node getsuccessor_leftmost(Node node){
		Node succ = node;
		
		while(succ.left!=null){
			succ = succ.left;
		}
		return succ;
	}
	
	public static Node findParent(Node r,Node node){
		Node parent = r;
		if(parent==null)
			return parent;
		while(parent!=null){
		if(parent.data > node.data){
			if(parent.left!=null)
				if(parent.left.data == node.data)
					return parent;
				else
					parent = parent.left;
		}
		if(parent.data < node.data){
			if(parent.right!=null)
				if(parent.right.data == node.data)
					return parent;
				else
					parent = parent.right;
		}
		}
		return parent;
	}
	
	public static Node sortedlist_to_balancedtree(ListNode list, int start, int end){
		if(start>end)
			return null;
		
		int mid = (start + end)/2;
		Node left = sortedlist_to_balancedtree(head, start, mid-1);
		Node parent = new Node(head.value);
		parent.left = left;
		head = head.next;
		parent.right = sortedlist_to_balancedtree(head, mid+1, end);
		return parent;
	}
	
	static Node prev = null;
	public static Node inorder_succ(Node node){
		if(node == null)
			return null;
		inorder_succ(node.left);
		if(prev!=null)
			prev.next = node;
		prev = node;
		inorder_succ(node.right);
		return null;
	}
	
	public static void print_inorder_succ(Node node){
		while(node.left!=null)
			node = node.left;
		while(node.next!=null){
			System.out.printf("%d ", node.data);
			node = node.next;
		}
	}
	
	public static void main(String[] args){
		createbst_one();
//		createbst_two();
		createlist();
//		inorder_succ(root_one);
//		print_inorder_succ(root_one);
//		printlist(head);
//		Node node = sortedlist_to_balancedtree(head, 1, 10);
//		printbst(node);
//		delete_node(root_one, 5);
//		printbst(root_one);
//		int[] vsum = new int[10];
//		vertical_sum(root_one,4,vsum);
//		print_vsum(vsum);
//		boolean res = subtree(root_one,root_two);
//		System.out.println(res);
//		inorder_iterative(root);
//		lca(root, 3, 6);
//		System.out.println(root.data);
//		printbst(root);
//		bsttoll(root);
//		printlist(bstlist);
		printallpaths(root_one, allpath);
//		Node head = inplacell(root);
//		printinplace(head);
//		levelorder(root);
//		sum = 12;
//		ksumpath(root_one, 12, path);
	}
	
	
}
