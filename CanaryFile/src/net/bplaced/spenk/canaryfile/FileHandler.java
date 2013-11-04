package net.bplaced.spenk.canaryfile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class FileHandler {
    public static boolean createNewFile(File file){
    	try {
    		file.createNewFile();
    	}catch (Exception e){
    		return false;
    	}
    	return true;
    }
    
    public static void writeToFile(File f, String s) throws IOException{
    	PrintWriter writer = new PrintWriter(new FileWriter(f, true));
    	writer.println(s);
    	writer.flush();
    	writer.close();
    }
    
    public static void writeToFile(File f, ArrayList<String> list) throws IOException{
    	PrintWriter writer = new PrintWriter(new FileWriter(f, true));
    	for (String s : list){
    		writer.println(s);
    	}
    	writer.flush();
    	writer.close();
    }
    
    public static ArrayList<String> getFileContent(File f) throws IOException{
    	ArrayList<String> content = new ArrayList<String>();
    	BufferedReader reader = new BufferedReader(new FileReader(f));
    	String line;
    	while ((line = reader.readLine()) != null){
    		if (!line.startsWith("#")){
    			content.add(line);
    		}
    	}
    	reader.close();
    	return content;
    }
    
    public static String getFilePattern (File f) throws IOException{
    	BufferedReader reader = new BufferedReader(new FileReader(f));
    	String line;
    	while ((line = reader.readLine()) != null){
    		if (line.startsWith("#")){
    			reader.close();
    			return line;
    		}
    	}
    	reader.close();
		return null;
    }
    
    public static int getNextEntry(File f) throws IOException{
    	return getFileContent(f).size();
    }
    
    public static void removeFromFile(File f, String s) throws IOException{
    	ArrayList<String> contents = getFileContent(f);
    	ArrayList<String> toPrint = new ArrayList<String>();
    	String filePattern = getFilePattern(f);
    	for (String content : contents){
    		if (!content.equals(s)){
    			toPrint.add(s);
    		}
    	}
    	deleteFile(f);
    	createNewFile(f);
    	writeToFile(f, filePattern);
    	writeToFile(f, toPrint);
    }
    
    public static boolean deleteFile(File f){
    	return f.delete();
    }

	public static boolean isDupe(File file, String string, ArrayList<DataTag> tags) throws IOException {
		ArrayList<String> content = getFileContent(file);
		String[] cutString = string.split("\\|\\|");
		String toMatch = "";
		for (int i = 0; i < tags.size(); i++){
			if (!tags.get(i).isInc){
				toMatch +=cutString[i];
			}
		}
		
		for (String s : content){
			String[] split = s.split("\\|\\|");
			String toMatch2 = "";
			for (int i = 0; i < tags.size(); i++){
				if (!tags.get(i).isInc){
					toMatch2 +=split[i];
				}
			}
			if (toMatch.equals(toMatch2)){
				return true;
			}
		}
		return false;
	}
	
	public static String findLineByValue(File file, String[] columns, Object[] value, ArrayList<DataTag> tags) throws IOException{
		ArrayList<String> contents = getFileContent(file);
		for (int c = 0; c < columns.length; c++){
			for (String content : contents){
				int equalLines = 0;
				String[] split = content.split("\\||\\|");
				for (int i = 0; i < tags.size(); i++){
					if (columns[i].equalsIgnoreCase(tags.get(i).content)){
						if (value[i].equals(split[i])){
							equalLines++;
						}
					}
				}
				
				if (equalLines == columns.length){
					return content;
				}
			}
		}
		return null;
	}
	
	public static ArrayList<String> findLinesByValue(File file, String[] columns, Object[] value, ArrayList<DataTag> tags) throws IOException{
		ArrayList<String> contents = getFileContent(file);
		for (int c = 0; c < columns.length; c++){
			for (String content : contents){
				int equalLines = 0;
				String[] split = content.split("\\||\\|");
				for (int i = 0; i < tags.size(); i++){
					if (columns[i].equalsIgnoreCase(tags.get(i).content)){
						if (value[i].equals(split[i])){
							equalLines++;
						}
					}
				}
				
				if (equalLines == columns.length){
					contents.add(content);
				}
			}
		}
		return contents;
	}
	
}
