package database;

import java.io.*;
import java.util.*;

/**
 * @author Amitabh Sural
 * @author Suhani Gupta
 * @author Syed Jibran Uddin
 * 
 */
public class DataManager implements Serializable {

	private static final long serialVersionUID = 1L;
	public static DataFile df = new DataFile();

	public static ArrayList<DataFile> dataFileList = new ArrayList<DataFile>();

	public DataManager() {

	}

	public static DataFile createFile(String fileName,Map<String, Integer> descriptor) throws IllegalArgumentException{
		
		File dbFile = null;
		int i = 0;
		df = null;
			
		 if ((!dataFileList.isEmpty()) && (dataFileList.size() > 0)) {
			 for (i = 0; i < dataFileList.size(); i++)
			 {
				 df = dataFileList.get(i);
				 if (df != null && df.fileName.equals(fileName+".file")) {
					 throw new IllegalArgumentException();
				 }
				 else { df = null;	 }
			 } 
		}
		 
		if (df == null) 
		{ 
			dbFile = new File(fileName+".file");
				df = new DataFile(fileName+".file", descriptor);
				 dataFileList.add(df);
				return df;
			
		}
		return df;
	}

	@SuppressWarnings("unchecked")
	public static DataFile restoreFile(String fileName){
		if(dataFileList.contains(fileName+".file"))
		{
//			System.out.println("File asked to restore already open in memory");
			throw new IllegalArgumentException("fileName");
		}
		File f = new File(fileName+".file");
		if(!f.exists()){
//			System.out.println("File asked to restore does not exist");
			throw new IllegalArgumentException("fileName");
		}
		df  = null;
		try {
			FileInputStream fis = new FileInputStream(fileName+".file");
			ObjectInputStream in = new ObjectInputStream(fis);
			df = (DataFile) in.readObject();
			dataFileList.add(df);
			in.close();
			
			return df;
			
		} catch (IOException ex) {
			ex.printStackTrace();
		}catch (ClassNotFoundException ex){
			ex.printStackTrace();
		}
		return null;
	}


	public static String print(Map<String, String> record) {
		StringBuffer strBuf = new StringBuffer();
		String keyName = "", keyValue = "";
		if (record != null && record.size() > 0) {
			Set set = record.keySet();
			Iterator itr = set.iterator();
						
			while (itr.hasNext()) {
				String key = (String) itr.next();
				
				strBuf.append("\t"+key+" : "+record.get(key));
				strBuf.append("\n");
			
			}

		}
		return strBuf.toString();
	}

	
	public static void exit() {
		 int i = 0;
		DataFile tempDF = new DataFile();
		try {

			 for (i = 0; i < dataFileList.size(); i++) {
			 tempDF = dataFileList.get(i);
			if (tempDF != null)
				tempDF.dumpFile();
			tempDF = null;
			}
			dataFileList.clear();
			df = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
