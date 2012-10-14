
public class LinkedListClass {

	public static class Node{
		int value;
		Node next;
		Node prev;
		
		public Node(){
			next = null;
			prev = null;
		}
		public Node(int v){
			value = v;
			next = null;
			prev = null;
		}
	}
	
	static Node head = new Node(1);
	
	public static void createll(){
		int i = 2;
		Node cur = head;
		while(i<9){
			Node node = new Node(i);
			if(head==null){
				head = node;
			}
			else{
				cur.next = node;
				cur = cur.next;
			}
			i++;
		}
//		while(i>0){
//			Node node = new Node(i);
//			if(head==null){
//				head = node;
//			}
//			else{
//				cur.next = node;
//				cur = cur.next;
//			}
//			i--;
//		}
	}
	
	public static void reversesinglyll(){
		Node prev = null;
		Node cur = head;
		Node fwd = null;
		
		while(cur!=null){
			fwd = cur.next;
			cur.next = prev;
			prev = cur;
			cur = fwd;
		}
		head = prev;
	}
	
	public static Node reverse(Node h){
		Node prev = null;
		Node cur = h;
		Node fwd = null;
		
		while(cur!=null){
			fwd = cur.next;
			cur.next = prev;
			prev = cur;
			cur = fwd;
		}
		//Node n = prev;
		return prev;
	}
	
	public static void reverse_recursively(Node n1){
		if(n1.next!=null){
			reverse_recursively(n1.next);
			n1.next.next = n1;
			n1.next = null;
		}
		else{
			head = n1;
		}
	}
	
	public static void kreverse(Node n, int k){
		int i = 0;
		Node start = null;
		Node end = null;
		Node prev = null;
		Node next = n;
		int temp=0;
		while(next!=null){
			start=next;
			end=next;
			i=1;
			temp++;
			while(i<k){
				if(end==null)
					break;
				end = end.next;
				i++;
			}
		
			if(end!=null){
				next = end.next;
				end.next = null;
			}
			
			Node node = null;
			if(temp==1){
				head = reverse(start);
				node = head;
			}
			else{
				node = reverse(start);
				prev.next = node;
			}
			
			while(node.next!=null)
				node = node.next;
			prev = node;
			node.next = next;
			}
	}
	
	public static void printll(){
		Node n = head;
		while(n!=null){
			System.out.printf("%d ", n.value);
			n = n.next;
		}
		System.out.printf("\n");
	}
	
	public static Node isPalindrome(Node n1, Node n2){
		Node n;
		if(n2==null)
			return n1;

			n1 = isPalindrome(n1,n2.next);
			if(n1!=null){
				if(n1.value==n2.value){
					if(n1.next!=null)
						return n1.next;
					else
						return n1;
				}
			}
		return null;
	}
	
	public static void main(String[] args){
		createll();
		printll();
		kreverse(head, 2);
		printll();
//		reverse_recursively(head);
//		printll();
//		Node result = isPalindrome(head,head);
//		if(result!=null)
//			System.out.println(true);
//		else
//			System.out.println(false);
//		printll();
//		reversesinglyll();
//		printll();
//		reversedoublyll();
	}
}
