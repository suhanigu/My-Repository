package queryparser;

/*
 *
 @author Amitabh Sural
 *
 */

import java.io.*;
import java.util.*;
import queryparser.*;

public class QueryParser {

	public static final String INSERT = "insert";
	public static final String UPDATE = "update";
	public static final String SELECT = "select";

	public static String inputString = "";

	public static int limit = -1;

	static QueryEval queryEval = new QueryEval();
	static CSVParser csvParser = new CSVParser();
	
	public static String tableName;
	static String string = "Select A, B, C From T1 Order by 1000*A+B*3 desc Limit 3;";
	static String insertString = "insert into T1 values (1,400,500)";
	static String crString = "create table T1 (A Integer Primary Key,	B Integer,C Integer);";
	static String dropIString = "Drop Index I1;";
	static String dropTString = "Drop Table T1;";

	/********************** Select Query Variables ****************************************************/

	public static List<String> columnNames = new ArrayList<String>();
	public static List<String> orderByIndexes = new ArrayList<String>();
	public static List<Integer> orderByCoeff = new ArrayList<Integer>();
	static int flag = -1;

	/********************** Insert Query Variables ****************************************************/
	public static List<String> values = new ArrayList<String>();

	/**************************************************************************************************/
	/********************** Create Query Variables ****************************************************/
	public static List<String> createValues = new ArrayList<String>();
	public static String primaryKey = "";
	public static List<String> finalValues = new ArrayList<String>();

	/**************************************************************************************************/
	/********************** Create Index Variables ****************************************************/

	public static String createIndex = "";
	public static String columnForIndex = "";

	/**************************************************************************************************/
	/********************** Drop Index Variables ****************************************************/

	public static String dropIndex = "";

	/**************************************************************************************************/

	public String[] columnValues;

	public String[] columnWhereNames;

	public String[] columnWhereValues;

	public static void selectParser() throws Exception {

		int m=0,n=0;
		orderByIndexes.clear();
		orderByCoeff.clear();
		columnNames.clear();
		inputString = inputString.toLowerCase();
		int fromPos = inputString.indexOf("from");
		if (fromPos == -1) {
			throw new Exception("Malformed SQL. Missing FROM statement.");
		}
		int tablestartpos = inputString.lastIndexOf("from") + 4;
		int tablendpos = inputString.indexOf("order");
		int orderstartpos = inputString.lastIndexOf("by") + 3;
		int orderendpos = -1;
		if (inputString.contains("desc")) {
			orderendpos = inputString.indexOf("desc");
			flag = 1;
		} else {
			orderendpos = inputString.indexOf("asc");
			flag = 2;
		}

		String listOfColumns = inputString.substring(7, fromPos);
		tableName = inputString.substring(tablestartpos, tablendpos);
		String orderByClause = inputString
				.substring(orderstartpos, orderendpos);

		System.out.println("Tables:" + tableName.trim().toUpperCase());
		System.out.println("ORDER BY CLAUSE:"
				+ orderByClause.trim().toUpperCase());

		/********************* Extracting from the ORDER BY Clause ***********************************/
		String clause = orderByClause.trim().toUpperCase();
		List<String> tempList = new ArrayList<String>();

		if (clause.contains("+")) {
			StringTokenizer tokens = new StringTokenizer(clause, "+");
			while (tokens.hasMoreTokens()) {
				String token = tokens.nextToken();
				tempList.add(token);
			}
			for (int k = 0; k < tempList.size(); k++) {
				String[] temp = { null, null };
				n=0;
				StringTokenizer tokenss = new StringTokenizer(tempList.get(m),"*");
				while (tokenss.hasMoreTokens()) {
					String token1 = tokenss.nextToken();
					temp[n++] = token1;
				}
				orderByCoeff.add(Integer.parseInt(temp[0]));
				orderByIndexes.add(temp[1]);
				m++;
			}
		} else {
			String[] temp = { null, null };
			int k = 0;
			StringTokenizer tokens = new StringTokenizer(clause, " ");
			while (tokens.hasMoreTokens()) {
				String token = tokens.nextToken();
				temp[k++] = token;
			}
			orderByCoeff.add(Integer.parseInt(temp[0]));
			orderByIndexes.add(temp[1]);

		}

		/*********************************************************************************************/

		StringTokenizer tokens = new StringTokenizer(listOfColumns.trim(), ",");
		while (tokens.hasMoreTokens()) {
			String token = tokens.nextToken();
			columnNames.add(token.trim().toUpperCase());
		}

		for (int i = 0; i < columnNames.size(); i++)
			System.out.println("Column : " + columnNames.get(i));
			
		for (int i = 0; i < orderByIndexes.size(); i++)
			System.out.println("orderby indexes: " + orderByIndexes.get(i));

		for (int i = 0; i < orderByCoeff.size(); i++)
			System.out.println("orderby coeff: " + orderByCoeff.get(i));
		/*
		 * if (inputString.contains("where")) { int wherestart =
		 * inputString.indexOf("where") + 5; int wherend =
		 * inputString.indexOf("="); String column =
		 * inputString.substring(wherestart, wherend); int valuestart = wherend
		 * + 1; String value = inputString.substring(valuestart,
		 * inputString.length() - 1); System.out.println("Column :" + column +
		 * "Value  :" + value);
		 * 
		 * }
		 */

		if (inputString.contains("limit"))
			limit = Integer.parseInt(inputString.substring(inputString.length() - 2, inputString.length() - 1));
		System.out.println("LIMIT : " + limit);
		
		if (flag == 1){
			queryEval.naive_desc();
			queryEval.selectValuesDesc();
		}
		if (flag == 2){
			queryEval.naive_asc();
			queryEval.selectValuesAsc();
		}
	}

	public static void insertParser() {

		String temp = inputString.toLowerCase();
		int tablestartpos = temp.lastIndexOf("insert into") + 11;
		int tablendpos = temp.indexOf("values");
		tableName = temp.substring(tablestartpos, tablendpos);
		System.out.println("Tables:" + tableName.trim().toUpperCase());
		
		int valuestartpos = temp.indexOf("(") + 1;
		int valuesendpos = temp.indexOf(")");
		String listOfValues = temp
				.substring(valuestartpos, valuesendpos);
		StringTokenizer tokens = new StringTokenizer(listOfValues.trim(), ",");
		while (tokens.hasMoreTokens()) {
			String token = tokens.nextToken();
			System.out.println("Unpadded string: " + token);
			token = String.format("%012d", Integer.parseInt(token));
			System.out.println("padded string: " + token);
			values.add(token);
		}

		for (int i = 0; i < values.size(); i++)
			System.out.println("Value : " + values.get(i).toUpperCase());

		queryEval.insertRecord();

			values.clear();
			System.out.println("After deletion....");
			
			for (int i = 0; i < values.size(); i++)
				System.out.println("Value : " + values.get(i).toUpperCase());
	}

	public static void createTableParser() {

		String createString = inputString.toLowerCase();
		int valuestartpos = createString.indexOf("(") + 1;
		int valuesendpos = createString.indexOf(")");
		String tableValues = createString
				.substring(valuestartpos, valuesendpos);
		int tablestart = createString.indexOf("table") + 5;
		tableName = createString.substring(tablestart, valuestartpos - 1);
		StringTokenizer tokens = new StringTokenizer(tableValues.toUpperCase()
				.trim(), ",");
		while (tokens.hasMoreTokens()) {
			String token = tokens.nextToken();
			createValues.add(token);
		}

		for (int i = 0; i < createValues.size(); i++) {
			if (createValues.get(i).contains("PRIMARY KEY")) {
				System.out.println("");
				int k = 0;
				String[] temp = { null, null, null, null };
				StringTokenizer tok = new StringTokenizer(createValues.get(i)
						.toUpperCase().trim(), " ");
				while (tok.hasMoreTokens()) {
					String token1 = tok.nextToken();
					temp[k++] = token1;
				}
				String tuple = temp[0];
				primaryKey = temp[0];
				finalValues.add(tuple);
			} else {
				int k = 0;
				String[] temp = { null, null };
				StringTokenizer tok = new StringTokenizer(createValues.get(i)
						.toUpperCase().trim(), " ");
				while (tok.hasMoreTokens()) {
					String token1 = tok.nextToken();
					temp[k++] = token1;
				}
				String tuple = temp[0];
				finalValues.add(tuple);
			}

		}

		for (int j = 0; j < finalValues.size(); j++)
			System.out.println("Final Values : "
					+ finalValues.get(j).toUpperCase());

		queryEval.createTable();

	}

	public static void dropIndexParser() {

		String dropIndexString = inputString.toLowerCase();
		int startpos = dropIndexString.lastIndexOf("index");
		dropIndex = dropIndexString.substring(startpos,
				dropIndexString.length() - 1);
	}

	public static void dropTableParser() {

		String dropTableString = inputString.toLowerCase();
		int startpos = dropTableString.indexOf("table") + 5;
		tableName = dropTableString.substring(startpos,
				dropTableString.length() - 1);
	}

	public static void createIndexParser() {

		String createIndexString = inputString.toLowerCase();
		int startpos = createIndexString.indexOf("index") + 5;
		int endpos = createIndexString.indexOf("on");
		createIndex = createIndexString.substring(startpos, endpos).toUpperCase();
		int valuestartpos = createIndexString.indexOf("(") + 1;
		int valuesendpos = createIndexString.indexOf(")");
		tableName = createIndexString.substring(endpos+2, valuestartpos-1).toUpperCase();
		columnForIndex = createIndexString.substring(valuestartpos, valuesendpos).toUpperCase();
		System.out.println("Inside create Index Parser Method");
		System.out.println("Index::" + createIndex.toUpperCase());
		System.out.println("Table :: " + tableName.toUpperCase());
		System.out.println("ColumnForIndexing:"+ columnForIndex.toUpperCase());
		queryEval.create_index();
	}

	public static void main(String[] args) {

		try {
			char inp = 'z';
			
			
			inputString = "create table T1(id Integer Primary Key,gp Integer,minutes Integer,pts Integer," +
					"dreb Integer,oreb Integer,reb Integer,asts Integer,stl Integer,blk Integer,turnover Integer," +
					"pf Integer,fga Integer,fgm Integer,fta Integer,ftm Integer,tpa Integer,tpm Integer)";
			
			createTableParser();
			csvParser.csvparser();
//			inputString = "insert into T1 values (1,400,500);";
//			insertParser();
//			inputString = "insert into T1 values(2,350,500);";
//			insertParser();
//			inputString = "insert into T1 values(3,400,350);";
//			insertParser();
//			inputString = "insert into T1 values(4,300,360);";
//			insertParser();
//			inputString = "insert into T1 values(5,400,250);";
//			insertParser();
//			inputString = "insert into T1 values(6,420,250);";
//			insertParser();
			inputString = "create index I1 on T1(gp);";
			createIndexParser();
			inputString = "create index I2 on T1(pts);";
			createIndexParser();
			inputString = "Select id, gp, pts, asts, pf From T1 Order by 1000*gp+3*pts desc Limit 5;";
			selectParser();
//			inputString = "Select id, gp, pts, asts, pf From T1 Order by 1000*gp+3*pts desc Limit 5;";
//			selectParser();
//			System.out.println("Printing the stored file");
//			String filecontent = QueryEval.df1.viewFile();
//			System.out.println(filecontent);
			
//
//			do {
//				System.out.println("Enter The Query:::: ");
//				BufferedReader br = new BufferedReader(new InputStreamReader(
//						System.in));
//				inputString = br.readLine();
//				inputString = inputString.toLowerCase();
//				System.out.println("Value Entered : " + inputString);
//				if (inputString.contains("CREATE TABLE")
//						|| inputString.contains("create table")) {
//					inp = 'c';
//				}
//				if (inputString.contains("CREATE INDEX")
//						|| inputString.contains("create index")) {
//					inp = 'm';
//				}
//				if (inputString.contains("SELECT")
//						|| inputString.contains("select")) {
//					inp = 's';
//				}
//				if (inputString.contains("INSERT")
//						|| inputString.contains("insert")) {
//					inp = 'i';
//				}
//				if (inputString.contains("DROP INDEX")
//						|| inputString.contains("drop index")) {
//					inp = 'p';
//				}
//				if (inputString.contains("DROP TABLE")
//						|| inputString.contains("drop table")) {
//					inp = 'q';
//				}
//				if (inputString.contains("$") || inputString.contains("$")) {
//					inp = 'v';
//				}
//				switch (inp) {
//				case 'c':
//					System.out
//							.println("Calling the Parser for Create Table function");
//					createTableParser();
//					break;
//				case 'p':
//					System.out
//							.println("Calling the Parser for Drop Index function");
//					dropIndexParser();
//					break;
//				case 'q':
//					System.out
//							.println("Calling the Parser for Drop Table function");
//					dropTableParser();
//					break;
//				case 's':
//					System.out
//							.println("Calling the Parser for Select function");
//					selectParser();
//					break;
//				case 'i':
//					System.out
//							.println("Calling the Parser for Insert function");
//					insertParser();
//					break;
//				case 'm':
//					System.out
//							.println("Calling the Parser for Create Index function");
//					createIndexParser();
//					break;
//
//				case 'v':
//					System.out.println("Printing the stored file");
//					String filecontent = QueryEval.df1.viewFile();
//					System.out.println(filecontent);
//					break;
//				default:
//					System.out.println("No value entered");
//				}
//			} while (inp != 'z');
//			System.out.println("Printing the content of the file");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
