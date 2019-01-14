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

import org.junit.Assert;
import org.junit.Test;

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

		Assert.assertNotNull(anonymized.getVertex("task0"));
		Assert.assertNotNull(anonymized.getVertex("task1"));
		Assert.assertNotNull(anonymized.getEdge("dependency0"));

		Task anonymizedTask = anonymized.getVertex("task0");
		Assert.assertEquals(anonymizedTask.getAttribute("cost"), 25);

		Dependency anonymizedDependency = anonymized.getEdge("dependency0");
		Assert.assertEquals(anonymizedDependency.getAttribute("test"), 0);

		Assert.assertNull(anonymized.getVertex("t1"));
		Assert.assertNull(anonymized.getVertex("t2"));
		Assert.assertNull(anonymized.getEdge("dep"));
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

		Assert.assertNotNull(anonymized.getVertex("resource0"));
		Assert.assertNotNull(anonymized.getVertex("resource1"));
		Assert.assertNotNull(anonymized.getEdge("link0"));

		Resource anonymizedResource = anonymized.getVertex("resource0");
		Assert.assertEquals(anonymizedResource.getAttribute("cost"), 25);

		Link anonymizedLink = anonymized.getEdge("link0");
		Assert.assertEquals(anonymizedLink.getAttribute("test"), 0);

		Assert.assertNull(anonymized.getVertex("r1"));
		Assert.assertNull(anonymized.getVertex("r2"));
		Assert.assertNull(anonymized.getEdge("link"));
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
		Assert.assertNotNull(anonymizedMappings);
		Assert.assertEquals(anonymizedMappings.size(), 3);

		Assert.assertFalse(anonymizedMappings.contains(m1));
		Assert.assertFalse(anonymizedMappings.contains(m2));
		Assert.assertFalse(anonymizedMappings.contains(m3));

	}

	// @Test
	// public void transformTest() {
	//
	// Resource r1 = new Resource("t1");
	// Resource r2 = new Resource("t2");
	//
	// ResourceTypes<Resource> types = new ResourceTypes<Resource>();
	// types.put("r1", r1);
	// types.put("r2", r2);
	//
	// Link l1 = new Link("linkType1");
	//
	// LinkTypes<Link> linkTypes = new LinkTypes<Link>();
	// linkTypes.put("l1", l1);
	//
	// Task t1 = new Task("t1");
	// Task t2 = new Task("t2");
	//
	// Application<Task, Dependency> app = new Application<Task, Dependency>();
	// app.addVertex(t1);
	// app.addVertex(t2);
	// app.addEdge(new Dependency("dep"), t1, t2);
	//
	// Mappings<Task, Resource> typeMappings = new Mappings<Task, Resource>();
	//
	// Mapping<Task, Resource> m1 = new Mapping<Task, Resource>("m1", t1, r1);
	// Mapping<Task, Resource> m2 = new Mapping<Task, Resource>("m2", t1, r2);
	// Mapping<Task, Resource> m3 = new Mapping<Task, Resource>("m3", t2, r1);
	// m3.setAttribute("M1", 0);
	//
	// typeMappings.add(m1);
	// typeMappings.add(m2);
	// typeMappings.add(m3);
	//
	// SpecificationTypeBased specTB = new SpecificationTypeBased(app, types,
	// typeMappings, linkTypes);
	//
	// SpecificationTransformerTypeBased<SpecificationTypeBased, Specification>
	// transformer = new
	// SpecificationTransformerTypeBased<SpecificationTypeBased,
	// Specification>();
	// Specification spec = transformer.transform(specTB);
	//
	// Assert.assertNotNull(spec.getApplication());
	// Assert.assertNotNull(spec.getArchitecture());
	// Assert.assertNotNull(spec.getMappings());
	// }
	//
	// @Test
	// public void generateArchitectureTest() {
	//
	// Resource r1 = new Resource("type1");
	// r1.setAttribute("A1", 5);
	// r1.setAttribute("A2", 1);
	//
	// Resource r2 = new Resource("type2");
	// r2.setAttribute("A1", 0);
	//
	// ResourceTypes<Resource> types = new ResourceTypes<Resource>();
	// types.put("r1", r1);
	// types.put("r2", r2);
	//
	// Link l1 = new Link("linkType1");
	// l1.setAttribute("B1", 2);
	//
	// LinkTypes<Link> linkTypes = new LinkTypes<Link>();
	// linkTypes.put("l1", l1);
	//
	// SpecificationTransformerTypeBased<SpecificationTypeBased, Specification>
	// transformer = new
	// SpecificationTransformerTypeBased<SpecificationTypeBased,
	// Specification>();
	//
	// Architecture<Resource, Link> arch =
	// transformer.generateArchitecture(types, linkTypes);
	//
	// Assert.assertNotNull(arch);
	// Assert.assertEquals(arch.getVertexCount(), 3);
	//
	// Assert.assertNotNull(arch.getVertex("r_type1"));
	// Assert.assertNotNull(arch.getVertex("r_type2"));
	// Assert.assertNotNull(arch.getVertex("bus"));
	//
	// Assert.assertNotNull(arch.getEdge("l_type1"));
	// Assert.assertNotNull(arch.getEdge("l_type2"));
	//
	// Assert.assertEquals(arch.getVertex("r_type1").getAttribute("A1"), 5);
	// Assert.assertEquals(arch.getVertex("r_type1").getAttribute("A2"), 1);
	//
	// Assert.assertEquals(arch.getVertex("r_type1").getType(), "type1");
	// Assert.assertEquals(arch.getEdge("l_type1").getAttribute("B1"), 2);
	//
	// Assert.assertNotEquals(arch.getVertex("r_type1"), types.get("type1"));
	// }
	//
	// @Test
	// public void generateMappingsTest() {
	//
	// Resource r1 = new Resource("type1");
	// Resource r2 = new Resource("type2");
	//
	// ResourceTypes<Resource> types = new ResourceTypes<Resource>();
	// types.put("r1", r1);
	// types.put("r2", r2);
	//
	// Link l1 = new Link("linkType1");
	//
	// LinkTypes<Link> linkTypes = new LinkTypes<Link>();
	// linkTypes.put("l1", l1);
	//
	// Task t1 = new Task("t1");
	// Task t2 = new Task("t2");
	//
	// Mappings<Task, Resource> typeMappings = new Mappings<Task, Resource>();
	//
	// Mapping<Task, Resource> m1 = new Mapping<Task, Resource>("m1", t1, r1);
	// Mapping<Task, Resource> m2 = new Mapping<Task, Resource>("m2", t1, r2);
	// Mapping<Task, Resource> m3 = new Mapping<Task, Resource>("m3", t2, r1);
	// m3.setAttribute("M1", 0);
	//
	// typeMappings.add(m1);
	// typeMappings.add(m2);
	// typeMappings.add(m3);
	//
	// SpecificationTransformerTypeBased<SpecificationTypeBased, Specification>
	// transformer = new
	// SpecificationTransformerTypeBased<SpecificationTypeBased,
	// Specification>();
	// Architecture<Resource, Link> arch =
	// transformer.generateArchitecture(types, linkTypes);
	//
	// Mappings<Task, Resource> mappings =
	// transformer.generateMappings(typeMappings, arch);
	//
	// Assert.assertNotNull(mappings);
	// Assert.assertEquals(mappings.size(), 3);
	//
	// Assert.assertEquals(mappings.get(t1).size(), 2);
	// Assert.assertEquals(mappings.get(t2).size(), 1);
	// Assert.assertEquals(mappings.get(t2).iterator().next().getAttribute("M1"),
	// 0);
	// }

}
