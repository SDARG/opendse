package net.sf.opendse.model.properties;

import static org.junit.Assert.*;


import org.junit.Test;

import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.properties.TaskPropertyService.ActivationModes;
import net.sf.opendse.model.properties.TaskPropertyService.TaskAttributes;

public class TaskPropertyServiceTest {

	@Test
	public void testIsProcessCommuncation() {
		Task task = new Task("task");
		Communication comm = new Communication("comm");
		assertTrue(TaskPropertyService.isProcess(task));
		assertTrue(TaskPropertyService.isCommunication(comm));
		assertFalse(TaskPropertyService.isCommunication(task));
		assertFalse(TaskPropertyService.isProcess(comm));
	}

	@Test
	public void testGetActivationMode() {
		Task task = new Task("task");
		assertEquals(ActivationModes.STATIC, TaskPropertyService.getActivationMode(task));
		TaskPropertyService.setActivationMode(task, ActivationModes.STATIC);
		assertEquals(ActivationModes.STATIC, TaskPropertyService.getActivationMode(task));
		TaskPropertyService.setActivationMode(task, ActivationModes.ALTERNATIVE);
		assertEquals(ActivationModes.ALTERNATIVE, TaskPropertyService.getActivationMode(task));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetInvalidActivationMode() {
		Task task = new Task("task");
		task.setAttribute(TaskAttributes.ACTIVATION_MODE.getXmlName(), "invalid");
		TaskPropertyService.getActivationMode(task);
	}
	
}
