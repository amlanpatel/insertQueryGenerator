package com.practic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class QueryGeneratorRepo {
	public static void main(String args[]) throws IOException {
		String tblName = "exam_result";
		List<String> list = getInsertQueryList(tblName);
		if(list.size() > 0) {
			writeToFile(tblName, list);
		}
	}

	//write to File
	private static void writeToFile(String tblName, List<String> list) throws IOException {
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("E:\\JAVA_WorkSpace\\Official_Practice\\jdbc-query-generator\\InsertQueyFolder"+File.separator+tblName+".sql"));
		for(String line : list) {
			bufferedWriter.write(line);
			bufferedWriter.write("\n");
		}
		System.out.println("Data writing is completed");
		bufferedWriter.close();
	}

	private static List<String> getInsertQueryList(String tblName) {
		List<String> insertSqlList = new ArrayList<String>();
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/practice", "root", "root");
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select * from " + tblName);
			ResultSetMetaData metaData = rs.getMetaData();
			StringBuilder sql = new StringBuilder();
			sql.append("Insert into ");
			sql.append(metaData.getTableName(1));
			sql.append("(");
			int colCount = metaData.getColumnCount();
			for(int i = 1; i <= colCount; i++) {
				sql.append(metaData.getColumnName(i));
				if(i != colCount) sql.append(", ");
			}
			sql.append(") values(");
			String initialSql = sql.toString();
			while(rs.next()) {
				sql = new StringBuilder();
				sql.append(initialSql);
				metaData = rs.getMetaData();
				for(int i = 1; i <= colCount; i++) {
					int colType = metaData.getColumnType(i);
					if(Types.INTEGER == colType) {
						sql.append(rs.getInt(i));
					}else if(Types.DOUBLE == colType) {
						sql.append(rs.getDouble(i));
					}else if(Types.VARCHAR == colType) {
						sql.append("'");
						sql.append(rs.getString(i));
						sql.append("'");
					}else if(Types.DATE == colType) {
						sql.append("'");
						sql.append(rs.getDate(i));
						sql.append("'");
					}					
					if(i==colCount) {
						sql.append(");");
						System.out.println(sql.toString());
						insertSqlList.add(sql.toString());
					}else {
						sql.append(", ");
					}
				}
			}
			rs.close();
			stmt.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return insertSqlList;
	}
}
