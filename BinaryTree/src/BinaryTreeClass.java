import java.util.ArrayList;

public class BinaryTreeClass {

	static class Node{
		int data;
		Node left;
		Node right;
		
		Node(int n){
			data = n;
			left = null;
			right = null;
		}
	}

	private static final int INT_MAX = 1000;

	private static final int INT_MIN = 0;
	
	static Node root_one;
	static Node root_two;
	
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
	
	public static void create_BT(){
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
	public static void create_BT_two(){
		root_two = new Node(10);
		insert(root_two, 12);
		insert(root_two, 11);	
	}
	public static Node lca_bt(Node node, int p, int q){
		if(node==null)
			return null;
		if(p==node.data || q==node.data)
			return node;
		Node L = lca_bt(node.left, p, q);
		Node R = lca_bt(node.right, p, q);
		if(L!=null && R!=null)
			return node;
		if(L!=null)
			return L;
		else
			return R;
	}
	
	public static boolean areIdentical(Node n1, Node n2){
		boolean l = false;
		boolean r = false;
		if(n2==null && n1==null)
			return true;
		if((n1==null && n2!=null) || (n1!=null & n2==null))
			return false;
		if(n1.data==n2.data){
			l = areIdentical(n1.left,n2.left);
			r = areIdentical(n1.right,n2.right);
		}
		if(l && r)
			return true;
		else
			return false;
	}
	
	public static boolean subtree(Node n1, Node n2){
		boolean l = false;
		boolean r = false;
		if(n2==null && n1==null)
			return true;
		if((n1==null && n2!=null) || (n1!=null & n2==null))
			return false;
		if(areIdentical(n1,n2))
			return true;
		else{
			l = subtree(n1.left,n2);
			r = subtree(n1.right, n2);
		}
		if(l || r)
			return true;
		else
			return false;
	}
	
	public static boolean isbst = false;
	public static int largest_bst(Node node, int min, int max, int size){
		boolean left_flag = false;
		boolean right_flag = false;
		
		if(node==null){
			size = 0;
			isbst = true;
			return size;
		}
		int l,r,s = 0;
		int minimum = INT_MAX;
		
		max = INT_MIN;
		l = largest_bst(node.left, min, max, size);
		if(isbst == true && node.data > max)
			left_flag = true;
		
		minimum = min;
		
		min = INT_MAX;
		r = largest_bst(node.right, min, max, size);
		if(isbst == true && node.data < min)
			right_flag = true;
		
		if(minimum < min)
			min = minimum;
		if(node.data < min)
			min = node.data;
		if(node.data > max)
			max = node.data;
		
		
		if(left_flag==true && right_flag==true){
			if(size < l+r+1)
				size = l+r+1;
			return l+r+1;
		}
		else{
			isbst = false;
			return 0;
		}
	}
	
	public static void printbst(ArrayList<Node> bst){
		int i = 0;
		while(i<bst.size()){
			System.out.printf("%d",bst.get(i).data);
			i++;
		}
		System.out.println("\n");
	}
	public static Node nth_smallest(Node node){
		
		return null;
	}
	
	public static void main(String[] args){
		create_BT();
		create_BT_two();
		int size = largest_bst(root_two, INT_MAX, INT_MIN, 0);
		System.out.println("size of largest bst: " + size);
//		boolean result = subtree(root_one, root_two);
//		System.out.println("result: " + result);
//		nth_smallest(root_one);
//		Node n = lca_bt(root_one, 12, 10);
//		System.out.println(n.data);
	}
}
