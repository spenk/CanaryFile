package net.bplaced.spenk.canaryfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.canarymod.database.Column;
import net.canarymod.database.Column.ColumnType;
import net.canarymod.database.Column.DataType;
import net.canarymod.database.DataAccess;
import net.canarymod.database.exceptions.DatabaseTableInconsistencyException;

public class DataTag {
	
	private String dataTag;
	private String rawContent;
	private String rawDataType;
	private String rawColumnType;
	
	public DataType dataType;
	public ColumnType columnType;
	public String content;
	public boolean isInc = false;
	public boolean isList = false;
	
	public DataTag(String dataTag){
		this.dataTag = dataTag;
		this.sortData();
	}
	
	private void sortData(){
		this.getContent();
		this.getTypes();
		this.getAdditionalData();
	}
	
	private void getContent() {
		Pattern p = Pattern.compile("\\{(.*?)\\}");
		Matcher m = p.matcher(dataTag);
		m.find();
		rawContent = m.group(0);
		content = m.group(1);
	}
	
	private void getTypes(){
		Pattern p = Pattern.compile("\\[(.*?)\\]");
		Matcher m = p.matcher(dataTag);
		boolean l = false;
		while (m.find()){
			if (!l){
				rawColumnType = m.group(0);
				columnType = FileUtils.getColumnTypeByString(m.group(0));
				l = true;
			}
			rawDataType = m.group(0);
			dataType = FileUtils.getDataTypeByString(m.group(0));
		}
	}
	
	private void getAdditionalData(){
		String s = dataTag.replace(rawContent, "").replace(rawColumnType, "").replace(rawDataType, "");
		if (s.contains("+")){
			isInc = true;
		}
		if (s.contains("~")){
			isList = true;
		}
	}
	
	public static ArrayList<DataTag> parseTags(String dataList){
		ArrayList<DataTag> tags = new ArrayList<DataTag>();
		dataList = dataList.substring(2);
		for (String s : dataList.split("\\|\\|")){
			tags.add(new DataTag(s));
		}
		return tags;
	}
	
	public static String getTagFromDataAccess(DataAccess data) throws DatabaseTableInconsistencyException{
		HashMap<Column, Object> columns = data.toDatabaseEntryList();
		StringBuilder firstLine = new StringBuilder();
		firstLine.append("#\\|\\|");
		for(Column c : columns.keySet()){
			String toAppend = null;
			toAppend += FileUtils.getStringByColumnType(c.columnType());
			toAppend += "{"+c.columnName()+"}";
			toAppend += FileUtils.getStringByDataType(c.dataType());
			
			if (c.autoIncrement()){
				toAppend += "+";
			}
			if (c.isList()){
				toAppend += "~";
			}
			
			toAppend += "\\|\\|";
			firstLine.append(toAppend);
		}
		return firstLine.toString();
	}
}
