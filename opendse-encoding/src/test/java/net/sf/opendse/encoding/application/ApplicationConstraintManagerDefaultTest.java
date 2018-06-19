package net.sf.opendse.encoding.application;

import static org.junit.Assert.*;

import static org.mockito.Mockito.mock;

import org.junit.Test;

import net.sf.opendse.model.properties.ApplicationElementPropertyService;

public class ApplicationConstraintManagerDefaultTest {

	@Test
	public void test() {
		ApplicationConstraintManagerDefault manager = new ApplicationConstraintManagerDefault();
		assertTrue(manager.getConstraintGenerator(
				ApplicationElementPropertyService.activationAttributeStatic) instanceof ApplicationConstraintGeneratorStatic);
		ApplicationConstraintGenerator generator = mock(ApplicationConstraintGenerator.class);
		String newMode = "newMode";
		manager.addConstraintGenerator(newMode, generator);
		assertTrue(manager.generatorMap.containsKey(newMode));
		assertEquals(generator, manager.generatorMap.get(newMode));
	}

}
