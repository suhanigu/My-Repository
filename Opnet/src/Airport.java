import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

abstract class Airplane {
	String name;
	int planeid;
	int routeid;
	
	abstract void init_airplane(String n, int pid, int rid);
}

class PassangerPlane extends Airplane{
	int capacity;
	int size;
	
	PassangerPlane(int c, int s){
		capacity  = c;
		size = s;
	}
	
	void init_airplane(String n, int pid, int rid){
		
	}
}

class CargoPlane extends Airplane{
	int weight;
	int size;
	
	CargoPlane(int w, int s){
		weight = w;
		size = s;
	}
	
	void init_airplane(String n, int pid, int rid){
		
	}
}

class List{
	public List(){
		
	}
	public void insert(int pos, Object o){
		
	}
	public void access(int pos){
		
	}
	public void getSize(){
		
	}
	public void remove(int pos){
		
	}
}

public class Airport implements Comparator<Object>{
	static ArrayList<Airplane> airplanes = new ArrayList<Airplane>();
	
	public int compare(Object o1, Object o2){
		Airplane a1 = (Airplane)o1;
		Airplane a2 = (Airplane)o2;
		if((a1 instanceof PassangerPlane) && (a2 instanceof PassangerPlane)){
			PassangerPlane p1 = (PassangerPlane)a1;
			PassangerPlane p2 = (PassangerPlane)a2;
			if(p1.size > p2.size)
				return 1;
			else if(p1.size < p2.size)
				return -1;
			else
				return 0;
		}
		else if((a1 instanceof CargoPlane) && (a2 instanceof CargoPlane)){
			CargoPlane p1 = (CargoPlane)a1;
			CargoPlane p2 = (CargoPlane)a2;
			if(p1.size > p2.size)
				return 1;
			else if(p1.size < p2.size)
				return -1;
			else
				return 0;
		}
		else if((a1 instanceof PassangerPlane) && (a2 instanceof CargoPlane)){
			return 1;
		}
		else if((a1 instanceof CargoPlane) && (a2 instanceof PassangerPlane)){
			return -1;
		}
		return 2;
	}
	
	public static void start(){
		airplanes = new ArrayList<Airplane>();
	}
	
	public static void enqueue(Airplane a){
		airplanes.add(airplanes.size(), a);
	}
	
	public static Airplane dequeue(){
		Collections.sort(airplanes, new Airport());
		System.out.println(airplanes.get(0));
		return airplanes.remove(0);
	}
	
	public static void main(String[] args){
		PassangerPlane p1 = new PassangerPlane(100, 4);
		PassangerPlane p2 = new PassangerPlane(100, 2);
		PassangerPlane p3 = new PassangerPlane(100, 8);
		CargoPlane c1 = new CargoPlane(100, 5);
		CargoPlane c2 = new CargoPlane(100, 7);
		CargoPlane c3 = new CargoPlane(100, 4);
		PassangerPlane b;
		CargoPlane c;
		
		enqueue(c1);
		enqueue(p1);
		enqueue(c2);
		enqueue(p2);
		enqueue(c3);
		enqueue(p3);
		
		Airplane a = dequeue();
		if(a instanceof PassangerPlane){
			b = (PassangerPlane)a;
//			System.out.println(b.size());
		}
		else{
			c = (CargoPlane)a;
//			System.out.println(c.size());
		}
		
		
	}
}