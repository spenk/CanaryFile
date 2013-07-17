package com.topicarp.spenk.canaryfile;

import java.io.BufferedWriter;
import java.io.IOException;

public class DataWriter {

	BufferedWriter writer;
	
	public DataWriter(BufferedWriter writer){
		this.writer = writer;
	}
	
	public void insertLine(String line) throws IOException{
		writer.write(line);
		writer.newLine();
		writer.flush();
		writer.close();
	}

}
