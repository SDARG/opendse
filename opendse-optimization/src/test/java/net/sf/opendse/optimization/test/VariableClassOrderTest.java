package net.sf.opendse.optimization.test;



import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;
import net.sf.opendse.optimization.VariableClassOrder;
import net.sf.opendse.optimization.encoding.variables.EAVI;

/**
 * Unit tests for the methods of {@link VariableClassOrder}
 * 
 * @author Fedor Smirnov
 *
 */
public class VariableClassOrderTest {
	@Test
	public void testVariableClassOrder() {
		VariableClassOrder order = new VariableClassOrder();
		Assertions.assertEquals(0, order.indexOf(new Resource("Mockup")));
		Assertions.assertEquals(0, order.indexOf(new Link("Mockup")));
		Assertions.assertEquals(0, order.indexOf(new Task("Mockup")));
	}

	@Test
	public void testIndexOfObject() {
		VariableClassOrder order = new VariableClassOrder();
		// in the default case, everything should return 0
		Assertions.assertEquals(0, order.indexOf(new Resource("Mockup")));
		Assertions.assertEquals(0, order.indexOf(new Link("Mockup")));
		// test the order in a non-default case
		order.add(Resource.class);
		order.add(Link.class);
		Assertions.assertEquals(0, order.indexOf(new Resource("Mockup")));
		Assertions.assertEquals(1, order.indexOf(new Link("Mockup")));
		Assertions.assertEquals(-1, order.indexOf(new Task("Mockup")));
	}

	@Test
	public void testAddVariableClass() {
		VariableClassOrder order = new VariableClassOrder();
		order.add(Resource.class);
		order.add(EAVI.class);
		order.add(Mapping.class);
		order.add(Link.class);
		order.add(Task.class, Resource.class, Link.class);
		boolean firstRelationCorrect = order.indexOf(new Task("Mockup")) < order.indexOf(new Resource("Mockup"));
		boolean secondRelationCorrect = order.indexOf(new Task("Mockup")) < order.indexOf(new Link("Mockup"));
		order.add(Dependency.class);
		boolean addingToTheEndCorrect = order.indexOf(new Dependency("Mockup")) == 5;
		Assertions.assertTrue(firstRelationCorrect);
		Assertions.assertTrue(secondRelationCorrect);
		Assertions.assertTrue(addingToTheEndCorrect);
	}

	@Test
	public void testGetSize() {
		VariableClassOrder order = new VariableClassOrder();
		order.add(Resource.class);
		Assertions.assertEquals(1, order.getOrderSize());
	}
}
