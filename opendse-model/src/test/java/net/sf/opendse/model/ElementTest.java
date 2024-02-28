package net.sf.opendse.model;



import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.sf.opendse.model.parameter.Parameter;
import net.sf.opendse.model.parameter.ParameterRange;

/**
 * Unit tests for {@link Element}.
 * 
 * @author Felix Reimann
 *
 */
public class ElementTest {
	/**
	 * Tests {@link Element#getAttribute(String)}
	 */
	@Test
	public void testGetAttribute() {
		Element element = new Element("id");
		String id = "attribute name";
		element.setAttribute(id, 0);

		Assertions.assertEquals(0, (int) element.<Integer> getAttribute(id));
		Assertions.assertNull(element.getAttribute("other"));
	}

	/**
	 * Tests {@link Element#getAttribute(String)} in case the attribute is defined at the parent {@link Element}.
	 */
	@Test
	public void testGetAttributeFromParent() {
		Element parent = new Element("id");
		Element element = new Element(parent);
		String id = "y";

		parent.setAttribute(id, 0);
		Assertions.assertEquals(0, (int) element.<Integer> getAttribute(id));
	}

	/**
	 * Tests {@link Element#getParent()}
	 */
	@Test
	public void testGetParent() {
		Element parent = new Element("id");
		Element element = new Element(parent);

		Assertions.assertTrue(parent == element.getParent());
		Assertions.assertFalse(element == element.getParent());
	}

	/**
	 * Tests {@link Element#setParent(Element)}
	 */
	@Test
	public void testSetParent() {
		Element parent = new Element("id");
		Element element = new Element("other id");
		element.setParent(parent);

		Assertions.assertTrue(parent == element.getParent());
		Assertions.assertFalse(element == element.getParent());
	}

	/**
	 * Tests, if {@link Element#setParent(Element)} throws {@link IllegalStateException} if parent is already defined.
	 */
	@Test
	public void testSetParentIfNotExisting() {
		assertThrows(IllegalStateException.class, () -> {
			Element parent = new Element("id");
			Element element = new Element(parent);
			element.setParent(parent);
		});
	}

	/**
	 * Tests {@link Element#getAttributeNames()}.
	 */
	@Test
	public void testGetAttributeNames() {
		Element element = new Element("id");
		String id = "attribute name";
		element.setAttribute(id, 0);

		Assertions.assertEquals(1, element.getAttributeNames().size());
		Assertions.assertTrue(element.getAttributeNames().contains(id));
	}

	/**
	 * Tests, if {@link Element#getAttributeNames()} returns parent {@link Attributes}.
	 */
	@Test
	public void testGetAttributeNamesOfParent() {
		Element parent = new Element("id");
		String id = "attribute name";
		parent.setAttribute(id, 0);
		Element element = new Element(parent);

		Assertions.assertEquals(1, element.getAttributeNames().size());
		Assertions.assertTrue(element.getAttributeNames().contains(id));
	}

	/**
	 * Tests {@link Element#getLocalAttributeNames()}.
	 */
	@Test
	public void testGetLocalNames() {
		Element parent = new Element("id");
		String id1 = "x";
		parent.setAttribute(id1, 1);
		Element element = new Element(parent);
		String id2 = "y";
		element.setAttribute(id2, 0);

		Assertions.assertEquals(1, element.getLocalAttributeNames().size());
		Assertions.assertFalse(element.getLocalAttributeNames().contains(id1));
		Assertions.assertTrue(element.getLocalAttributeNames().contains(id2));
	}

	/**
	 * Tests {@link Element#toString()}.
	 */
	@Test
	public void testToString() {
		String id = "id";
		Element element = new Element(id);

		Assertions.assertEquals(id, element.toString());
	}

	/**
	 * Tests {@link Element#isDefined(String)}.
	 */
	@Test
	public void testIsdefined() {
		Element parent = new Element("id");
		String parentattribute = "a";
		parent.setAttribute(parentattribute, 1);

		Element element = new Element(parent);
		String attribute = "b";
		element.setAttribute(attribute, 1);

		Assertions.assertTrue(element.isDefined(attribute));
		Assertions.assertTrue(element.isDefined(parentattribute));
		Assertions.assertFalse(element.isDefined("c"));
	}

	/**
	 * Tests {@link Element#getAttributes()}.
	 */
	@Test
	public void testGetAttributes() {
		Element parent = new Element("id");
		String parentattribute = "a";
		parent.setAttribute(parentattribute, 1);

		Element element = new Element(parent);
		String attribute = "b";
		element.setAttribute(attribute, 1);

		Assertions.assertTrue(element.getAttributes().isDefined(parentattribute));
		Assertions.assertTrue(element.getAttributes().isDefined(attribute));
	}

	/**
	 * Tests {@link Element#getLocalAttributes()}.
	 */
	@Test
	public void testGetLocalAttributes() {
		Element parent = new Element("id");
		String parentattribute = "a";
		parent.setAttribute(parentattribute, 1);

		Element element = new Element(parent);
		String attribute = "b";
		element.setAttribute(attribute, 1);

		Assertions.assertFalse(element.getLocalAttributes().isDefined(parentattribute));
		Assertions.assertTrue(element.getLocalAttributes().isDefined(attribute));
	}

	@Test
	public void testType() {
		Element element = new Element("id");
		String type = "my type";
		element.setType(type);
		Assertions.assertEquals(type, element.getType());
	}

	/**
	 * Tests {@link Element#getAttributeParameter(String)}.
	 */
	@Test
	public void testGetAttributeParameter() {
		Element element = new Element("id");
		element.setAttribute("normal attribute", 1);
		Parameter parameter = new ParameterRange(1.0, 0.0, 2.0);
		element.setAttribute("parameter", parameter);

		Assertions.assertEquals(parameter, element.getAttributeParameter("parameter"));
		Assertions.assertNull(element.getAttributeParameter("normal attribute"));
	}

	/**
	 * Tests {@link Element#getAttributeParameter(String)} with a parent {@link Parameter}.
	 */
	@Test
	public void testGetAttributeParameterParent() {
		Element parent = new Element("id");
		Parameter parameter = new ParameterRange(1.0, 0.0, 2.0);
		parent.setAttribute("parameter", parameter);

		Element element = new Element(parent);

		Assertions.assertEquals(parameter, element.getAttributeParameter("parameter"));
		Assertions.assertNull(element.getAttributeParameter("other"));
	}

	/**
	 * Tests {@link Element#equals(Object)}.
	 */
	@Test
	public void testEquals() {
		Element parent = new Element("id");
		Element element = new Element(parent);

		Assertions.assertEquals(element, element);
		Assertions.assertNotEquals(element, null);
		Assertions.assertEquals(element, parent);
	}
}
