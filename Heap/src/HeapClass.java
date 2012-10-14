
public class HeapClass {
		
	static class Heap<E extends Comparable<? super E>>{
		int[] heap;
		int size;
		int num;
	
	Heap(int[] h, int s, int n){
		heap = h;
		size = s;
		num = n;
	}
	
	public int heapsize(){
		return num;
	}
	
	public boolean isLeaf(int pos){
		return (pos >= num/2 && pos < num);
	}
	
	public int leftchild(int pos){
		if(pos > num/2){
			System.out.println("no left child");
			return -1;
		}
		return 2*pos + 1;
	}
	
	public int rightchild(int pos){
		if(pos > num/2){
			System.out.println("no right child");
			return -1;
		}
		return 2*pos + 2;
	}
	
	public int parent(int pos){
		if(pos <= 0){
			System.out.println("no parent");
			return -1;
		}
		return ((pos-1)/2);
	}
	
	public void insert(int val){
		if(num > size){
			System.out.println("Heap is full");
			return;
		}
		
		int cur = num;
		heap[cur] = val;
		num++;
		
		while(cur!=0 && heap[cur] > heap[parent(cur)]) {
				swap(cur, parent(cur));
				cur = parent(cur);
		}
	}
	
	public void swap(int a, int b){
		int temp = heap[a];
		heap[a] = heap[b];
		heap[b] = temp;
	}
	
	public void displayheap(){
		int i = 0;
		while(i<num){
			System.out.printf("%d ",heap[i]);
			i++;
		}
		System.out.printf("\n");
	}
	
	public void heapsort(){
		if(num==0)
			return;
		int i = num - 1;
		while(i>0){
			swap(0, i);
			i--;
			heapify(0,i);
		}
	}
	
	public void heapify(int start, int end){
		if(start > end)
			return;
		int i = start;
		int j = end;
		while(i<(start + end)/2){
		if(heap[leftchild(i)] > heap[rightchild(i)]){
			if(heap[i] < heap[leftchild(i)])
				swap(i, leftchild(i));
			i = 2*i + 1;
		}
		else if(heap[leftchild(i)] < heap[rightchild(i)]){
			if(heap[i] < heap[rightchild(i)])
				swap(i, rightchild(i));
			i = 2*i + 2;
		}
		}
	}
	
}
	
	public static void main(String[] args){
		int[] arr = new int[10];
		Heap<Integer> h = new Heap<Integer>(arr, 9, 0);
		h.insert(5);
		h.insert(4);
		h.insert(6);
		h.insert(3);
		h.insert(7);
		h.insert(2);
		h.insert(8);
		h.insert(1);
		h.insert(9);
		h.insert(10);
		
		h.displayheap();
		h.heapsort();
		h.displayheap();
	}
}
