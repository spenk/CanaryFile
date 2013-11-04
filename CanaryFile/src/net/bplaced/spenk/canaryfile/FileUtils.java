package net.bplaced.spenk.canaryfile;

import java.util.ArrayList;
import java.util.HashMap;

import net.canarymod.database.Column;
import net.canarymod.database.Column.ColumnType;
import net.canarymod.database.Column.DataType;

public class FileUtils {
	
	public static HashMap<String, Object> convertColumnMap(HashMap<Column, Object> columns){
		HashMap<String, Object> toReturn = new HashMap<String, Object>();
		for (Column c : columns.keySet()){
			toReturn.put(c.columnName(), columns.get(c));
		}
		return toReturn;
	}

    public static DataType getDataTypeByString(String type){
    	if (type.equals("[B]")){
    		return DataType.BOOLEAN;
    	}
    	if (type.equals("[BY]")){
    		return DataType.BYTE;
    	}
    	if (type.equals("[D]")){
    		return DataType.DOUBLE;
    	}
    	if (type.equals("[F]")){
    		return DataType.FLOAT;
    	}
    	if (type.equals("[I]")){
    		return DataType.INTEGER;
    	}
    	if (type.equals("[L]")){
    		return DataType.LONG;
    	}
    	if (type.equals("[SH]")){
    		return DataType.SHORT;
    	}
    	if (type.equals("[S]")){
    		return DataType.STRING;
    	}
    	return null;
    }
    
    public static ColumnType getColumnTypeByString(String type){
    	if (type.equals("[P]")){
    		return ColumnType.PRIMARY;
    	}
    	if (type.equals("[U]")){
    		return ColumnType.UNIQUE;
    	}
    	if (type.equals("[N]")){
    		return ColumnType.NORMAL;
    	}
		return null;
    }
    
    public static String getStringByDataType(DataType type){
    	switch (type){
    		case BOOLEAN: return "[B]";
    		case BYTE:    return "[BY]";
    		case DOUBLE:  return "[D]";
    		case FLOAT:   return "[F]";
    		case INTEGER: return "[I]";
    		case LONG:    return "[L]";
    		case SHORT:   return "[SH]";
    		case STRING:  return "[S]";
    		default: 	  return "[]";
    	}
    }
    
    public static String getStringByColumnType(ColumnType type){
    	switch (type){
    		case PRIMARY: return "[P]";
    		case NORMAL:  return "[N]";
    		case UNIQUE:  return "[U]";
    		default:      return "[]";
    	}
    }
    
    public static void addTypeToMap(Object child, HashMap<String, Object> dataSet, DataTag tag) {
    	DataType type = tag.dataType;
        switch (type) {
            case BYTE:
                if (tag.isList) {
                    ArrayList<Byte> byteList = new ArrayList<Byte>();

                    for (String el : child.toString().substring(1, child.toString().length()-1).split("\\|\\|")) {
                        byteList.add(Byte.parseByte(el));
                    }
                    dataSet.put(tag.content, byteList);
                }
                else {
                    dataSet.put(tag.content, Byte.parseByte(child.toString()));
                }
                break;

            case DOUBLE:
                if (tag.isList) {
                    ArrayList<Double> byteList = new ArrayList<Double>();

                    for (String el : child.toString().substring(1, child.toString().length()-1).split("\\|\\|")) {
                        byteList.add(Double.parseDouble(el));
                    }
                    dataSet.put(tag.content, byteList);
                }
                else {
                    dataSet.put(tag.content, Double.parseDouble(child.toString()));
                }
                break;

            case FLOAT:
                if (tag.isList) {
                    ArrayList<Float> byteList = new ArrayList<Float>();

                    for (String el : child.toString().substring(1, child.toString().length()-1).split("\\|\\|")) {
                        byteList.add(Float.parseFloat(el));
                    }
                    dataSet.put(tag.content, byteList);
                }
                else {
                    dataSet.put(tag.content, Float.parseFloat(child.toString()));
                }
                break;

            case INTEGER:
                if (tag.isList) {
                    ArrayList<Integer> byteList = new ArrayList<Integer>();

                    for (String el : child.toString().substring(1, child.toString().length()-1).split("\\|\\|")) {
                        byteList.add(Integer.parseInt(el));
                    }
                    dataSet.put(tag.content, byteList);
                }
                else {
                    dataSet.put(tag.content, Integer.parseInt(child.toString()));
                }
                break;

            case LONG:
                if (tag.isList) {
                    ArrayList<Long> byteList = new ArrayList<Long>();

                    for (String el : child.toString().substring(1, child.toString().length()-1).split("\\|\\|")) {
                        byteList.add(Long.parseLong(el));
                    }
                    dataSet.put(tag.content, byteList);
                }
                else {
                    dataSet.put(tag.content, Long.parseLong(child.toString()));
                }
                break;

            case SHORT:
                if (tag.isList) {
                    ArrayList<Short> byteList = new ArrayList<Short>();

                    for (String el : child.toString().substring(1, child.toString().length()-1).split("\\|\\|")) {
                        byteList.add(Short.parseShort(el));
                    }
                    dataSet.put(tag.content, byteList);
                }
                else {
                    dataSet.put(tag.content, Short.parseShort(child.toString()));
                }
                break;

            case STRING:
                if (tag.isList) {
                    ArrayList<String> byteList = new ArrayList<String>();

                    for (String el : child.toString().substring(1, child.toString().length()-1).split("\\|\\|")) {
                        byteList.add(el);
                    }
                    dataSet.put(tag.content, byteList);
                }
                else {
                    dataSet.put(tag.content, child.toString());
                }
                break;

            case BOOLEAN:
                if (tag.isList) {
                    ArrayList<Boolean> byteList = new ArrayList<Boolean>();

                    for (String el : child.toString().substring(1, child.toString().length()-1).split("\\|\\|")) {
                        byteList.add(Boolean.valueOf(el));
                    }
                    dataSet.put(tag.content, byteList);
                }
                else {
                    dataSet.put(tag.content, Boolean.valueOf(child.toString()));
                }
                break;

            default:
                break;
        }
    }

}
