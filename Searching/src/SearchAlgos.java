import java.util.ArrayList;


public class SearchAlgos {

	static ArrayList<Integer> arr = new ArrayList<Integer>();
	
	public static void createarray(ArrayList<Integer> a){
		int i = 1;
		while(i<=10){
			a.add(i);
			i++;
		}
	}
	
	public static void printarray(ArrayList<Integer> a){
		int i = 0;
		while(i<a.size()){
			System.out.println(a.get(i) + ",");
			i++;
		}
	}
	
	public static void binarysearch(ArrayList<Integer> a, int x, int start, int end) throws InterruptedException{
	
		int mid = 0;
		int res = 0;
		
		if(end==start)
			System.exit(0);
		
		while(end >= start){
			mid = (start+end)/2;
			System.out.println("middle index: " + mid);
			System.out.println("middle element: " + a.get(mid));
			Thread.sleep(1000);
			if(a.get(mid) == x){
				System.out.println("Found at: " + mid);
				System.exit(0);
			}
			if(x < a.get(mid))
				binarysearch(a, x, start, mid-1);
			else
				binarysearch(a, x, mid+1, end);
		}
	}
	 
	public static void main(String[] args) throws InterruptedException{
		createarray(arr);
		//printarray(arr);
		System.out.println("Size of array: " + arr.size());
		binarysearch(arr, 11, 0, arr.size()-1);
	}
}
