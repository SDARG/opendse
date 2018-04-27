package net.sf.opendse.io;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.opendse.model.Application;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;


/**
 * The {@code TgffReader} imports an {@code Application}, {@code Mappings} and a set of {@code Resource} types from a tgff-file.
 * 
 * @author richthammer 
 */
public class TgffReader {
	
	private static final String HYPERPERIOD = "@HYPERPERIOD";
	private static final String TASK_GRAPH = "@TASK_GRAPH";
	private static final String COMMUN_QUANT = "@COMMUN_QUANT";
	private static final String CORE = "@CORE";
	private static final String CLIENT_PE = "@CLIENT_PE";
	private static final String SERVER_PE = "@SERVER_PE";
	private static final String PROC = "@PROC";
	private static final String LINK = "LINK";		// also for @CLIENT_LINK, @SERVER_LINK, @PRIM_LINK -> no "@"
	private static final String WIRE = "@WIRING";
	
	private static final String TASK = "TASK";
	private static final String ARC = "ARC";
	private static final String HARD_DEADLINE = "HARD_DEADLINE";
	private static final String SOFT_DEADLINE = "SOFT_DEADLINE";
	
	private static final String VALID = "valid";
	private static final String TYPE = "type";
	
	private static final String CLOSING = "}";
	private static final String COMMENT = "#";
	private static final String AT = "@";
	private static final String SEPARATOR = " +";
	private static final String CONNECTOR = "_";
	
	public static final String TGFF_TYPE = "TGFF_TYPE";
	public static final String PERIOD = "PERIOD"; 
	public static final String MSG_SIZE = "MSG_SIZE";
	public static final String RES_ATTRIBUTES = "RES_ATTRIBUTES";
	public static final String RES_VALUES = "RES_VALUES";
	
	
	protected String fileName;
	protected boolean parsed = false;
	protected double hyperperiod;
	protected String memory;

	protected Application<Task, Dependency> application;
	protected Architecture<Resource, Link> architecture;
	protected Mappings<Task, Resource> mappings;
	
	protected Map<String, String> properties;
	protected Map<String, Double> messageSizes;	

	protected Map<String, List<Task>> tgffTypeMap = new HashMap<String, List<Task>>();

	
	public TgffReader(String fileName) {
		this.fileName = fileName;
		parseFile(fileName);
	}
	
	public TgffReader(File file) {
		this.fileName = file.getAbsolutePath();
		parseFile(fileName);
	}

	public Application<Task, Dependency> getApplication() {
		return application;
	}
	
	public Mappings<Task, Resource> getMappings() {
		return mappings;
	}
	
	public Architecture<Resource, Link> getArchitecture() {
		return architecture;
	}
	
	/**
	 * Read application, mappings and resource types from a tgff-file.
	 * 
	 * @param fileName
	 *            the tgff-file name
	 */
	private void parseFile(String fileName) {
		
		if (!parsed) {
			parseFile(new File(fileName));
			parsed = true;			
		}	
	}
	

	private void parseFile(File tgffFile) {
		
		application = new Application<Task, Dependency>();
		architecture = new Architecture<Resource, Link>();
		mappings = new Mappings<Task, Resource>();
				
		try {
			BufferedReader br = new BufferedReader(new FileReader(tgffFile));
						
			String currentLine;
			while ((currentLine = br.readLine()) != null) {
					
				// import hyperperiod
				if (currentLine.contains(HYPERPERIOD)) {
					this.hyperperiod = importHyperperiod(currentLine);
				}
				
				// import message sizes
				else if (currentLine.contains(COMMUN_QUANT)) {
					this.messageSizes = importMessageSizes(br);
				}
					
				// import application graphs
				else if (currentLine.contains(TASK_GRAPH)) {
			    	importTaskGraph(currentLine, br);
			    }
				
				// import resources and mappings (only mappings to valid resource types are created)
				else if (currentLine.contains(CORE) || currentLine.contains(PROC) || 
						 currentLine.contains(CLIENT_PE) || currentLine.contains(SERVER_PE)) {
					importCore(currentLine, br);
				}
				
				// import -coords/-cowls link resources
				else if (currentLine.contains(LINK)) {
					importLink(currentLine, br);
				}
				
				// import -mocsyn wiring-properties as resource
				else if (currentLine.contains(WIRE)) {
					importWiring(br);
				}
				
				// other @-annotated properties 
				else if (!isComment(currentLine) && currentLine.contains(AT)) {
					importProperty(currentLine);
				}
			}			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	private void importCore(String name, BufferedReader br) throws IOException {
		
		// create resource (type)
		Resource res = new Resource("r" + name.split(SEPARATOR)[1]);
		
		// first line contains attributes of resources
		String [] resAttributes = (br.readLine()).replace(COMMENT, "").trim().split(SEPARATOR);		
		
		// second line contains attribute values
		String [] resValues = br.readLine().trim().split(SEPARATOR);
		
		assert resAttributes.length == resValues.length : "tgff-file \"" + CORE + "\": number of values is not "
				+ "equal to required number of resource attributes";
		
		for (int i = 0; i < resAttributes.length; i++) {
			res.setAttribute(resAttributes[i], resValues[i]);
		}			
		architecture.addVertex(res);
		
		// create mappings
		String line;
		List<String> attributes = new LinkedList<String>();
		
    	while (!isClosing(line = br.readLine())) {
		
    		// extract attributes of resource type
    		if (line.contains(TYPE)) {
    			attributes = new LinkedList<String>(Arrays.asList(line.replace(COMMENT, "").trim().split(SEPARATOR)));
    		}
    		// extract values for each attribute
    		else if (!isComment(line) && line.length() > 0) {

    			String [] values = line.split(SEPARATOR);
    			assert values.length == attributes.size() : "tgff-file \"" + CORE + "\": number of values is not "
    					+ "equal to required number of attributes";
    			
    			String tgffType = values[0];
    		
    			// only add mappings to valid resource type
    			boolean validMapping = false;
    			
    			if (attributes.contains(VALID)) {
    				validMapping = values[attributes.indexOf(VALID)].equals("1") ? true : false;
    			}
    			
    			if (validMapping) {
    			
    				// if tasks exist that can be mapped to current resource type
	    			if (tgffTypeMap.containsKey(tgffType)) {
		    			
	    				for (Task task : tgffTypeMap.get(tgffType)) {
	    					String mappingID = "m" + CONNECTOR + task.getId() + CONNECTOR + res.getId();
	    					Mapping<Task, Resource> mapping = new Mapping<Task, Resource>(mappingID, task, res);
	    					
	    					// annotate extracted attributes and values
	    					for (int i = 0; i < values.length; i++) {
	    						mapping.setAttribute(attributes.get(i), values[i]);
	    					}
	    					mappings.add(mapping);
		    			}
	    			}
    			}
    		}		
    	}		
	}

	private static Map<String, Double> importMessageSizes (BufferedReader br) throws NumberFormatException, IOException {

		Map<String, Double> sizes = new HashMap<String, Double>();
		String line;
		
		while (!isClosing(line = br.readLine())) {
			if (!isComment(line)) {					
				String [] entries = line.split(SEPARATOR);
				assert entries.length == 2: "tgff-file \"" + COMMUN_QUANT + "\": wrong number of entries";
								
	    		sizes.put(entries[0], Double.valueOf(entries[1]));
	    	}			    		
    	}
		return sizes;
	}
	
	private void importTaskGraph(String name, BufferedReader br) throws NumberFormatException, IOException {
		
		String id = CONNECTOR + name.split(SEPARATOR)[1];
		
		String line;
		double period = -1;
		
    	while (!isClosing(line = br.readLine())) {
    		if (!isComment(line)) {
			
				if (line.contains(PERIOD)) {
					period = Double.parseDouble(line.replace(PERIOD, "").replaceAll(SEPARATOR, ""));	
				}
					
				else if (line.contains(TASK)) {
					addTask(line, id, period);
				}
				
				else if (line.contains(ARC)) {
					addCommunication(line, id, period);
				}
				
				else if (line.contains(HARD_DEADLINE)) {
					addDeadline(line, id, HARD_DEADLINE);
				}
				else if (line.contains(SOFT_DEADLINE)) {
					addDeadline(line, id, SOFT_DEADLINE); 
				}	
			}	
    	}
	}

	private void addTask(String line, String suffix, double period) {
	
		String [] entries = line.split(SEPARATOR);
		assert entries.length == 4: "tgff-file \"" + TASK + "\": wrong number of entries";
		
		String id = entries[1] + suffix;
		String type = entries[3];
		
		Task task = new Task(id);
		task.setAttribute(PERIOD, period);
		task.setAttribute(TGFF_TYPE, type);
		
		// for more efficient generation of resource type mappings
		if (tgffTypeMap.containsKey(type)) {
			List<Task> taskList = tgffTypeMap.get(type);
			taskList.add(task);
		}
		else {
			LinkedList<Task> taskList = new LinkedList<Task>();
			taskList.add(task);
			
			tgffTypeMap.put(type, taskList);
		}
		
		application.addVertex(task);
	}	
	
	private void addCommunication (String line, String suffix, double period) {
			
		String [] entries = line.split(SEPARATOR);
		assert entries.length == 8: "tgff-file \"ARC\": wrong number of entries in line";
		
		String id = entries[1];
			
		Communication comm = new Communication(id);
		comm.setAttribute(PERIOD, period);
		comm.setAttribute(TGFF_TYPE, entries[7]);
		comm.setAttribute(MSG_SIZE, messageSizes.get(entries[7]));
		
		Task t1 = application.getVertex(entries[3] + suffix);
		Task t2 = application.getVertex(entries[5] + suffix);
		
		application.addVertex(comm);
		application.addEdge(new Dependency(id + "_0"), t1, comm);
		application.addEdge(new Dependency(id + "_1"), comm, t2);		
	}
	
	private void addDeadline(String line, String suffix, String deadlineType) {
		
		String [] entries = line.split(SEPARATOR);
		assert entries.length == 6 : "tgff-file \"" + deadlineType +"\": wrong number of entries";
		
		Task t = application.getVertex(entries[3] + suffix);
		
		if (t != null) {
			t.setAttribute(deadlineType, Double.parseDouble(entries[5]));
		}
	}
	
	private void importWiring(BufferedReader br) throws IOException {
		
		Resource res = new Resource(WIRE);
		
		String currentLine;
		String property = "";
		
		while (!isClosing(currentLine = br.readLine())) {
			
			// get attribute name
			if (isComment(currentLine)) {
				property = currentLine.replace(COMMENT, "").trim();
			}
			// get corresponding attribute value
			else {
				res.setAttribute(property, currentLine);
			}
		}
		architecture.addVertex(res);		
	}

	private void importProperty(String line) {
		
		if (properties == null) {
			properties = new HashMap<String, String>();
		}
		
		String [] property = line.split(SEPARATOR);
		assert property.length >= 2: "tgff-file \"" + line + "\": number of values is not equal to required "
				+ "number of resource attributes";
		
		properties.put(property[0], property[1]);	
	}

	private void importLink(String name, BufferedReader br) throws IOException {
				
		// create resource
		Resource link = new Resource("l" + name.split(SEPARATOR)[1]);
		
		// next line contains attributes of links
		String [] linkAttributes = (br.readLine()).replace(COMMENT, "").trim().split(SEPARATOR);		
		
		String currentLine;
		while (!isClosing(currentLine = br.readLine())) {
			
			if (!isComment(currentLine)) {
		
				String [] linkValues = currentLine.trim().split(SEPARATOR);				
				assert linkAttributes.length == linkValues.length : "tgff-file \"" + LINK + "\": number of values "
						+ "is not equal to required number of resource attributes";
							
				for (int i = 0; i < linkAttributes.length; i++) {
					link.setAttribute(linkAttributes[i], linkValues[i]);
				}
			}
		}
		architecture.addVertex(link);
	}

	private static double importHyperperiod(String line) {
		return Double.parseDouble(line.replace(HYPERPERIOD, "").trim());
	}

	private static boolean isComment(String currentLine) {

		if (currentLine.startsWith(COMMENT)) {
			return true;
		}
		return false;
	}	

	private static boolean isClosing (String s) {
		return (s.contains(CLOSING) && !s.contains(COMMENT));
	}
}