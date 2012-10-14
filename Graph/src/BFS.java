import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;


public class BFS {
	
	public static class Node{
		int data;
		boolean isvisited;
		
		public Node(int n){
			data = n;
			isvisited = false;
		}
	}
	
	static ArrayList<Node>[] adjlist = (ArrayList<Node>[]) new ArrayList[7];
	static Stack stack = new Stack();
	static int[] parent = new int[7];
	
	public static void creategraph(){
		ArrayList<Node> list = new ArrayList<Node>();
		Node n1 = new Node(1);
		Node n2 = new Node(2);
		Node n3 = new Node(3);
		Node n4 = new Node(4);
		Node n5 = new Node(5);
		Node n6 = new Node(6);
		list.add(n1);
		list.add(n2);
		list.add(n3);
		list.add(n4);
		adjlist[1] = new ArrayList<Node>();
		adjlist[1].addAll(list);
		list.clear();
		list.add(n2);
		list.add(n4);
		adjlist[2] = new ArrayList<Node>();
		adjlist[2].addAll(list);
		list.clear();
		list.add(n3);
		list.add(n6);
		adjlist[3] = new ArrayList<Node>();
		adjlist[3].addAll(list);
		list.clear();
		list.add(n4);
//		list.add(n1);
		list.add(n5);
		adjlist[4] = new ArrayList<Node>();
		adjlist[4].addAll(list);
		list.clear();
		list.add(n5);
		list.add(n6);
		adjlist[5] = new ArrayList<Node>();
		adjlist[5].addAll(list);
		list.clear();
		list.add(n6);
		adjlist[6] = new ArrayList<Node>();
		adjlist[6].addAll(list);
		list.clear();
	}
	
	public static void bfs(){
		Queue<Node> queue = new LinkedList<Node>();
		
		queue.add(adjlist[1].get(0));
		
		while(!queue.isEmpty()){
		Node cur = queue.peek();
		System.out.println(queue.peek().data);
		queue.poll().isvisited = true;
		
		Iterator<Node> itr = adjlist[cur.data].iterator();
		itr.next();
		while(itr.hasNext()){
			Node node = itr.next();
			if(node.isvisited == false)
				queue.add(node);
		}
		}
	}
	
	public static void dfs(Node node){
		
		if(node == null)
			return;
		
		stack.push(node);
		node.isvisited = true;
		
		Node cur = (Node)stack.pop();
		System.out.println(cur.data);
			
		Iterator<Node> itr = adjlist[cur.data].iterator();
		int i = itr.next().data;
		while(itr.hasNext()){
			Node n = itr.next();
			if(n.isvisited == false && parent[n.data]==0){
				parent[n.data] = i;
				dfs(n);
			}
		}
	}
	
	public static void main(String[] args){
		creategraph();
		System.out.println("Graph Created");
//		bfs();
		dfs(adjlist[1].get(0));
	}
	
}
