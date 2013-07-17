package com.topicarp.spenk.canaryfile;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import net.canarymod.database.Column;
import net.canarymod.database.DataAccess;
import net.canarymod.database.exceptions.DatabaseTableInconsistencyException;

public class DataPattern {

	DataAccess data;
	
	
	public DataPattern(DataAccess data){
		this.data = data;
	}
	
	public String getDataPattern() throws DatabaseTableInconsistencyException{
		StringBuilder pattern = new StringBuilder();
		HashSet<Column> tableLayout = data.getTableLayout();
		pattern.append("#");
		for (Column c : tableLayout){
			pattern.append("|"+c.columnName());
		}
		return pattern.toString();
	}
	
	public String[] sortData(String filePattern, HashMap<Column, Object> dataMap, BufferedReader bufferedReader) throws IOException{
		filePattern = filePattern.replace("#|", "");
		String[] pattern = filePattern.split("\\|");
		StringBuilder sortedData = new StringBuilder();
		HashMap<String,String> sortData = new HashMap<String,String>();
		boolean inc = false;
		int in = 0;
		
		int i = 0;
		for (Column c : dataMap.keySet()){
			if (!c.autoIncrement()){
			sortData.put(c.columnName(), dataMap.get(c).toString());
			}else{
				inc = true;
				for (String s : pattern){
					if(!s.equalsIgnoreCase(c.columnName())){
						i++;
					}else{
						DataReader reader = new DataReader(bufferedReader);
						sortData.put(c.columnName(), reader.getNextNumber(i));
						in = i;
					}
				}
			}
		}
		
		for (String pat : pattern){
		sortedData.append(sortData.get(pat)+"|");	
		}
		
		String toret = sortedData.toString().substring(0,sortedData.toString().length()-1);
		String[] returned = {toret,Boolean.toString(inc),Integer.toString(in)};
		return returned;
	}

}
