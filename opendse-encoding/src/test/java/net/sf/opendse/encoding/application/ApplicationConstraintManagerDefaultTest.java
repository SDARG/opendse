package net.sf.opendse.encoding.application;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sf.opendse.model.properties.ApplicationElementPropertyService;

public class ApplicationConstraintManagerDefaultTest {

	@Test
	public void test() {
		ApplicationConstraintManagerDefault manager = new ApplicationConstraintManagerDefault();
		assertTrue(manager.getConstraintGenerator(
				ApplicationElementPropertyService.activationAttributeStatic) instanceof ApplicationConstraintGeneratorStatic);
	}

}
