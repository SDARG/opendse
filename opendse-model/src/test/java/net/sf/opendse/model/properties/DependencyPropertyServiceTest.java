package net.sf.opendse.model.properties;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.properties.DependencyPropertyService.ActivationModes;
import net.sf.opendse.model.properties.DependencyPropertyService.DependencyAttributes;

public class DependencyPropertyServiceTest {

	@Test
	public void testGetActivationMode() {
		Dependency dependency = new Dependency("dependency");
		assertEquals(ActivationModes.STATIC, DependencyPropertyService.getActivationMode(dependency));
		DependencyPropertyService.setActivationMode(dependency, ActivationModes.STATIC);
		assertEquals(ActivationModes.STATIC, DependencyPropertyService.getActivationMode(dependency));
		DependencyPropertyService.setActivationMode(dependency, ActivationModes.ALTERNATIVE);
		assertEquals(ActivationModes.ALTERNATIVE, DependencyPropertyService.getActivationMode(dependency));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetInvalidActivationMode() {
		Dependency dependency = new Dependency("dependency");
		dependency.setAttribute(DependencyAttributes.ACTIVATION_MODE.getXmlName(), "invalid");
		DependencyPropertyService.getActivationMode(dependency);
	}

}
