package net.sf.opendse.model;

import org.junit.Assert;
import org.junit.Test;

import net.sf.opendse.model.parameter.Parameter;
import net.sf.opendse.model.parameter.ParameterRange;

public class AttributesTest {

	/**
	 * Tests {@link Attributes#getAttribute(String)}.
	 */
	@Test
	public void testGetAttribute() {
		Attributes attributes = new Attributes();
		String id = "test";
		attributes.setAttribute(id, 1);

		Assert.assertEquals(1l, (long) attributes.<Integer> getAttribute(id));
		Assert.assertNull(attributes.<Integer> getAttribute("other"));
	}

	/**
	 * Tests {@link Attributes#getAttribute(String)} with {@link Parameter}.
	 */
	@Test
	public void testGetAttributeWithParameter() {
		Attributes attributes = new Attributes();
		String id = "test";
		Parameter parameter = new ParameterRange(1.0, 0.0, 2.0);
		attributes.setAttribute(id, parameter);

		Assert.assertEquals(1.0, attributes.getAttribute(id));
	}

	/**
	 * Tests {@link Attributes#getAttributes()}.
	 */
	@Test
	public void testGetAttributes() {
		Attributes attributes = new Attributes();
		Assert.assertEquals(attributes, attributes.getAttributes());
	}

	/**
	 * Tests {@link Attributes#getAttributeParameter(String)}.
	 */
	@Test
	public void testGetAttributeParameter() {
		Attributes attributes = new Attributes();
		String id = "test";
		String id2 = "test2";
		Parameter parameter = new ParameterRange(1.0, 0.0, 2.0);
		attributes.setAttribute(id, parameter);
		attributes.setAttribute(id2, 2);

		Assert.assertEquals(parameter, attributes.getAttributeParameter(id));
		Assert.assertNull(attributes.getAttributeParameter(id2));
	}

	/**
	 * Tests {@link Attributes#getAttributeNames()}.
	 */
	@Test
	public void testGetAttributeNames() {
		Attributes attributes = new Attributes();
		String id = "test";
		attributes.setAttribute(id, 2);
		Assert.assertFalse(attributes.getAttributeNames().isEmpty());
		Assert.assertTrue(attributes.getAttributeNames().contains(id));
		Assert.assertEquals(1, attributes.getAttributeNames().size());
	}

	/**
	 * Tests {@link Attributes#isDefined(String)}.
	 */
	@Test
	public void testIsDefined() {
		Attributes attributes = new Attributes();
		String id = "test";
		attributes.setAttribute(id, 2);
		Assert.assertTrue(attributes.isDefined(id));
		Assert.assertFalse(attributes.isDefined("other"));
	}
}
