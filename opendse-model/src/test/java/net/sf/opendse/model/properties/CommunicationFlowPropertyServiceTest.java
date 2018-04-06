package net.sf.opendse.model.properties;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.properties.DependencyPropertyService.RoutingModes;

public class CommunicationFlowPropertyServiceTest {

	@Test
	public void test() {
		Dependency depDef1 = new Dependency("dep1");
		Dependency depDef2 = new Dependency("dep2");
		Dependency depRed1 = new Dependency("dep3");
		Dependency depRed2 = new Dependency("dep4");
		DependencyPropertyService.setRoutingMode(depDef1, RoutingModes.DEFAULT);
		DependencyPropertyService.setRoutingMode(depDef2, RoutingModes.DEFAULT);
		DependencyPropertyService.setRoutingMode(depRed1, RoutingModes.REDUNDANT);
		DependencyPropertyService.setRoutingMode(depRed2, RoutingModes.REDUNDANT);

		assertEquals(RoutingModes.DEFAULT, CommunicationFlowPropertyService.getRoutingMode(depDef1, depDef2));
		assertEquals(RoutingModes.REDUNDANT, CommunicationFlowPropertyService.getRoutingMode(depDef1, depRed1));
		assertEquals(RoutingModes.REDUNDANT, CommunicationFlowPropertyService.getRoutingMode(depRed1, depDef2));
		assertEquals(RoutingModes.REDUNDANT, CommunicationFlowPropertyService.getRoutingMode(depRed1, depRed2));
	}
}