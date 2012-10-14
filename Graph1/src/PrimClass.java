import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class PrimClass {
	
	public static class Node{
		int data;
		int cost;
		boolean isvisited;
		static int inf = 1000;
		
		public Node(int n){
			data = n;
			cost = inf;
			isvisited = false;
		}
	}
	
	static ArrayList<Node>[] adjlist = (ArrayList<Node>[]) new ArrayList[13];
	static Node[] node = (Node[]) new Node[13];
	
	public static void creategraph(){
		
		initgraph();
		
		addedge(1,2,1);
		addedge(1,3,4);
		addedge(1,9,4);
		addedge(2,1,1);
		addedge(2,3,2);
		addedge(2,4,1);
		addedge(3,1,3);
		addedge(3,2,2);
		addedge(3,4,2);
		addedge(3,7,2);
		addedge(3,8,3);
		addedge(4,2,1);
		addedge(4,3,2);
		addedge(4,5,5);
		addedge(4,6,3);
		addedge(4,7,3);
		addedge(5,4,5);
		addedge(5,6,4);
		addedge(5,12,2);
		addedge(6,4,3);
		addedge(6,5,4);
		addedge(6,7,6);
		addedge(6,11,1);
		addedge(7,3,2);
		addedge(7,4,3);
		addedge(7,6,6);
		addedge(7,8,2);
		addedge(7,11,4);
		addedge(8,3,3);
		addedge(8,7,2);
		addedge(8,10,2);
		addedge(9,1,4);
		addedge(9,8,4);
		addedge(9,10,5);
		addedge(10,8,2);
		addedge(10,9,5);
		addedge(10,11,3);
		addedge(11,6,1);
		addedge(11,7,4);
		addedge(11,10,3);
		addedge(11,12,4);
		addedge(12,5,2);
		addedge(12,11,4);
	}
	
	public static void initgraph(){
		for(int i = 0; i<14; i++){
			node[i] = new Node(i);
		}
	}
	public static void addedge(int v1, int v2, int e){
	
			adjlist[v1].add(node[v2]);
			node[v2].cost = e;
	}
	
	public static void primalgo(){
		Queue<Node> queue = new LinkedList<Node>();
		ArrayList<Integer> parent = new ArrayList<Integer>();
		ArrayList<Integer> value = new ArrayList<Integer>();
		
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
	
	public static void main(String[] args){
		creategraph();
		primalgo();
	}

}
