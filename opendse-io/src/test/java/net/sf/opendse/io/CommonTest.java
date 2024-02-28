package net.sf.opendse.io;

import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.sf.opendse.model.Resource;


public class CommonTest {
	public enum E1 {
		a, b;
	}
	
	// Used as the "very small number" for double comparisons
	public static final double epsilon = .000_000_000_000_1;

	@Test
	public void enumToInstance() throws IllegalArgumentException, SecurityException, InstantiationException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Object o = Common.toInstance("a", E1.class);
		Assertions.assertEquals(E1.a, o);
		Assertions.assertNotEquals(E1.b, o);
	}

	@Test
	public void resourceToInstance() throws IllegalArgumentException, SecurityException, InstantiationException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Object o = Common.toInstance("a", Resource.class);
		Assertions.assertEquals(new Resource("a"), o);
		Assertions.assertNotEquals(new Resource("b"), o);
	}
}
