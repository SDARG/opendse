package net.sf.opendse.encoding.variables;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sf.opendse.model.Task;

public class TTest {

	@Test
	public void test() {
		Task task = new Task("task");
		T tVar = new T(task);
		assertEquals(task, tVar.getTask());
		assertEquals(tVar, new T(task));
	}

}
