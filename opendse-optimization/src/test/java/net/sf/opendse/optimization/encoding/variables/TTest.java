package net.sf.opendse.optimization.encoding.variables;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sf.opendse.model.Task;

public class TTest {

	@Test
	public void test() {
		Task test = new Task("test");
		T t = new T(test);
		assertEquals(test, t.getTask());
	}

}
