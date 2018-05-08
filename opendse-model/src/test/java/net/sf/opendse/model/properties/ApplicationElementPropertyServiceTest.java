package net.sf.opendse.model.properties;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

public class ApplicationElementPropertyServiceTest {

	@Test(expected = IllegalArgumentException.class)
	public void testWrongAlternativeAttrAccess() {
		ApplicationElementPropertyService.setAlternativeAttributes(new Task("t"), "Func1", "A");
	}

	@Test
	public void testAlternativeActivationAttributes() {
		Task task = new Task("t");
		ApplicationElementPropertyService.setActivationMode(task, ApplicationElementPropertyService.activationAttributeAlternative);
		ApplicationElementPropertyService.setAlternativeAttributes(task, "Func1", "A");
		assertEquals("Func1", ApplicationElementPropertyService.getAlternativeFunction(task));
		assertEquals("A", ApplicationElementPropertyService.getAlternativeId(task));
	}

	@Test
	public void testActivationMode() {
		Dependency dep = new Dependency("dep");
		Task task = new Task("task");
		assertEquals(ApplicationElementPropertyService.activationAttributeStatic, ApplicationElementPropertyService.getActivationMode(task));
		assertEquals(ApplicationElementPropertyService.activationAttributeStatic, ApplicationElementPropertyService.getActivationMode(dep));
		ApplicationElementPropertyService.setActivationMode(task, ApplicationElementPropertyService.activationAttributeStatic);
		ApplicationElementPropertyService.setActivationMode(dep, ApplicationElementPropertyService.activationAttributeAlternative);
		assertEquals(ApplicationElementPropertyService.activationAttributeStatic, ApplicationElementPropertyService.getActivationMode(task));
		assertEquals(ApplicationElementPropertyService.activationAttributeAlternative, ApplicationElementPropertyService.getActivationMode(dep));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCheckElement() {
		Resource res = new Resource("res");
		ApplicationElementPropertyService.checkElement(res);
	}

}
