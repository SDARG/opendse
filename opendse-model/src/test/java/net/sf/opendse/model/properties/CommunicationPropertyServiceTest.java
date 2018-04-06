package net.sf.opendse.model.properties;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.properties.CommunicationPropertyService.RoutingModes;

public class CommunicationPropertyServiceTest {

	@Test(expected = IllegalArgumentException.class)
	public void checkUnknownRoutingMode() {
		Communication comm = new Communication("comm");
		comm.setAttribute(CommunicationPropertyService.CommunicationAttributes.ROUTING_MODE.xmlName, "unknown");
		CommunicationPropertyService.getRoutingMode(comm);
	}

	@Test
	public void checkRoutingModes() {
		Communication comm = new Communication("comm");
		assertEquals(RoutingModes.DEFAULT, CommunicationPropertyService.getRoutingMode(comm));
		CommunicationPropertyService.setRoutingMode(comm, RoutingModes.DEFAULT);
		assertEquals(RoutingModes.DEFAULT, CommunicationPropertyService.getRoutingMode(comm));
		CommunicationPropertyService.setRoutingMode(comm, RoutingModes.CHIP);
		assertEquals(RoutingModes.CHIP, CommunicationPropertyService.getRoutingMode(comm));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCheckTask() {
		CommunicationPropertyService.checkTask(new Task("task"));
	}
}