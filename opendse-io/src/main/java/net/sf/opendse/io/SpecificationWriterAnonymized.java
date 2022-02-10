/*******************************************************************************
 * Copyright (c) 2015 OpenDSE
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
package net.sf.opendse.io;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;
import net.sf.opendse.model.Application;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Routings;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;

/**
 * The {@code SpecificationWriterAnonymized} writes an anonymized
 * {@code Specification} to a {@code File}. The anonymization covers the IDs of
 * {@code Task}, {@code Dependency}, {@code Resource}, {@code Link} and
 * {@code Mapping}. Note that anonymization keeps all other {@code Attributes}
 * as given in the {@code Specification}.
 *
 * 
 * @author Valentina Richthammer
 *
 */
public class SpecificationWriterAnonymized<S extends Specification> extends SpecificationWriter
		implements Transformer<S, S> {

	/**
	 * Constructs a new {@link SpecificationWriterAnonymized} that will always
	 * export {@link Routings}.
	 */
	public SpecificationWriterAnonymized() {
		super(true, new ClassDictionaryDefault());
	}

	/**
	 * Write the specification to a file.
	 *
	 * @param specification the specification
	 * @param filename      the name of the target file
	 */
	public void writeAnonymized(S specification, String filename) {

		Specification anonymizedSpec = transform(specification);
		write(anonymizedSpec, new File(filename));
	}

	/**
	 * Write the specification to a file.
	 *
	 * @param specification the specification
	 * @param file          the target file
	 */
	public void writeAnonymized(S specification, File file) {

		Specification anonymizedSpec = transform(specification);
		write(anonymizedSpec, file);
	}

	/**
	 * Transforms a {@code Specification} by anonymizing its ids.
	 */
	@Override
	public S transform(S specification) {

		Map<Task, Task> taskMap = new HashMap<Task, Task>();
		Map<Resource, Resource> resourceMap = new HashMap<Resource, Resource>();

		Application<Task, Dependency> anonymizedApp = anonymizeApplication(specification.getApplication(), taskMap);
		Architecture<Resource, Link> anonymizedArch = anonymizeArchitecture(specification.getArchitecture(),
				resourceMap);
		Mappings<Task, Resource> anonymizedMappings = anonymizeMappings(specification.getMappings(), taskMap,
				resourceMap);

		return (S) new Specification(anonymizedApp, anonymizedArch, anonymizedMappings);
	}

	/**
	 * Anonymizes the ids of an {@code Application}.
	 * 
	 * @param app     the application
	 * @param taskMap {@code Task} map to store anonymized ids (for anonymizing the
	 *                {@code Mappings})
	 * @return the anonymized {@code Application}
	 */
	protected Application<Task, Dependency> anonymizeApplication(Application<Task, Dependency> app,
			Map<Task, Task> taskMap) {

		Application<Task, Dependency> anonymizedApp = new Application<Task, Dependency>();

		List<Task> taskSet = new ArrayList<Task>(app.getVertices());
		Collections.shuffle(taskSet);

		// copy tasks
		int index = 0;
		for (Task task : taskSet) {
			Task anonymizedTask = new Task("task" + index);

			// copy attributes
			for (Entry<String, Object> attribute : task.getAttributes().entrySet()) {
				anonymizedTask.setAttribute(attribute.getKey(), attribute.getValue());
			}

			// copy local attributes
			for (Entry<String, Object> attribute : task.getLocalAttributes().entrySet()) {
				anonymizedTask.setAttribute(attribute.getKey(), attribute.getValue());
			}

			anonymizedApp.addVertex(anonymizedTask);
			taskMap.put(task, anonymizedTask);

			index++;
		}

		// copy dependencies
		List<Dependency> dependencies = new ArrayList<Dependency>(app.getEdges());
		Collections.shuffle(dependencies);

		index = 0;
		for (Dependency dependency : app.getEdges()) {
			Dependency anonymizedEdge = new Dependency("dependency" + index);

			// copy attributes
			for (Entry<String, Object> attribute : dependency.getAttributes().entrySet()) {
				anonymizedEdge.setAttribute(attribute.getKey(), attribute.getValue());
			}

			// copy local attributes
			for (Entry<String, Object> attribute : dependency.getLocalAttributes().entrySet()) {
				anonymizedEdge.setAttribute(attribute.getKey(), attribute.getValue());
			}

			Task origSource = app.getSource(dependency);
			Task origDest = app.getDest(dependency);

			EdgeType type = app.getEdgeType(dependency);

			anonymizedApp.addEdge(anonymizedEdge, taskMap.get(origSource), taskMap.get(origDest), type);
			index++;
		}
		return anonymizedApp;
	}

	/**
	 * Anonymizes the ids of an {@code Architecture}.
	 * 
	 * @param architecture the architecture
	 * @param resourceMap  {@code Resource} map to store anonymized ids (for
	 *                     anonymizing the {@code Mappings})
	 * @return the anonymized {@code Architecture}
	 */
	protected Architecture<Resource, Link> anonymizeArchitecture(Architecture<Resource, Link> architecture,
			Map<Resource, Resource> resourceMap) {

		Architecture<Resource, Link> anonymizedArchitecture = new Architecture<Resource, Link>();

		List<Resource> resourceSet = new ArrayList<Resource>(architecture.getVertices());
		Collections.shuffle(resourceSet);

		// copy resources
		int index = 0;
		for (Resource resource : resourceSet) {
			Resource anonymizedResource = new Resource("resource" + index);

			// copy attributes
			for (Entry<String, Object> attribute : resource.getAttributes().entrySet()) {
				anonymizedResource.setAttribute(attribute.getKey(), attribute.getValue());
			}

			// copy local attributes
			for (Entry<String, Object> attribute : resource.getLocalAttributes().entrySet()) {
				anonymizedResource.setAttribute(attribute.getKey(), attribute.getValue());
			}

			anonymizedArchitecture.addVertex(anonymizedResource);
			resourceMap.put(resource, anonymizedResource);

			index++;
		}

		// copy dependencies
		List<Link> links = new ArrayList<Link>(architecture.getEdges());
		Collections.shuffle(links);

		index = 0;
		for (Link link : architecture.getEdges()) {

			Link anonymizedLink = new Link("link" + index);

			// copy attributes
			for (Entry<String, Object> attribute : link.getAttributes().entrySet()) {
				anonymizedLink.setAttribute(attribute.getKey(), attribute.getValue());
			}

			// copy local attributes
			for (Entry<String, Object> attribute : link.getLocalAttributes().entrySet()) {
				anonymizedLink.setAttribute(attribute.getKey(), attribute.getValue());
			}

			Pair<Resource> endPoints = architecture.getEndpoints(link);

			Resource origSource = endPoints.getFirst();
			Resource origDest = endPoints.getSecond();

			EdgeType type = architecture.getEdgeType(link);

			anonymizedArchitecture.addEdge(anonymizedLink, resourceMap.get(origSource), resourceMap.get(origDest),
					type);
			index++;
		}
		return anonymizedArchitecture;
	}

	/**
	 * /** Anonymizes the ids of the {@code Mappings}.
	 * 
	 * @param mappings    the mappings
	 * @param taskMap     {@code Task} map storing anonymized ids
	 * @param resourceMap {@code Resource} map storing anonymized ids
	 * @return the anonymized {@code Mappings}
	 */
	protected Mappings<Task, Resource> anonymizeMappings(Mappings<Task, Resource> mappings, Map<Task, Task> taskMap,
			Map<Resource, Resource> resourceMap) {

		Mappings<Task, Resource> anonymizedMappings = new Mappings<Task, Resource>();

		List<Mapping<Task, Resource>> mappingSet = new ArrayList<Mapping<Task, Resource>>(mappings.getAll());
		Collections.shuffle(mappingSet);

		// copy mappings
		int index = 0;
		for (Mapping<Task, Resource> mapping : mappingSet) {

			Task origTask = mapping.getSource();
			Resource origResource = mapping.getTarget();

			Mapping<Task, Resource> anonymizedMapping = new Mapping<Task, Resource>("mapping" + index,
					taskMap.get(origTask), resourceMap.get(origResource));

			// copy attributes
			for (Entry<String, Object> attribute : mapping.getAttributes().entrySet()) {
				anonymizedMapping.setAttribute(attribute.getKey(), attribute.getValue());
			}

			// copy local attributes
			for (Entry<String, Object> attribute : mapping.getLocalAttributes().entrySet()) {
				anonymizedMapping.setAttribute(attribute.getKey(), attribute.getValue());
			}

			anonymizedMappings.add(anonymizedMapping);
			index++;
		}
		return anonymizedMappings;
	}
}
