package net.sf.opendse.model.properties;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.properties.ApplicationElementPropertyService.ActivationModes;

public class ApplicationElementPropertyServiceTest {

	@Test(expected = IllegalArgumentException.class)
	public void testWrongAlternativeAttrAccess() {
		ApplicationElementPropertyService.setAlternativeAttributes(new Task("t"), "Func1", "A");
	}

	@Test
	public void testAlternativeActivationAttributes() {
		Task task = new Task("t");
		ApplicationElementPropertyService.setActivationMode(task, ActivationModes.ALTERNATIVE);
		ApplicationElementPropertyService.setAlternativeAttributes(task, "Func1", "A");
		assertEquals("Func1", ApplicationElementPropertyService.getAlternativeFunction(task));
		assertEquals("A", ApplicationElementPropertyService.getAlternativeId(task));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUnknownActivationMode() {
		Task task = new Task("task");
		task.setAttribute(ApplicationElementPropertyService.ApplicationElementAttributes.ACTIVATION_MODE.xmlName,
				"unknown");
		ApplicationElementPropertyService.getActivationMode(task);
	}

	@Test
	public void testActivationMode() {
		Dependency dep = new Dependency("dep");
		Task task = new Task("task");
		assertEquals(ActivationModes.STATIC, ApplicationElementPropertyService.getActivationMode(task));
		assertEquals(ActivationModes.STATIC, ApplicationElementPropertyService.getActivationMode(dep));
		ApplicationElementPropertyService.setActivationMode(task, ActivationModes.STATIC);
		ApplicationElementPropertyService.setActivationMode(dep, ActivationModes.ALTERNATIVE);
		assertEquals(ActivationModes.STATIC, ApplicationElementPropertyService.getActivationMode(task));
		assertEquals(ActivationModes.ALTERNATIVE, ApplicationElementPropertyService.getActivationMode(dep));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCheckElement() {
		Resource res = new Resource("res");
		ApplicationElementPropertyService.checkElement(res);
	}

}
