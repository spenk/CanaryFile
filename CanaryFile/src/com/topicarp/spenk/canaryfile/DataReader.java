package com.topicarp.spenk.canaryfile;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class DataReader {

	BufferedReader reader;
	
	public DataReader(BufferedReader reader){
		this.reader = reader;
	}
	
	public String getFilePattern() throws IOException{
		String line;
		while ((line = reader.readLine()) != null) {
			if (line.startsWith("#")){
				reader.close();
				return line;
			}
		}
		reader.close();
		return null;
	}
	
	public String getNextNumber(int numId) throws IOException{
		int num = -1;
		String line;
		while ((line = reader.readLine()) != null) {
			if (line.startsWith("#")){
				continue;
			}
			String[] lineSplit = line.split("\\|");
			int tnum = Integer.parseInt(lineSplit[numId]);
			if (tnum > num){
				num = tnum;
			}
		}
		reader.close();
		int fin = num+1;
		return Integer.toString(fin);
	}//TODO change to file length
	
	public ArrayList<String> getFileContent() throws IOException{
		ArrayList<String> content = new ArrayList<String>();
		String line;
		while ((line = reader.readLine()) != null) {
			if (line.startsWith("#")){
				continue;
			}
			content.add(line);
		}
		reader.close();
		return content;
	}
	
	public boolean isDuplicate(String toCheck, boolean ignore, int toIgnore) throws IOException{
		ArrayList<String> fileContent = getFileContent();
		ArrayList<String> ignoredContent = new ArrayList<String>();
		int i = 0;
		if (ignore){
			
			StringBuilder toCheckBuilder = new StringBuilder();
			String[] check = toCheck.split("\\|");
			while(i < check.length){
				if (i == toIgnore){
					continue;
				}
				toCheckBuilder.append(check[i]+"|");
			}
			ignoredContent.add(toCheckBuilder.toString().substring(0,toCheckBuilder.toString().length()-1)); 
			i = 0;
			
			for(String x : fileContent){
				String[] content = x.split("\\|");
				StringBuilder builder = new StringBuilder();
				while(i < x.length()){
					if (i == toIgnore){
						continue;
					}
					builder.append(content[i]+"|");
				}
				ignoredContent.add(builder.toString().substring(0,builder.toString().length()-1)); 
			}
		}else{
			ignoredContent = fileContent;
		}
		return ignoredContent.contains(toCheck);
	}
}
