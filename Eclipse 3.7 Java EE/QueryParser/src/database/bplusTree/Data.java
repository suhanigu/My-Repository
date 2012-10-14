package database.bplusTree;

/**
 * @author Amitabh Sural
 * @author Suhani Gupta
 * @author Syed Jibran Uddin
 * 
 */

public class Data
{

	private int value;
	private boolean isDeleted = false;
	
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public boolean isDeleted() {
		return isDeleted;
	}
	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}	

}
