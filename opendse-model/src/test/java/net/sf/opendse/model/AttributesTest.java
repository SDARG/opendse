package net.sf.opendse.model;



import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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

		Assertions.assertEquals(1l, (long) attributes.<Integer>getAttribute(id));
		Assertions.assertNull(attributes.<Integer>getAttribute("other"));
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

		Assertions.assertEquals(1.0, attributes.getAttribute(id), 0.0);
	}

	/**
	 * Tests {@link Attributes#getAttributes()}.
	 */
	@Test
	public void testGetAttributes() {
		Attributes attributes = new Attributes();
		Assertions.assertEquals(attributes, attributes.getAttributes());
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

		Assertions.assertEquals(parameter, attributes.getAttributeParameter(id));
		Assertions.assertNull(attributes.getAttributeParameter(id2));
	}

	/**
	 * Tests {@link Attributes#getAttributeNames()}.
	 */
	@Test
	public void testGetAttributeNames() {
		Attributes attributes = new Attributes();
		String id = "test";
		attributes.setAttribute(id, 2);
		Assertions.assertFalse(attributes.getAttributeNames().isEmpty());
		Assertions.assertTrue(attributes.getAttributeNames().contains(id));
		Assertions.assertEquals(1, attributes.getAttributeNames().size());
	}

	/**
	 * Tests {@link Attributes#isDefined(String)}.
	 */
	@Test
	public void testIsDefined() {
		Attributes attributes = new Attributes();
		String id = "test";
		attributes.setAttribute(id, 2);
		Assertions.assertTrue(attributes.isDefined(id));
		Assertions.assertFalse(attributes.isDefined("other"));
	}
}
