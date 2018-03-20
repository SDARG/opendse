package net.sf.opendse.encoding.application;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sf.opendse.model.properties.TaskPropertyService;

public class DefaultConstraintGeneratorManagerTest {

	@Test(expected = IllegalArgumentException.class)
	public void testUnknownActivationMode() {
		DefaultConstraintGeneratorManager manager = new DefaultConstraintGeneratorManager();
		manager.getConstraintGenerator("invalid");
	}

	@Test
	public void test() {
		DefaultConstraintGeneratorManager manager = new DefaultConstraintGeneratorManager();
		assertTrue(manager.getConstraintGenerator(
				TaskPropertyService.ActivationModes.STATIC.getXmlName()) instanceof StaticModeConstraintGenerator);
	}

}
