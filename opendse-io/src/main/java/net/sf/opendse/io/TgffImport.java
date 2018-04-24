/*******************************************************************************
 * Copyright (c) 2015 OpenDSE
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package net.sf.opendse.io;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


import net.sf.opendse.model.Application;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Attributes;
import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Edge;
import net.sf.opendse.model.Element;
import net.sf.opendse.model.Function;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Node;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Routings;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.TgffCommunication;
import net.sf.opendse.model.TgffTask;


/**
 * The {@code TgffImport} imports an {@code Application} from a tgff-file.
 * 
 */


	

public class TgffImport {
	
	private static final String TASK_GRAPH = "@TASK_GRAPH";
	private static final String COMMUN_QUANT = "@COMMUN_QUANT";

	private static final String TASK = "TASK";
	private static final String ARC = "ARC";
	
	private static final String tgffPERIOD = "PERIOD";
	private static final String PERIOD = "h"; // attribute name required by opendse-realtime package
	private static final String WCET = "e";

	private static final String HARD_DEADLINE = "HARD_DEADLINE";
	private static final String SOFT_DEADLINE = "SOFT_DEADLINE";
	
	private static final String CLOSING = "}";
	private static final String COMMENT = "#";
	private static final String AT = "@";
	private static final String WHITESPACE = " ";
	private static final String CONNECT = "_";

	private static final String E = "E";
	
	private static final String X = "X-Coord";
	private static final String Y = "Y-Coord";
	
	private static final String COMM = "COMM";
	private static final String COMP = "COMP";
	
	protected Map<String, Double> messageSizes;
	
	// list of resource types to be used, as specified in tgff-files!
	protected List<String> resourceTypes = Arrays.asList("AMD K6-2E+ 500Mhz/ACR -- square", 
						 "IBM PowerPC 405GP - 266 Mhz -- rectangle", "AMD K6-IIIE+ 550Mhz/ACR -- rectangle");

	protected Map<String, Map<String, String[]>> resources;
	
	protected String fileName;
	protected Application<Task, Dependency> application;
	protected Architecture<Resource, Link> architecture;
	
	protected boolean multicore = true;


	protected Map<String, Element> knownElements = new HashMap<String, Element>();

	public TgffImport(String fileName) {
		this.fileName = fileName;
	}


	/**
	 * Import an application from a tgff-file.
	 * 
	 * @param filename
	 *            the file name
	 * @return the application
	 */
	public Application<Task, Dependency> getApplication() {
		
		if (application == null) {
			application = getApplication(new File(fileName));
		}
		
		return application;
	}
	
	
	
	

	/**
	 * Import an application from a tgff-file.
	 * 
	 * @param file
	 *            the file
	 * @return the application
	 */
	public Application<Task, Dependency> getApplication(File tgffFile) {
		
		application = new Application<Task, Dependency>();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(tgffFile));
						
			String currentLine;
			while ((currentLine = br.readLine()) != null) {
				
				if (currentLine.contains(COMMUN_QUANT)) {
					messageSizes = new HashMap<String, Double>();
					
					while (!isClosing(currentLine = br.readLine())) {
			    		String [] elems = currentLine.split(WHITESPACE);
			    		
			    		// skip comment line
			    		if (elems.length == 2) {
			    			messageSizes.put(elems[0], Double.parseDouble((elems[1].split(E))[0]));
			    		}			    		
			    	}
				}
					
				// read sub-application graphs
			    if (currentLine.contains(TASK_GRAPH)) {
			    	
			    	String functionName = (currentLine.replaceAll(AT, "")).replaceAll(WHITESPACE, CONNECT);
			    	
			    	List<String> taskGraph = new LinkedList<String>();
			    
			    	while (!isClosing(currentLine = br.readLine())) {
			    		taskGraph.add(currentLine);
			    	}
			    	addToApplication(functionName, taskGraph);
			    }			  
			}
			
			if(multicore) annotateApplication(tgffFile);
			return application;
			
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	// annotate application with WCET (from all possible resource types)
	// TODO
	private void annotateApplication (File tgffFile) {
		
		extractTypeInformation(tgffFile);
		
		for (Task task : application) {
			
			// annotate computational tasks with WCET
			if (task instanceof TgffTask) {
				TgffTask tgffTask = (TgffTask) task;
				tgffTask.setWCET(extractWCET(tgffTask));
			}
		}
	}
	
	private double extractWCET(TgffTask task) {
		
		String tgffType = task.getTgffType();
		double wcet = Double.MAX_VALUE;
		
		for (String resourceType : resources.keySet()) {
			
		}
		
		
		return wcet;
	}
	
    private void extractTypeInformation(File tgffFile) {
    	
    	if (resources == null) {
    		
    		resources = new HashMap<String, Map<String, String[]>>();
		
	    	try {
				BufferedReader br = new BufferedReader(new FileReader(tgffFile));
				
				String currentLine;
				while ((currentLine = br.readLine()) != null) {
					
					String type = extractType(currentLine);
				    if (type != null) {			    	
				    
				    	Map<String, String []> properties = new HashMap<String, String[]>();
				    	
				    	while (!isClosing(currentLine = br.readLine())) {
				    		
				    		if (!ignoreLine(currentLine)) {
				    			
				    			String [] elements = currentLine.split(WHITESPACE);
				    			String first = elements[0];
				    			
				    			properties.put(first == " " ? "resourceProperties" : first, elements);
				    			
				    			// also adds line with resource properties (price, buffered, max_freq, etc.)!
				    			//taskProperties.add(currentLine);
				    		}
				    	}
				    	resources.put(type, properties);
				    }			  
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
	}


    private boolean ignoreLine(String line) {

    	if (line.contains(COMMENT) || line.contains(AT)) {
    		return true;
    	}
		return false;
	}
    

    
    public Architecture<Resource, Link> getArchitecture (int xDim, int yDim, boolean random) {
    	
    	if (architecture == null) {
    		architecture = new Architecture<Resource, Link>();
    		
    		Collection<Resource> commRouters = new HashSet<Resource>();
    		
    		for (int x = 0; x < xDim; x++) {
    			for (int y = 0; y < yDim; y++) {
    				
    				String id = CONNECT + x + CONNECT + y;
    				
    				Resource c = new Resource("c" + id);
    				c.setType(COMM);
    				c.setAttribute(X,  x);
    				c.setAttribute(Y,  y);
    				architecture.addVertex(c);
    				commRouters.add(c);
    				
    				Resource r = new Resource("r" + id);
    				r.setType(COMP);
    				r.setAttribute(X,  x);
    				r.setAttribute(Y,  y);
    				// TODO resource type, abh. von random!
    				architecture.addVertex(r);
    				
    				architecture.addEdge(new Link("local" + id), r, c);
    			}
    		}
    		
    		interconnect(commRouters, xDim, yDim);
    		
    		
    		
    		
    		
    		
    		
    		
    	}
    	
    	return architecture;    	
    }

    private void interconnect(Collection<Resource> commRouters, int xDim, int yDim) {
		
    	for (Resource router : commRouters) {
    		int x = router.getAttribute(X);
    		int y = router.getAttribute(Y);
    		
    		// TODO
    	}
    	
	}


//	private Architecture<Resource> getArchitectureFromFile(String file) {
//		NocLoader loader = new NocLoader(file);
//		noc = loader.getNoc();
//		
//		setNocFilename(file);
//		mesh = noc.getWidth();
//		if (noc.getWidth() != noc.getHeight()) {
//			System.err.println("Height not equals width. Possible problem"); // FIXME
//		}
//
//		IArchitecture<IResource> architecture = new Architecture<IResource>();
//
//		INoCRouter.setBandWidth(InvasiveCaseStudyModule.getLinkBandwidth());
//		INoCRouter.setMaxSL(InvasiveCaseStudyModule.getMaxSL());
//		NocRouter globalNocRouter = new NocRouter(ROUTERNAME, 2);
//		ResourceType[] resourceTypeValues = ResourceType.values();
//		
//		
//		for (int i = 0; i < noc.getWidth(); i++) {
//			for (int j = 0; j < noc.getWidth(); j++) {
//
//				ECU ecu;
//				// translate from Conoc resources to opt4j
//				Unit unit = noc.getUnit(i, j);
//				assert unit.getResourceType() <= resourceTypeValues.length;
//				ResourceType type = resourceTypeValues[unit.getResourceType()];
//				
//				System.out.println("resourceType-values " + type);
//
//
//				ecu = new InvasiveResource(i + "," + j, type.name() + " " + i
//						+ " " + j, i, j, type); // 5.0,
//												// 100,
//												// 1,
//												// 1);
//
//				// ((InvasiveResource) ecu).setSchedulingInterval(5);
//				ecu.setAttribute("XCOORDINATE", i);
//				ecu.setAttribute("YCOORDINATE", j);
//				architecture.add(ecu);
//				ecus.add(ecu);
//				interconnect(architecture, ecu, globalNocRouter);
//
//			}
//		}
//		
//		return architecture;
//	}
//
//	@Override
//	public synchronized IMapping createMapping(IResource resource, ITask task) {
//		IMapping mapping = null;
//		if (resource instanceof InvasiveResource
//				&& task instanceof InvasiveTask) {
//
//			if (((InvasiveTask) task).getResourcetype().contains(
//					((InvasiveResource) resource).getResourceType())) {
//				mapping = new MyMapping(task, resource);
//			}
//		}
//		return mapping;
//	}
//
//	public synchronized Collection<IMapping> createMappings(IResource resource,
//			Collection<ITask> tasks) {
//		Collection<IMapping> mappings = new HashSet<IMapping>();
//		for (ITask task : tasks) {
//			if (task instanceof IFunction) {
//				IMapping mapping = createMapping(resource, task);
//				if (mapping != null) {
//					mappings.add(mapping);
//				}
//			}
//		}
//		return mappings;
//	}


	//*
	
	/**
	 * Add one "@TASK_GRAPH" block to the application 
	 * 
	 */
	private void addToApplication(String functionName, List<String> taskGraph) throws IOException {
		
		if (taskGraph.size() > 0) {
		
			double period;
	
			Iterator<String> it = taskGraph.iterator();
			
			String firstLine = it.next();
			
			assert firstLine.contains(tgffPERIOD) : "error in tgff-file: no period specified";
				
			period = Double.parseDouble(firstLine.replace(tgffPERIOD + WHITESPACE, ""));			
			
			while(it.hasNext()) {
				String line = it.next();
				
				if (line.contains(TASK)) {
					TgffTask task = getTgffTask(line, period);
					application.addVertex(task);
				}
				
				else if (line.contains(ARC)) {
					addCommunication(line, period);
				}
				
				else if (line.contains(HARD_DEADLINE)) {
					addDeadline(line, HARD_DEADLINE);
				}
				else if (line.contains(SOFT_DEADLINE)) {
					addDeadline(line, SOFT_DEADLINE); 
				}
			}
		}	
	}
	
	

    private void addDeadline(String line, String deadlineType) {

    	String [] elems = line.split(WHITESPACE);
    	assert elems.length == 6 : "tgff-file \"HARD_DEADLINE\": wrong number of entries in line";
    	
    	Task t = application.getVertex(elems[3]);
    	
    	if (t != null) {
    		t.setAttribute(deadlineType, Double.parseDouble(elems[5]));
    	}
	}



	private TgffTask getTgffTask(String line, double period) {

		String [] elems = line.split(WHITESPACE);
		
		assert elems.length == 4: "tgff-file \"TASK\": wrong number of entries in line";
		
		return new TgffTask(elems[1], elems[3], period);
	}
	
	private void addCommunication (String line, double period) {
		
		String [] elems = line.split(WHITESPACE);
		
		assert elems.length == 8: "tgff-file \"ARC\": wrong number of entries in line";
		
		String id = elems[1];
			
		TgffCommunication comm = new TgffCommunication(id, messageSizes.get(elems[7]), period);
		Task t1 = application.getVertex(elems[3]);
		Task t2 = application.getVertex(elems[5]);
		
		application.addVertex(comm);
		application.addEdge(new Dependency(id + "_0"), t1, comm);
		application.addEdge(new Dependency(id + "_1"), comm, t2);		
	}

	private boolean isClosing (String s) {
		return (s.contains(CLOSING) && !s.contains(COMMENT));
	}
	
	private String extractType(String s) {
		
		for (String elem : resourceTypes) {
			if (s.contains(elem)) {
				return elem;
			}
		}
		return null;
	}	
}
