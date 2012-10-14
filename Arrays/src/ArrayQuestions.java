
public class ArrayQuestions {
	
	static int[] arr = new int[9];
	
	public static void reorganize(){
		int i=0;
		for(i=0;i<arr.length;i++){
			arr[i] = i;
		}
		
		i = 0;
		while(i<arr.length){
			int x = (i%3)*(arr.length/3) + (i/3);
			while(x<i)
				x = (x%3)*(arr.length/3) + (x/3);
			swap(i,x);
			i++;
		}
	}
	
	public static void swap(int a, int b){
		int temp = arr[a];
		arr[a] = arr[b];
		arr[b] = temp;
	}
	
	public static void printarray(){
		int i = 0;
		while(i<arr.length){
			System.out.printf("%d ", arr[i]);
			i++;
		}
	}
	
	public static void main(String[] args){
		reorganize();
		printarray();
	}
}
