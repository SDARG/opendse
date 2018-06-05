package net.sf.opendse.model.properties;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

public class ArchitectureElementPropertyServiceTest {

	@Test(expected = IllegalArgumentException.class)
	public void testWrongInput() {
		Task task = new Task("task");
		ArchitectureElementPropertyService.getOffersRoutingVariety(task);
	}

	@Test
	public void test() {
		Resource res = new Resource("res");
		assertTrue(ArchitectureElementPropertyService.getOffersRoutingVariety(res));
		ArchitectureElementPropertyService.setOfferRoutingVariety(res, false);
		assertFalse(ArchitectureElementPropertyService.getOffersRoutingVariety(res));
		Link link = new Link("link");
		assertTrue(ArchitectureElementPropertyService.getOffersRoutingVariety(link));
		ArchitectureElementPropertyService.setOfferRoutingVariety(link, true);
		assertTrue(ArchitectureElementPropertyService.getOffersRoutingVariety(link));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testOuterElementException() {
		ArchitectureElementPropertyService.getOuterElementId(new Link("link"));
	}

	@Test
	public void testOuterElement() {
		Resource outer = new Resource("outer");
		Link link = new Link("link");
		ArchitectureElementPropertyService.setOfferRoutingVariety(link, false);
		ArchitectureElementPropertyService.setOuterResourceId(link, outer);
		String outerId = ArchitectureElementPropertyService.getOuterElementId(link);
		assertEquals(outer.getId(), outerId);
	}

}
