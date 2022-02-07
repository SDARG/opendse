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

import static net.sf.opendse.io.Common.iterable;
import static net.sf.opendse.io.Common.setAttributes;
import static net.sf.opendse.io.Common.toInstance;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.MatchResult;

import edu.uci.ics.jung.graph.util.EdgeType;
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
import net.sf.opendse.model.parameter.ParameterRange;
import net.sf.opendse.model.parameter.ParameterRangeDiscrete;
import net.sf.opendse.model.parameter.ParameterSelect;
import net.sf.opendse.model.parameter.ParameterUniqueID;
import net.sf.opendse.model.parameter.Parameters;
import nu.xom.Elements;

/**
 * The {@code SpecificationReader} reads a {@code Specification} from an
 * {@code InputStream} or file.
 * 
 * @author Martin Lukasiewycz
 * 
 */
public class SpecificationReader {

	protected Map<String, Element> knownElements = new HashMap<String, Element>();
	protected final ClassDictionary classDict;

	/**
	 * Default constructor with the default class dictionary.
	 */
	public SpecificationReader() {
		this(new ClassDictionaryDefault());
	}

	/**
	 * Constructor to work with a custom class dictionary.
	 * 
	 * @param classDict the custom class dictionary
	 */
	public SpecificationReader(ClassDictionary classDict) {
		this.classDict = classDict;
	}

	/**
	 * Read specification from a file.
	 * 
	 * @param filename the file name
	 * @return the specification
	 */
	public Specification read(String filename) {
		return read(new File(filename));
	}

	/**
	 * Read specification from a file.
	 * 
	 * @param file the file
	 * @return the specification
	 */
	public Specification read(File file) {
		try {
			return read(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Read specification from an input stream.
	 * 
	 * @param in the input stream
	 * @return the specification
	 */
	public Specification read(InputStream in) {
		try {
			nu.xom.Builder parser = new nu.xom.Builder();
			nu.xom.Document doc = parser.build(in);

			nu.xom.Element eSpec = doc.getRootElement();

			return toSpecification(eSpec);
		} catch (Exception ex) {
			throw new IllegalArgumentException(ex);
		}
	}

	/**
	 * Convert an XML element to a specification
	 * 
	 * @param eSpecification the XML element
	 * @return the specification
	 */
	public Specification toSpecification(nu.xom.Element eSpecification) {
		try {
			nu.xom.Element eArchitecture = eSpecification.getChildElements("architecture", SpecificationWriter.NS)
					.get(0);
			nu.xom.Element eApplication = eSpecification.getChildElements("application", SpecificationWriter.NS).get(0);
			nu.xom.Element eMappings = eSpecification.getChildElements("mappings", SpecificationWriter.NS).get(0);

			Architecture<Resource, Link> architecture = toArchitecture(eArchitecture);
			Application<Task, Dependency> application = toApplication(eApplication);
			Mappings<Task, Resource> mappings = toMappings(eMappings, architecture, application);

			Specification specification = null;

			Elements routingElements = eSpecification.getChildElements("routings", SpecificationWriter.NS);
			if (routingElements != null && routingElements.size() > 0) {
				nu.xom.Element eRoutings = routingElements.get(0);
				Routings<Task, Resource, Link> routings = toRoutings(eRoutings, architecture, application);
				specification = new Specification(application, architecture, mappings, routings);
			} else {
				specification = new Specification(application, architecture, mappings);
			}

			Elements elements = eSpecification.getChildElements("attributes", SpecificationWriter.NS);
			if (elements.size() > 0) {
				nu.xom.Element eAttributes = elements.get(0);
				Attributes attributes = toAttributes(eAttributes);
				setAttributes(specification, attributes);
			}

			return specification;
		} catch (Exception ex) {
			throw new IllegalArgumentException(ex);
		}
	}

	protected Routings<Task, Resource, Link> toRoutings(nu.xom.Element eRoutings,
			Architecture<Resource, Link> architecture, Application<Task, Dependency> application)
			throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Routings<Task, Resource, Link> routings = new Routings<Task, Resource, Link>();

		nu.xom.Elements eRoutingList = eRoutings.getChildElements("routing", SpecificationWriter.NS);
		for (nu.xom.Element eRouting : iterable(eRoutingList)) {
			String sourceId = eRouting.getAttributeValue("source");
			Task source = application.getVertex(sourceId);

			Architecture<Resource, Link> routing = toRouting(eRouting, architecture, application);
			routings.set(source, routing);
		}

		return routings;
	}

	protected Architecture<Resource, Link> toRouting(nu.xom.Element eRouting, Architecture<Resource, Link> architecture,
			Application<Task, Dependency> application)
			throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Map<String, Resource> map = new HashMap<String, Resource>();
		Architecture<Resource, Link> routing = new Architecture<Resource, Link>();

		nu.xom.Elements eResources = eRouting.getChildElements("resource", SpecificationWriter.NS);
		for (nu.xom.Element eResource : iterable(eResources)) {
			Resource parent = architecture.getVertex(eResource.getAttributeValue("id"));
			Resource resource = toNode(eResource, parent);
			routing.addVertex(resource);
			map.put(resource.getId(), resource);
		}

		nu.xom.Elements eLinks = eRouting.getChildElements("link", SpecificationWriter.NS);
		for (nu.xom.Element eLink : iterable(eLinks)) {
			Link parent = architecture.getEdge(eLink.getAttributeValue("id"));
			Link link = toEdge(eLink, parent);

			String type = eLink.getAttributeValue("orientation");
			EdgeType edgeType = EdgeType.UNDIRECTED;
			if (type != null) {
				edgeType = EdgeType.valueOf(type);
			}

			Resource source = map.get(eLink.getAttributeValue("source"));
			Resource destination = map.get(eLink.getAttributeValue("destination"));

			routing.addEdge(link, source, destination, edgeType);
		}

		return routing;
	}

	protected Mappings<Task, Resource> toMappings(nu.xom.Element eMappings, Architecture<Resource, Link> architecture,
			Application<Task, Dependency> application)
			throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		Mappings<Task, Resource> mappings = new Mappings<Task, Resource>();

		nu.xom.Elements eMaps = eMappings.getChildElements("mapping", SpecificationWriter.NS);
		for (nu.xom.Element eMap : iterable(eMaps)) {
			String sourceId = eMap.getAttributeValue("source");
			String targetId = eMap.getAttributeValue("target");

			Task source = application.getVertex(sourceId);
			Resource target = architecture.getVertex(targetId);
			assert source != null : "Unknown task: " + sourceId;
			assert target != null : "Unknown resource: " + targetId;

			Mapping<Task, Resource> mapping = toMapping(eMap, source, target);
			mappings.add(mapping);
		}

		return mappings;
	}

	protected Application<Task, Dependency> toApplication(nu.xom.Element eApplication)
			throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		Application<Task, Dependency> application = new Application<Task, Dependency>();

		nu.xom.Elements eTasks = eApplication.getChildElements("task", SpecificationWriter.NS);
		for (nu.xom.Element eTask : iterable(eTasks)) {
			Task task = toNode(eTask, null);
			application.addVertex(task);
		}
		nu.xom.Elements eCommunications = eApplication.getChildElements("communication", SpecificationWriter.NS);
		for (nu.xom.Element eCommunication : iterable(eCommunications)) {
			Communication communication = toNode(eCommunication, null);
			application.addVertex(communication);
		}

		nu.xom.Elements eDependencies = eApplication.getChildElements("dependency", SpecificationWriter.NS);
		for (nu.xom.Element eDependency : iterable(eDependencies)) {
			parseDependency(eDependency, application);
		}

		nu.xom.Element eFunctions = eApplication.getFirstChildElement("functions", SpecificationWriter.NS);
		if (eFunctions != null) {
			nu.xom.Elements eFuncs = eFunctions.getChildElements("function", SpecificationWriter.NS);
			for (nu.xom.Element eFunc : iterable(eFuncs)) {
				Task task = application.getVertex(eFunc.getAttributeValue("anchor"));
				Function<Task, Dependency> function = application.getFunction(task);
				Attributes attributes = toAttributes(eFunc.getFirstChildElement("attributes", SpecificationWriter.NS));
				setAttributes(function, attributes);
			}
		}

		return application;
	}

	protected void parseDependency(nu.xom.Element eDependency, Application<Task, Dependency> application)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		Dependency dependency = toEdge(eDependency, null);

		String srcName = eDependency.getAttributeValue("source");
		Task source = application.getVertex(srcName);
		if (source == null) {
			throw new IllegalArgumentException("Source of dependency " + srcName + " not found: " + srcName);
		}

		String dstName = eDependency.getAttributeValue("destination");
		Task destination = application.getVertex(dstName);
		if (destination == null) {
			throw new IllegalArgumentException("Destination of dependency " + dependency + " not found: " + dstName);
		}

		application.addEdge(dependency, source, destination, EdgeType.DIRECTED);
	}

	protected Architecture<Resource, Link> toArchitecture(nu.xom.Element eArch)
			throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		Architecture<Resource, Link> architecture = new Architecture<Resource, Link>();

		nu.xom.Elements eResources = eArch.getChildElements("resource", SpecificationWriter.NS);
		for (nu.xom.Element eResource : iterable(eResources)) {
			Resource resource = toNode(eResource, null);
			architecture.addVertex(resource);
		}

		nu.xom.Elements eLinks = eArch.getChildElements("link", SpecificationWriter.NS);
		for (nu.xom.Element eLink : iterable(eLinks)) {
			parseLink(eLink, architecture);
		}

		return architecture;
	}

	protected void parseLink(nu.xom.Element eLink, Architecture<Resource, Link> architecture)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		Link link = toEdge(eLink, null);

		String type = eLink.getAttributeValue("orientation");
		EdgeType edgeType = EdgeType.UNDIRECTED;
		if (type != null) {
			edgeType = EdgeType.valueOf(type);
		}

		String srcName = eLink.getAttributeValue("source");
		Resource source = architecture.getVertex(srcName);
		if (source == null) {
			throw new IllegalArgumentException("Source of link " + link + " not found: " + srcName);
		}

		String dstName = eLink.getAttributeValue("destination");
		Resource destination = architecture.getVertex(dstName);
		if (destination == null) {
			throw new IllegalArgumentException("Destination of link " + link + " not found: " + dstName);
		}

		architecture.addEdge(link, source, destination, edgeType);
	}

	@SuppressWarnings("unchecked")
	protected <C> Class<C> getClass(nu.xom.Element eElement) throws ClassNotFoundException {
		Class<C> type = null;
		if (eElement.getAttribute("class") != null) {
			type = (Class<C>) Class.forName(eElement.getAttributeValue("class"));
		} else {
			type = (Class<C>) classDict.getClass(eElement.getLocalName());
		}
		if (type == null) {
			throw new RuntimeException("Unknown node type for " + eElement);
		}
		return type;
	}

	protected Class<?> getClass(String name) throws ClassNotFoundException {
		if (classDict.hasClassName(name)) {
			return classDict.getClass(name);
		} else {
			return Class.forName(name);
		}
	}

	@SuppressWarnings("unchecked")
	protected <N extends Node> N toNode(nu.xom.Element eNode, N parent)
			throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		Class<N> type = getClass(eNode);

		N node = null;

		if (parent == null) {
			String id = eNode.getAttributeValue("id");
			if (knownElements.containsKey(id)) {
				node = (N) knownElements.get(id);
			} else {
				node = type.getConstructor(String.class).newInstance(id);
				knownElements.put(node.getId(), node);
			}
		} else {
			node = type.getConstructor(Element.class).newInstance(parent);
		}

		nu.xom.Elements eAttributes = eNode.getChildElements("attributes", SpecificationWriter.NS);
		if (eAttributes.size() > 0) {
			Attributes attributes = toAttributes(eAttributes.get(0));
			setAttributes(node, attributes);
		}

		return node;

	}

	protected <E extends Edge> E toEdge(nu.xom.Element eEdge, E parent)
			throws ClassNotFoundException, IllegalArgumentException, SecurityException, InstantiationException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Class<E> type = getClass(eEdge);

		E edge = null;

		if (parent == null) {
			String id = eEdge.getAttributeValue("id");
			edge = type.getConstructor(String.class).newInstance(id);
		} else {
			edge = type.getConstructor(Element.class).newInstance(parent);
		}

		nu.xom.Elements eAttributes = eEdge.getChildElements("attributes", SpecificationWriter.NS);
		if (eAttributes.size() > 0) {
			Attributes attributes = toAttributes(eAttributes.get(0));
			setAttributes(edge, attributes);
		}

		return edge;
	}

	protected <M extends Mapping<?, ?>> M toMapping(nu.xom.Element eMapping, Task source, Resource target)
			throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		Class<M> type = getClass(eMapping);

		M node = null;

		String id = eMapping.getAttributeValue("id");
		node = type.getConstructor(String.class, Task.class, Resource.class).newInstance(id, source, target);

		nu.xom.Elements eAttributes = eMapping.getChildElements("attributes", SpecificationWriter.NS);
		if (eAttributes.size() > 0) {
			Attributes attributes = toAttributes(eAttributes.get(0));
			setAttributes(node, attributes);
		}

		return node;

	}

	protected Attributes toAttributes(nu.xom.Element eAttributes)
			throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		Attributes attributes = new Attributes();

		nu.xom.Elements eAttributeList = eAttributes.getChildElements("attribute", SpecificationWriter.NS);

		for (nu.xom.Element element : iterable(eAttributeList)) {
			String name = element.getAttributeValue("name");
			Object value = toAttribute(element);

			attributes.put(name, value);
		}

		return attributes;
	}

	protected Object toAttribute(nu.xom.Element eAttribute)
			throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		String parameter = eAttribute.getAttributeValue("parameter");
		String type = eAttribute.getAttributeValue("type");
		String value = eAttribute.getValue();

		if (type == null) {
			throw new IllegalArgumentException("no type given for attribute " + eAttribute);
		}
		if (parameter != null) {

			if (parameter.equals("RANGE")) {
				return getRange(value);
			} else if (parameter.equals("DISCRETERANGE")) {
				return getRangeInt(value);
			} else if (parameter.equals("SELECT")) {
				return getSelectRefList(type, value);
			} else if (parameter.equals("UID")) {
				return getUniqueID(value);
			} else {
				throw new IllegalArgumentException("Unknown parameter type: " + parameter);
			}
		} else {
			Class<?> clazz = null;
			try {
				clazz = getClass(type);
			} catch (ClassNotFoundException e) {
				System.err.println("Class " + type + " not found. Ignoring attribute value " + value);
				return null;
			}
			if (Collection.class.isAssignableFrom(clazz)) {
				return toAttributeCollection(eAttribute, clazz);
			} else {
				return toAttributeObject(value, clazz);
			}
		}
	}

	/**
	 * Constructs an attribute collection that contains all passed elements and
	 * their corresponding class.
	 * 
	 * @param eAttribute the attribute element to add the collection to
	 * @param clazz      the class of the objects that are to create
	 * @return the constructed collection
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Object toAttributeCollection(nu.xom.Element eAttribute, Class<?> clazz) throws InstantiationException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		Collection collectionAttribute = (Collection) clazz.getConstructor().newInstance();
		for (nu.xom.Element childElement : iterable(eAttribute.getChildElements())) {
			Object actualEntry = toAttribute(childElement);
			collectionAttribute.add(actualEntry);
		}
		return collectionAttribute;
	}

	/**
	 * Constructs an instance of the passed class that contains the passed value.
	 *
	 * @param value the value of the object that is to create
	 * @param clazz the class of the object that is to create
	 * @return the constructed object
	 */
	protected Object toAttributeObject(String value, Class<?> clazz) throws InstantiationException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		Object object = null;

		if (Element.class.isAssignableFrom(clazz) && knownElements.containsKey(value)) {
			object = knownElements.get(value);
		} else {
			object = toInstance(value, clazz);
			if (object instanceof Element) {
				knownElements.put(value, (Element) object);
			}

		}

		// "fallback procedure"
		if (object == null && clazz.equals(Serializable.class)) {
			try {
				object = Common.fromString(value);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		return object;
	}

	/**
	 * Parse the {@link ParameterRange}.
	 * 
	 * @param value the string to parse
	 * @return the corresponding parameter
	 */
	protected ParameterRange getRange(String value) {
		Scanner scanner = new Scanner(value);
		scanner.useDelimiter("[\\s+,()]+");

		double v = new Double(scanner.next());
		double lb = new Double(scanner.next());
		double ub = new Double(scanner.next());
		double gr = new Double(scanner.next());

		scanner.close();

		return Parameters.range(v, lb, ub, gr);
	}

	/**
	 * Parse the {@link ParameterRangeDiscrete}.
	 * 
	 * @param value the string to parse
	 * @return the corresponding parameter
	 */
	protected ParameterRangeDiscrete getRangeInt(String value) {
		Scanner scanner = new Scanner(value);
		scanner.useDelimiter("[\\s+,()]+");

		int v = new Integer(scanner.next());
		int lb = new Integer(scanner.next());
		int ub = new Integer(scanner.next());

		scanner.close();

		return Parameters.range(v, lb, ub);
	}

	/**
	 * Parse the {@link ParameterSelect}.
	 * 
	 * @param value the string to parse
	 * @return the corresponding parameter
	 */
	protected ParameterSelect getSelectRefList(String type, String value) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		value = value.replace("[", "(").replace("]", ")");

		Scanner scanner = new Scanner(value);
		scanner.useDelimiter("[()]+");

		Class<?> clazz = getClass(type);

		Object def = toInstance(scanner.next(), clazz);
		List<Object> select = new ArrayList<Object>();
		for (String part : scanner.next().split(",")) {
			select.add(toInstance(part, clazz));
		}

		String reference = null;
		if (scanner.hasNext()) {
			String next = scanner.next().trim();
			if (!next.equals("")) {
				reference = next;
			}
		}

		scanner.close();

		return Parameters.selectRefList(reference, def, select);
	}

	/**
	 * Parse the {@link ParameterUniqueID}.
	 * 
	 * @param value the string to parse
	 * @return the corresponding parameter
	 */
	protected ParameterUniqueID getUniqueID(String value) {
		Scanner scanner = new Scanner(value);
		scanner.findInLine("(\\w+) \\[UID:(\\w+)\\]");

		MatchResult result = scanner.match();
		int def = new Integer(result.group(1));
		String identifier = result.group(2);

		scanner.close();

		return Parameters.uniqueID(def, identifier);
	}
}
