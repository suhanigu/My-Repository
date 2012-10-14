package queryparser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;

public class CSVParser {

	public static String[][] lineitems = new String [10000][18];
	static QueryParser qparser = new QueryParser();

	public void csvparser() {

		try {

			
			String strFile = "C:/Users/suhanigu/workspace/QueryParser/src/queryparser/NBA.csv";
			
			BufferedReader br = new BufferedReader(new FileReader(strFile));
			String strLine = "";
			StringTokenizer st = null;
			int lineNumber = 0, tokenNumber = 0;

			while ((strLine = br.readLine()) != null) {
				lineNumber++;

				st = new StringTokenizer(strLine, ",");

				while (st.hasMoreTokens()) {
					String value = st.nextToken();
					tokenNumber++;
					System.out.println("Line # " + lineNumber + ", Token # "
							+ tokenNumber + ", Token : " + value);
					lineitems[lineNumber-1][tokenNumber-1] = value;
				}

				tokenNumber = 0;

			}

			System.out
					.println("Done with the Parsing, Lets print top 5 rows now");
			
			for (int i = 1; i < 101; i++) {
				String valuestring = "";
				System.out.println("Row Number --->"+ i+1);
				for (int j = 0; j < 18; j++) {
					System.out.println(lineitems[i][j] +"\t");
					valuestring += lineitems[i][j]+",";
				}
				valuestring = valuestring.substring(0,valuestring.length()-1);
				System.out.println("valuestring: " + valuestring);
				QueryParser.inputString = "insert into T1 values(" + valuestring + ");";
//				qparser.inputString = "insert into T1 values(lineitems[i][0],lineitems[i][1],lineitems[i][2],lineitems[i][3],lineitems[i][4],lineitems[i][5],lineitems[i][6],lineitems[i][7]," +
//						"lineitems[i][8],lineitems[i][9],lineitems[i][10],lineitems[i][11],lineitems[i][12],lineitems[i][13],lineitems[i][14],lineitems[i][15],lineitems[i][16]," +
//						"lineitems[i][17]);";
				QueryParser.insertParser();
				System.out.println("\n");
			}

		} catch (Exception e) {
			System.out.println("Exception while reading csv file: " + e);
		}
	}
}