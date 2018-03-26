package net.sf.opendse.encoding.application;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sf.opendse.model.properties.ApplicationElementPropertyService.ActivationModes;

public class DefaultConstraintGeneratorManagerTest {

	@Test
	public void test() {
		DefaultConstraintGeneratorManager manager = new DefaultConstraintGeneratorManager();
		assertTrue(manager.getConstraintGenerator(
				ActivationModes.STATIC) instanceof StaticModeConstraintGenerator);
	}

}
