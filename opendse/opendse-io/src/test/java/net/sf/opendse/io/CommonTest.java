package net.sf.opendse.io;

import java.lang.reflect.InvocationTargetException;

import net.sf.opendse.model.Resource;

import org.junit.Assert;
import org.junit.Test;

public class CommonTest {
	enum E1 {
		a, b;
	}

	@Test
	public void enumToInstance() throws IllegalArgumentException, SecurityException, InstantiationException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Object o = Common.toInstance("a", E1.class);
		Assert.assertEquals(E1.a, o);
		Assert.assertNotEquals(E1.b, o);
	}

	@Test
	public void resourceToInstance() throws IllegalArgumentException, SecurityException, InstantiationException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Object o = Common.toInstance("a", Resource.class);
		Assert.assertEquals(new Resource("a"), o);
		Assert.assertNotEquals(new Resource("b"), o);
	}
}
