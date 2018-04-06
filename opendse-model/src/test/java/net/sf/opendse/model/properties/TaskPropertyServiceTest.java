package net.sf.opendse.model.properties;

import static org.junit.Assert.*;


import org.junit.Test;

import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Task;

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
}
