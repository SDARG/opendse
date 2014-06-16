/**
 * OpenDSE is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * OpenDSE is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with OpenDSE. If not, see http://www.gnu.org/licenses/.
 */
package net.sf.opendse.io;

import static net.sf.opendse.io.Common.getType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collection;

import net.sf.opendse.model.Application;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Attributes;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Edge;
import net.sf.opendse.model.Function;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Models;
import net.sf.opendse.model.Node;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Routings;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.parameter.Parameter;
import nu.xom.Serializer;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * The {@code SpecificationWriter} write a {@code Specification} to an
 * {@code OutputStream} or {@code File}.
 * 
 * @author Martin Lukasiewycz
 * 
 */
public class SpecificationWriter {

	/**
	 * Write the specification to a file.
	 * 
	 * @param specification
	 *            the specification
	 * @param filename
	 *            the name of the target file
	 */
	public void write(Specification specification, String filename) {
		write(specification, new File(filename));
	}

	/**
	 * Write the specification to a file.
	 * 
	 * @param specification
	 *            the specification
	 * @param file
	 *            the file
	 */
	public void write(Specification specification, File file) {
		try {
			FileOutputStream out = new FileOutputStream(file);
			write(specification, out);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Write the specification to an output stream.
	 * 
	 * @param specification
	 *            the specification
	 * @param out
	 *            the output stream
	 */
	public void write(Specification specification, OutputStream out) {

		nu.xom.Element eSpec = toElement(specification);
		nu.xom.Document doc = new nu.xom.Document(eSpec);

		try {
			Serializer serializer = new Serializer(out);
			serializer.setIndent(2);
			serializer.setMaxLength(2000);
			serializer.write(doc);
			serializer.flush();
		} catch (IOException ex) {
			System.out.println(ex + " " + out);
		}
	}

	/**
	 * Write a collection of specifications to an output stream.
	 * 
	 * @param specifications
	 *            the specifications
	 * @param out
	 *            the output stream
	 */
	public void write(Collection<Specification> specifications, OutputStream out) {

		nu.xom.Element eSpecs = new nu.xom.Element("specifications");

		for (Specification spec : specifications) {
			nu.xom.Element eSpec = toElement(spec);
			eSpecs.appendChild(eSpec);
		}
		nu.xom.Document doc = new nu.xom.Document(eSpecs);

		try {
			Serializer serializer = new Serializer(out);
			serializer.setIndent(2);
			serializer.setMaxLength(2000);
			serializer.write(doc);
			serializer.flush();
		} catch (IOException ex) {
			System.out.println(ex + " " + out);
		}
	}

	/**
	 * Transform a specification to an XML element.
	 * 
	 * @param specification
	 *            the specification
	 * @return the XML element
	 */
	public nu.xom.Element toElement(Specification specification) {
		nu.xom.Element eSpec = new nu.xom.Element("specification");
		eSpec.appendChild(toElement(specification.getArchitecture()));
		eSpec.appendChild(toElement(specification.getApplication()));
		eSpec.appendChild(toElement(specification.getMappings()));
		eSpec.appendChild(toElement(specification.getRoutings(), specification.getArchitecture()));

		if (specification.getAttributes().size() > 0) {
			eSpec.appendChild(toElement(specification.getAttributes()));
		}

		return eSpec;
	}

	protected nu.xom.Element toElement(Routings<Task, Resource, Link> routings,
			Architecture<Resource, Link> architecture) {
		nu.xom.Element eRoutings = new nu.xom.Element("routings");

		for (Task task : routings.getTasks()) {
			nu.xom.Element eRouting = toElement(routings.get(task), architecture);
			eRouting.setLocalName("routing");
			eRouting.addAttribute(new nu.xom.Attribute("source", task.getId()));
			eRoutings.appendChild(eRouting);
		}

		return eRoutings;
	}

	protected nu.xom.Element toElement(Architecture<Resource, Link> routing, Architecture<Resource, Link> architecture) {
		nu.xom.Element eArch = new nu.xom.Element("routing");

		for (Resource resource : routing) {
			nu.xom.Element element = toElement(resource, "resource", true);
			if (resource.getParent() != architecture.getVertex(resource.getId())) {
				element.removeChildren();
			}
			eArch.appendChild(element);
		}
		for (Link link : routing.getEdges()) {
			Pair<Resource> endpoints = routing.getEndpoints(link);
			nu.xom.Element element = toElement(link, "link", endpoints.getFirst(), endpoints.getSecond(),
					routing.getEdgeType(link), true);
			if (link.getParent() != architecture.getVertex(link.getId())) {
				element.removeChildren();
			}
			eArch.appendChild(element);
		}

		return eArch;
	}

	protected nu.xom.Element toElement(Architecture<Resource, Link> architecture) {
		nu.xom.Element eArch = new nu.xom.Element("architecture");

		for (Resource resource : architecture) {
			eArch.appendChild(toElement(resource, "resource", false));
		}
		for (Link link : architecture.getEdges()) {
			Pair<Resource> endpoints = architecture.getEndpoints(link);
			eArch.appendChild(toElement(link, "link", endpoints.getFirst(), endpoints.getSecond(),
					architecture.getEdgeType(link), false));
		}

		return eArch;
	}

	protected nu.xom.Element toElement(Application<Task, Dependency> application) {
		nu.xom.Element eArch = new nu.xom.Element("application");

		for (Task task : application) {
			if (Models.isProcess(task)) {
				eArch.appendChild(toElement(task, "task", false));
			} else {
				eArch.appendChild(toElement(task, "communication", false));
			}

		}
		for (Dependency dependency : application.getEdges()) {
			Pair<Task> endpoints = application.getEndpoints(dependency);
			eArch.appendChild(toElement(dependency, "dependency", endpoints.getFirst(), endpoints.getSecond(),
					application.getEdgeType(dependency), false));
		}
		nu.xom.Element eFunctions = new nu.xom.Element("functions");
		eArch.appendChild(eFunctions);
		for (Function<Task, Dependency> function : application.getFunctions()) {
			eFunctions.appendChild(toElement(function));
		}

		return eArch;
	}

	protected nu.xom.Element toElement(Function<Task, Dependency> function) {
		nu.xom.Element eFunction = new nu.xom.Element("function");

		Task t = function.getVertices().iterator().next();
		eFunction.addAttribute(new nu.xom.Attribute("anchor", t.getId()));

		eFunction.appendChild(toElement(function.getAttributes()));
		return eFunction;
	}

	protected nu.xom.Element toElement(Mappings<Task, Resource> mappings) {
		nu.xom.Element eMappings = new nu.xom.Element("mappings");
		for (Mapping<Task, Resource> mapping : mappings) {
			eMappings.appendChild(toElement(mapping));
		}

		return eMappings;
	}

	protected nu.xom.Element toElement(Mapping<Task, Resource> mapping) {
		nu.xom.Element eMapping = new nu.xom.Element("mapping");
		eMapping.addAttribute(new nu.xom.Attribute("id", mapping.getId()));
		if (!getType(mapping.getClass()).equals("mapping")) {
			eMapping.addAttribute(new nu.xom.Attribute("class", getType(mapping.getClass())));
		}
		eMapping.addAttribute(new nu.xom.Attribute("source", mapping.getSource().getId()));
		eMapping.addAttribute(new nu.xom.Attribute("target", mapping.getTarget().getId()));
		nu.xom.Element eAttributes = toElement(mapping.getAttributes());
		if (eAttributes.getChildCount() > 0) {
			eMapping.appendChild(eAttributes);
		}
		return eMapping;
	}

	protected nu.xom.Element toElement(Node node, String name, boolean local) {
		nu.xom.Element eElem = new nu.xom.Element(name);
		eElem.addAttribute(new nu.xom.Attribute("id", node.getId()));
		if (!getType(node.getClass()).equals(name)) {
			eElem.addAttribute(new nu.xom.Attribute("class", getType(node.getClass())));
		}
		nu.xom.Element eAttributes = toElement(local ? node.getLocalAttributes() : node.getAttributes());
		if (eAttributes.getChildCount() > 0) {
			eElem.appendChild(eAttributes);
		}
		return eElem;
	}

	protected nu.xom.Element toElement(Edge edge, String name, Node source, Node dest, EdgeType edgeType, boolean local) {
		nu.xom.Element eElem = new nu.xom.Element(name);
		eElem.addAttribute(new nu.xom.Attribute("id", edge.getId()));
		if (!getType(edge.getClass()).equals(name)) {
			eElem.addAttribute(new nu.xom.Attribute("class", getType(edge.getClass())));
		}
		eElem.addAttribute(new nu.xom.Attribute("source", source.getId()));
		eElem.addAttribute(new nu.xom.Attribute("destination", dest.getId()));
		eElem.addAttribute(new nu.xom.Attribute("type", edgeType.toString()));
		nu.xom.Element eAttributes = toElement(local ? edge.getLocalAttributes() : edge.getAttributes());
		if (eAttributes.getChildCount() > 0) {
			eElem.appendChild(eAttributes);
		}
		return eElem;
	}

	protected nu.xom.Element toElement(Attributes attributes) {
		nu.xom.Element eAttributes = new nu.xom.Element("attributes");

		for (String attributeName : attributes.getAttributeNames()) {
			nu.xom.Element eAttr = new nu.xom.Element("attribute");
			eAttr.addAttribute(new nu.xom.Attribute("name", attributeName));

			Object attribute = attributes.getAttribute(attributeName);
			if (attribute != null) {
				Class<?> cls = attribute.getClass();

				if (attributes.isParameter(attributeName)) {

					Parameter parameter = attributes.getAttributeParameter(attributeName);
					eAttr.appendChild(parameter.toString());
					eAttr.addAttribute(new nu.xom.Attribute("type", getType(cls)));
					eAttr.addAttribute(new nu.xom.Attribute("parameter", getType(parameter.getClass())));

				} else if (Common.isPrimitive(cls) || cls.equals(String.class)) {

					eAttr.addAttribute(new nu.xom.Attribute("type", getType(cls)));
					eAttr.appendChild(attributes.getAttribute(attributeName).toString());

				} else if (attributes instanceof Serializable) {

					Serializable s = (Serializable) attribute;
					eAttr.addAttribute(new nu.xom.Attribute("type", Serializable.class.getName()));
					try {
						eAttr.appendChild(Common.toString(s));
					} catch (IOException e) {
						e.printStackTrace();
					}

				} else {
					System.err.println("Failed to write attribute " + attribute);
				}

				eAttributes.appendChild(eAttr);
			}
		}

		return eAttributes;
	}

}
