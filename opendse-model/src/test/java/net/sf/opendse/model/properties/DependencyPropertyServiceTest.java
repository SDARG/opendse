package net.sf.opendse.model.properties;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.properties.DependencyPropertyService.DependencyAttributes;
import net.sf.opendse.model.properties.DependencyPropertyService.RoutingModes;

public class DependencyPropertyServiceTest {

	@Test(expected=IllegalArgumentException.class)
	public void testUnknownRoutingMode() {
		Dependency dependency = new Dependency("dep");
		dependency.setAttribute(DependencyAttributes.ROUTING_MODE.xmlName, "unknown");
		DependencyPropertyService.getRoutingMode(dependency);
	}
	
	@Test
	public void testRoutingMode() {
		Dependency dependency = new Dependency("dep");
		assertEquals(RoutingModes.DEFAULT, DependencyPropertyService.getRoutingMode(dependency));
		DependencyPropertyService.setRoutingMode(dependency, RoutingModes.DEFAULT);
		assertEquals(RoutingModes.DEFAULT, DependencyPropertyService.getRoutingMode(dependency));
		DependencyPropertyService.setRoutingMode(dependency, RoutingModes.REDUNDANT);
		assertEquals(RoutingModes.REDUNDANT, DependencyPropertyService.getRoutingMode(dependency));
	}
}
