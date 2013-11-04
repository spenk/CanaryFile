package net.bplaced.spenk.canaryfile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.canarymod.database.Column;
import net.canarymod.database.DataAccess;
import net.canarymod.database.Database;
import net.canarymod.database.exceptions.DatabaseAccessException;
import net.canarymod.database.exceptions.DatabaseReadException;
import net.canarymod.database.exceptions.DatabaseTableInconsistencyException;
import net.canarymod.database.exceptions.DatabaseWriteException;

public class FileDatabase extends Database{
	
    public static FileDatabase instance;
    public static String directory = "db/";
	
    public FileDatabase() {
        File path = new File(directory);
        
        if (!path.exists()) {
            path.mkdirs();
        }
    }

    public static FileDatabase getInstance() {
        if (instance == null) {
            instance = new FileDatabase();
        }
        return instance;
    }

	@Override
	public void insert(DataAccess data) throws DatabaseWriteException {
    	File file = new File(directory+data.getName()+".txt");
    	if (!file.exists()){
    		if (!FileHandler.createNewFile(file)){
    			throw new DatabaseWriteException("Could not create "+directory+data.getName()+".txt!");
    		}
    		try {
				initializeFile(file, data);
			} catch (DatabaseTableInconsistencyException e) {
				throw new DatabaseWriteException("Could not write to "+directory+data.getName()+".txt!");
			} catch (IOException e) {
				throw new DatabaseWriteException("Could not write to "+directory+data.getName()+".txt!");
			}
    	}
    	try {
			insertData(file, data);
		} catch (DatabaseTableInconsistencyException e) {
			throw new DatabaseWriteException("Could not write to "+directory+data.getName()+".txt!");
		} catch (IOException e) {
			throw new DatabaseWriteException("Could not write to "+directory+data.getName()+".txt!");
		}
	}

	@Override
	public void load(DataAccess data, String[] column, Object[] value) throws DatabaseReadException {
		File file = new File(directory+data.getName()+".txt");
		if (!file.exists()){
			throw new DatabaseReadException("Could not find "+directory+data.getName()+".txt to read from!");
		}
		if (column.length != value.length){
			throw new DatabaseReadException("Data is inconsistent!");
		}
		 try {
			 HashMap<String, Object> toReturn = new HashMap<String, Object>();
			 ArrayList<DataTag> tags = DataTag.parseTags(FileHandler.getFilePattern(file));
			 String[] line = FileHandler.findLineByValue(file, column, value, tags).split("\\|\\|");
			 for (int i = 0; i < tags.size();i++){
				 FileUtils.addTypeToMap(line[i], toReturn, tags.get(i));
			 }
			 data.load(toReturn);
		} catch (IOException e) {
			throw new DatabaseReadException("Could not read from database "+directory+data.getName()+".txt");
		} catch (DatabaseAccessException e) {
			throw new DatabaseReadException("Could not read from database "+directory+data.getName()+".txt");
		}
	}

	@Override
	public void loadAll(DataAccess data, List<DataAccess> datasets, String[] column, Object[] value) throws DatabaseReadException {
		File file = new File(directory + data.getName() + ".txt");
		if (!file.exists()) {
			throw new DatabaseReadException("Could not find " + directory + data.getName() + ".txt to read from!");
		}
		if (column.length != value.length) {
			throw new DatabaseReadException("Data is inconsistent!");
		}
		ArrayList<DataTag> tags;
		ArrayList<String> matches;
		try {
		tags = DataTag.parseTags(FileHandler.getFilePattern(file));
		matches = FileHandler.findLinesByValue(file, column, value, tags);
		} catch (IOException e) {
			throw new DatabaseReadException("Could not read from database "+directory+data.getName()+".txt");
		}
		
		for (String s : matches){
			String[] line = s.split("\\|\\|");
			HashMap<String, Object> dataSet = new HashMap<String, Object>();
			 for (int i = 0; i < tags.size();i++){
				 FileUtils.addTypeToMap(line[i], dataSet, tags.get(i));
			 }
			 
			DataAccess da = data.getInstance();

			try {
				da.load(dataSet);
			} catch (DatabaseAccessException e) {
				throw new DatabaseReadException("Could not read from database "+directory+data.getName()+".txt");
			}
			datasets.add(da);
		}
	}

	@Override
	public void remove(String table, String[] column, Object[] value) throws DatabaseWriteException {
		File file = new File(directory+table+".txt");
		if (!file.exists()){
			throw new DatabaseWriteException("Could not find "+directory+table+".txt!");
		}
		if (column.length != value.length){
			throw new DatabaseWriteException("Data is inconsistent at "+directory+table+".txt!");
		}
		 try {
			 ArrayList<DataTag> tags = DataTag.parseTags(FileHandler.getFilePattern(file));
			 String line = FileHandler.findLineByValue(file, column, value, tags);
			 FileHandler.removeFromFile(file, line);
		} catch (IOException e) {
			throw new DatabaseWriteException("Could not write to "+directory+table+".txt!");
		}
	}

	@Override
	public void update(DataAccess data, String[] column, Object[] value) throws DatabaseWriteException {
		File file = new File(directory + data.getName() + ".txt");
		ArrayList<DataTag> tags;
		String match;
		StringBuilder builder = new StringBuilder();
		if (!file.exists()) {
			throw new DatabaseWriteException("Could not find " + directory + data.getName() + ".txt to read from!");
		}
		if (column.length != value.length) {
			throw new DatabaseWriteException("Data is inconsistent!");
		}
		try {
			tags = DataTag.parseTags(FileHandler.getFilePattern(file));
			match = FileHandler.findLineByValue(file, column, value, tags);
		} catch (IOException e) {
			throw new DatabaseWriteException("Could not write to "+directory+data.getName()+".txt!");
		}
		
		if (match == null){
			insert(data);
		} else {
			remove(data.getName(), column, value);
			HashMap<Column, Object> dataSet;
			try {
				dataSet = data.toDatabaseEntryList();
			} catch (DatabaseTableInconsistencyException e) {
				throw new DatabaseWriteException("Could not write to "+directory+data.getName()+".txt!");
			}
			String[] split = match.split("\\|\\|");
			for (Column c : dataSet.keySet()){
				for (int i = 0; i < tags.size(); i++){
					if (tags.get(i).content.equals(c.columnName())){
						if (!tags.get(i).isInc){
							if (!split[i].equalsIgnoreCase(dataSet.get(c).toString())){
								split[i] = dataSet.get(c).toString();
							}
						}
					}
				}
			}
			for (String s : split){
				builder.append("\\|\\|"+s);
			}
			try {
				FileHandler.writeToFile(file, builder.toString().substring(2));
			} catch (IOException e) {
				throw new DatabaseWriteException("Could not write to "+directory+data.getName()+".txt!");
			}
		}
	}

	@Override
	public void updateSchema(DataAccess data) throws DatabaseWriteException {
		File file = new File(directory + data.getName() + ".txt");
		if (!file.exists()) {
			throw new DatabaseWriteException("Could not find " + directory + data.getName() + ".txt");
		}
		try {
		String oldDataTag = FileHandler.getFilePattern(file);
		ArrayList<String> contents = FileHandler.getFileContent(file);
		
		String newDagaTag = DataTag.getTagFromDataAccess(data);
		
		}catch (IOException e){
			throw new DatabaseWriteException("Could not write to "+directory+data.getName()+".txt!");
		} catch (DatabaseTableInconsistencyException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * This will create the first line that indicates the pattern of all written data.
	 * 
	 * @param file
	 * @param data
	 * @throws DatabaseTableInconsistencyException
	 * @throws IOException
	 */
    public void initializeFile(File file, DataAccess data) throws DatabaseTableInconsistencyException, IOException {
    	String tag = DataTag.getTagFromDataAccess(data);
		FileHandler.writeToFile(file, tag.substring(0, tag.length() - 2));
	}
    
    
    /**
     * 
     * this will strip the data and place it into order of the tag header
     * 
     * @param file
     * @param data
     * @throws DatabaseTableInconsistencyException
     * @throws IOException
     */
    public void insertData(File file, DataAccess data) throws DatabaseTableInconsistencyException, IOException{
    	HashMap<String, Object> CData = FileUtils.convertColumnMap(data.toDatabaseEntryList());
    	ArrayList<DataTag> tags = DataTag.parseTags(FileHandler.getFilePattern(file));
    	StringBuilder builder = new StringBuilder();
    	for (DataTag tag : tags){
    		if (tag.isInc){
    			builder.append(FileHandler.getNextEntry(file)+"\\|\\|");
    		} else {
    			builder.append(CData.get(tag.content)+"\\|\\|");
    		}
    	}
    	if (!FileHandler.isDupe(file, builder.toString(), tags)){
    		FileHandler.writeToFile(file, builder.toString().substring(0,builder.toString().length()-2));
    	}
    }
    
}
