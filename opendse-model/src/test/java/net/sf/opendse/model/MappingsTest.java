package net.sf.opendse.model;

import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;



public class MappingsTest {
	/**
	 * Tests {@link Mappings#remove(Mapping)}.
	 */
	@Test
	public void testRemove() {
		Mappings<Task, Resource> mappings = new Mappings<Task, Resource>();
		Mapping<Task, Resource> mapping = new Mapping<Task, Resource>("m", new Task("t"), new Resource("r"));
		mappings.add(mapping);

		Assertions.assertTrue(mappings.remove(mapping));
		Assertions.assertEquals(0, mappings.size());
		Assertions.assertFalse(mappings.remove(mapping));
	}

	@Test
	public void testRemoveAll() {
		Mappings<Task, Resource> mappings = new Mappings<Task, Resource>();
		Mapping<Task, Resource> mapping = new Mapping<Task, Resource>("m", new Task("t"), new Resource("r"));
		mappings.add(mapping);

		Assertions.assertTrue(mappings.removeAll(Collections.singleton(mapping)));
		Assertions.assertEquals(0, mappings.size());
		Assertions.assertFalse(mappings.removeAll(Collections.singleton(mapping)));
	}

	@Test
	public void testGetSourceTarget() {
		Mappings<Task, Resource> mappings = new Mappings<Task, Resource>();
		Task t = new Task("t");
		Resource r = new Resource("r");
		Mapping<Task, Resource> mapping = new Mapping<Task, Resource>("m", t, r);
		mappings.add(mapping);

		Assertions.assertEquals(Collections.singleton(mapping), mappings.get(t, r));
	}

	@Test
	public void testGetSources() {
		Mappings<Task, Resource> mappings = new Mappings<Task, Resource>();
		Task t = new Task("t");
		Resource r = new Resource("r");
		Mapping<Task, Resource> mapping = new Mapping<Task, Resource>("m", t, r);
		mappings.add(mapping);

		Assertions.assertEquals(Collections.singleton(t), mappings.getSources(r));
	}

	@Test
	public void testGetTargets() {
		Mappings<Task, Resource> mappings = new Mappings<Task, Resource>();
		Task t = new Task("t");
		Resource r = new Resource("r");
		Mapping<Task, Resource> mapping = new Mapping<Task, Resource>("m", t, r);
		mappings.add(mapping);

		Assertions.assertEquals(Collections.singleton(r), mappings.getTargets(t));
	}

	@Test
	public void testGetAll() {
		Mappings<Task, Resource> mappings = new Mappings<Task, Resource>();
		Task t = new Task("t");
		Resource r = new Resource("r");
		Mapping<Task, Resource> mapping = new Mapping<Task, Resource>("m", t, r);
		mappings.add(mapping);

		Assertions.assertEquals(Collections.singleton(mapping), mappings.getAll());
	}
}
