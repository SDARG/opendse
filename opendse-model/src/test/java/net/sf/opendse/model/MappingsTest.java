package net.sf.opendse.model;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

public class MappingsTest {
	/**
	 * Tests {@link Mappings#remove(Mapping)}.
	 */
	@Test
	public void testRemove() {
		Mappings<Task, Resource> mappings = new Mappings<Task, Resource>();
		Mapping<Task, Resource> mapping = new Mapping<Task, Resource>("m", new Task("t"), new Resource("r"));
		mappings.add(mapping);

		Assert.assertTrue(mappings.remove(mapping));
		Assert.assertEquals(0, mappings.size());
		Assert.assertFalse(mappings.remove(mapping));
	}

	@Test
	public void testRemoveAll() {
		Mappings<Task, Resource> mappings = new Mappings<Task, Resource>();
		Mapping<Task, Resource> mapping = new Mapping<Task, Resource>("m", new Task("t"), new Resource("r"));
		mappings.add(mapping);

		Assert.assertTrue(mappings.removeAll(Collections.singleton(mapping)));
		Assert.assertEquals(0, mappings.size());
		Assert.assertFalse(mappings.removeAll(Collections.singleton(mapping)));
	}

	@Test
	public void testGetSourceTarget() {
		Mappings<Task, Resource> mappings = new Mappings<Task, Resource>();
		Task t = new Task("t");
		Resource r = new Resource("r");
		Mapping<Task, Resource> mapping = new Mapping<Task, Resource>("m", t, r);
		mappings.add(mapping);

		Assert.assertEquals(Collections.singleton(mapping), mappings.get(t, r));
	}

	@Test
	public void testGetSources() {
		Mappings<Task, Resource> mappings = new Mappings<Task, Resource>();
		Task t = new Task("t");
		Resource r = new Resource("r");
		Mapping<Task, Resource> mapping = new Mapping<Task, Resource>("m", t, r);
		mappings.add(mapping);

		Assert.assertEquals(Collections.singleton(t), mappings.getSources(r));
	}

	@Test
	public void testGetTargets() {
		Mappings<Task, Resource> mappings = new Mappings<Task, Resource>();
		Task t = new Task("t");
		Resource r = new Resource("r");
		Mapping<Task, Resource> mapping = new Mapping<Task, Resource>("m", t, r);
		mappings.add(mapping);

		Assert.assertEquals(Collections.singleton(r), mappings.getTargets(t));
	}

	@Test
	public void testGetAll() {
		Mappings<Task, Resource> mappings = new Mappings<Task, Resource>();
		Task t = new Task("t");
		Resource r = new Resource("r");
		Mapping<Task, Resource> mapping = new Mapping<Task, Resource>("m", t, r);
		mappings.add(mapping);

		Assert.assertEquals(Collections.singleton(mapping), mappings.getAll());
	}
}
