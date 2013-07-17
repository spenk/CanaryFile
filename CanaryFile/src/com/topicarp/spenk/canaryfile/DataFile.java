package com.topicarp.spenk.canaryfile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import net.canarymod.database.Column;
import net.canarymod.database.DataAccess;
import net.canarymod.database.exceptions.DatabaseTableInconsistencyException;

public class DataFile {

	File dataFile;
	
	public DataFile(String dataFileName){
		this.dataFile = new File("db/"+dataFileName+".txt");
	}
	
	public void craftFile() throws IOException{
		if (!dataFile.exists()){
			dataFile.createNewFile();
		}
	}
	
	public BufferedWriter getWriter() throws IOException{
		FileWriter fileWriter = new FileWriter(dataFile,true);
		BufferedWriter bWriter = new BufferedWriter(fileWriter);
		return bWriter;
	}
	
	public BufferedReader getReader() throws IOException{
		FileReader fileReader = new FileReader(dataFile);
		BufferedReader bReader = new BufferedReader(fileReader);
		return bReader;
	}
	
	public boolean isEmpty(){
		return dataFile.length() == 0;
	}
	
	public void writePattern(DataAccess data) throws DatabaseTableInconsistencyException, IOException {
		DataPattern dataPattern = new DataPattern(data);
		DataWriter dataWriter = new DataWriter(getWriter());
		String pattern = dataPattern.getDataPattern();
		dataWriter.insertLine(pattern);
	}

	public void writeData(DataAccess data)throws DatabaseTableInconsistencyException, IOException {
		HashMap<Column, Object> dataList = data.toDatabaseEntryList();
		DataPattern dataPattern = new DataPattern(data);
		DataReader reader = new DataReader(getReader());
		DataWriter dataWriter = new DataWriter(getWriter());
		String[] dataLine = dataPattern.sortData(reader.getFilePattern(), dataList, this.getReader());
		if (!reader.isDuplicate(dataLine[0], Boolean.parseBoolean(dataLine[1]), Integer.parseInt(dataLine[2]))) {
			dataWriter.insertLine(dataLine[0]);
		}
	}
} 
