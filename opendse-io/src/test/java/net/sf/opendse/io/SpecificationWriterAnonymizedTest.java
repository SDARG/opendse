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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.sf.opendse.model.Application;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;

public class SpecificationWriterAnonymizedTest {

	@Test
	public void anonymizeApplicationTest() {

		Task t1 = new Task("t1");
		Task t2 = new Task("t2");

		t1.setAttribute("cost", 25);
		t2.setAttribute("cost", 25);

		Dependency dependency = new Dependency("dep");
		dependency.setAttribute("test", 0);

		Application<Task, Dependency> app = new Application<Task, Dependency>();
		app.addVertex(t1);
		app.addVertex(t2);
		app.addEdge(dependency, t1, t2);

		SpecificationWriterAnonymized<Specification> anonymizer = new SpecificationWriterAnonymized<Specification>();
		Application<Task, Dependency> anonymized = anonymizer.anonymizeApplication(app, new HashMap<Task, Task>());

		Assertions.assertNotNull(anonymized.getVertex("task0"));
		Assertions.assertNotNull(anonymized.getVertex("task1"));
		Assertions.assertNotNull(anonymized.getEdge("dependency0"));

		Task anonymizedTask = anonymized.getVertex("task0");
		Assertions.assertEquals((int) anonymizedTask.getAttribute("cost"), 25);

		Dependency anonymizedDependency = anonymized.getEdge("dependency0");
		Assertions.assertEquals((int) anonymizedDependency.getAttribute("test"), 0);

		Assertions.assertNull(anonymized.getVertex("t1"));
		Assertions.assertNull(anonymized.getVertex("t2"));
		Assertions.assertNull(anonymized.getEdge("dep"));
	}

	@Test
	public void anonymizeArchitectureTest() {

		Resource t1 = new Resource("r1");
		Resource t2 = new Resource("r2");

		t1.setAttribute("cost", 25);
		t2.setAttribute("cost", 25);

		Link link = new Link("link");
		link.setAttribute("test", 0);

		Architecture<Resource, Link> architecture = new Architecture<Resource, Link>();
		architecture.addVertex(t1);
		architecture.addVertex(t2);
		architecture.addEdge(link, t1, t2);

		SpecificationWriterAnonymized<Specification> anonymizer = new SpecificationWriterAnonymized<Specification>();
		Architecture<Resource, Link> anonymized = anonymizer.anonymizeArchitecture(architecture,
				new HashMap<Resource, Resource>());

		Assertions.assertNotNull(anonymized.getVertex("resource0"));
		Assertions.assertNotNull(anonymized.getVertex("resource1"));
		Assertions.assertNotNull(anonymized.getEdge("link0"));

		Resource anonymizedResource = anonymized.getVertex("resource0");
		Assertions.assertEquals((int) anonymizedResource.getAttribute("cost"), 25);

		Link anonymizedLink = anonymized.getEdge("link0");
		Assertions.assertEquals((int) anonymizedLink.getAttribute("test"), 0);

		Assertions.assertNull(anonymized.getVertex("r1"));
		Assertions.assertNull(anonymized.getVertex("r2"));
		Assertions.assertNull(anonymized.getEdge("link"));
	}

	@Test
	public void anonymizeMappingsTest() {

		Task t1 = new Task("t1");
		Task t2 = new Task("t2");

		Resource r1 = new Resource("r1");
		Resource r2 = new Resource("r2");

		Mappings<Task, Resource> mappings = new Mappings<Task, Resource>();
		Mapping<Task, Resource> m1 = new Mapping<Task, Resource>("m1", t1, r1);
		Mapping<Task, Resource> m2 = new Mapping<Task, Resource>("m2", t1, r2);
		Mapping<Task, Resource> m3 = new Mapping<Task, Resource>("m3", t2, r1);

		m1.setAttribute("test", 0);

		mappings.add(m1);
		mappings.add(m2);
		mappings.add(m3);

		Map<Task, Task> taskMap = new HashMap<Task, Task>();
		taskMap.put(t1, new Task("task1"));
		taskMap.put(t2, new Task("task2"));

		Map<Resource, Resource> resourceMap = new HashMap<Resource, Resource>();
		resourceMap.put(r1, new Resource("resource1"));
		resourceMap.put(r2, new Resource("resource2"));

		SpecificationWriterAnonymized<Specification> anonymizer = new SpecificationWriterAnonymized<Specification>();
		Mappings<Task, Resource> anonymized = anonymizer.anonymizeMappings(mappings, taskMap, resourceMap);

		Set<Mapping<Task, Resource>> anonymizedMappings = anonymized.getAll();
		Assertions.assertNotNull(anonymizedMappings);
		Assertions.assertEquals(anonymizedMappings.size(), 3);

		Assertions.assertFalse(anonymizedMappings.contains(m1));
		Assertions.assertFalse(anonymizedMappings.contains(m2));
		Assertions.assertFalse(anonymizedMappings.contains(m3));
	}
}
