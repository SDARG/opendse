package net.sf.opendse.io;

import static net.sf.opendse.io.Common.getType;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import net.sf.opendse.io.CommonTest.E1;
import net.sf.opendse.model.Element;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.parameter.ParameterRangeInt;

import org.junit.Assert;
import org.junit.Test;

public class SpecificationReaderTest {
	@Test
	public void attributeToEnum() throws IllegalArgumentException, SecurityException, InstantiationException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		nu.xom.Element eAttr = new nu.xom.Element("attribute", SpecificationWriter.NS);
		eAttr.addAttribute(new nu.xom.Attribute("name", "test"));

		eAttr.addAttribute(new nu.xom.Attribute("type", E1.class.getName()));
		eAttr.appendChild(E1.a.name());
		System.out.println(eAttr.toXML());

		SpecificationReader reader = new SpecificationReader();
		E1 e = (E1) reader.toAttribute(eAttr);
		Assert.assertEquals(E1.a, e);
		Assert.assertNotEquals(E1.b, e);
	}

	@Test
	public void attributeToResource() throws IllegalArgumentException, SecurityException, InstantiationException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		nu.xom.Element eAttr = new nu.xom.Element("attribute", SpecificationWriter.NS);
		eAttr.addAttribute(new nu.xom.Attribute("name", "test"));

		Resource attribute = new Resource("test");

		eAttr.addAttribute(new nu.xom.Attribute("type", getType(Resource.class)));
		eAttr.appendChild(((Element) attribute).getId());

		SpecificationReader reader = new SpecificationReader();
		Assert.assertEquals(attribute, reader.toAttribute(eAttr));
	}

	@Test
	public void attributeToInt() throws IllegalArgumentException, SecurityException, InstantiationException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		nu.xom.Element eAttr = new nu.xom.Element("attribute", SpecificationWriter.NS);
		eAttr.addAttribute(new nu.xom.Attribute("name", "test"));

		int i = 10;

		eAttr.addAttribute(new nu.xom.Attribute("type", getType(Integer.class)));
		eAttr.appendChild("10");

		SpecificationReader reader = new SpecificationReader();
		Assert.assertEquals(i, reader.toAttribute(eAttr));
	}

	@Test
	public void attributeToString() throws IllegalArgumentException, SecurityException, InstantiationException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		nu.xom.Element eAttr = new nu.xom.Element("attribute", SpecificationWriter.NS);
		eAttr.addAttribute(new nu.xom.Attribute("name", "test"));

		String s = "10";

		eAttr.addAttribute(new nu.xom.Attribute("type", getType(String.class)));
		eAttr.appendChild("10");

		SpecificationReader reader = new SpecificationReader();
		Assert.assertEquals(s, reader.toAttribute(eAttr));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void attributeToHashSet() throws IllegalArgumentException, SecurityException, InstantiationException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		nu.xom.Element eAttr = new nu.xom.Element("attribute", SpecificationWriter.NS);
		eAttr.addAttribute(new nu.xom.Attribute("name", "test"));

		String s1 = "10";
		String s2 = "101";
		Set<String> strings = new HashSet<String>();
		strings.add(s1);
		strings.add(s2);

		nu.xom.Element eAttr1 = new nu.xom.Element("attribute", SpecificationWriter.NS);
		eAttr1.addAttribute(new nu.xom.Attribute("name", "test1"));
		eAttr1.addAttribute(new nu.xom.Attribute("type", getType(String.class)));
		eAttr1.appendChild(s1);

		nu.xom.Element eAttr2 = new nu.xom.Element("attribute", SpecificationWriter.NS);
		eAttr2.addAttribute(new nu.xom.Attribute("name", "test2"));
		eAttr2.addAttribute(new nu.xom.Attribute("type", getType(String.class)));
		eAttr2.appendChild(s2);

		eAttr.addAttribute(new nu.xom.Attribute("type", getType(HashSet.class)));
		eAttr.appendChild(eAttr1);
		eAttr.appendChild(eAttr2);
		System.out.println(eAttr.toXML());

		SpecificationReader reader = new SpecificationReader();
		Set<String> strings2 = (Set<String>) reader.toAttribute(eAttr);
		Assert.assertEquals(strings, strings2);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void attributeToSet() throws IllegalArgumentException, SecurityException, InstantiationException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		nu.xom.Element eAttr = new nu.xom.Element("attribute", SpecificationWriter.NS);
		eAttr.addAttribute(new nu.xom.Attribute("name", "test"));

		String s1 = "10";
		String s2 = "101";
		Set<String> strings = new HashSet<String>();
		strings.add(s1);
		strings.add(s2);

		nu.xom.Element eAttr1 = new nu.xom.Element("attribute", SpecificationWriter.NS);
		eAttr1.addAttribute(new nu.xom.Attribute("name", "test1"));
		eAttr1.addAttribute(new nu.xom.Attribute("type", getType(String.class)));
		eAttr1.appendChild(s1);

		nu.xom.Element eAttr2 = new nu.xom.Element("attribute", SpecificationWriter.NS);
		eAttr2.addAttribute(new nu.xom.Attribute("name", "test2"));
		eAttr2.addAttribute(new nu.xom.Attribute("type", getType(String.class)));
		eAttr2.appendChild(s2);

		eAttr.addAttribute(new nu.xom.Attribute("type", "SET"));
		eAttr.appendChild(eAttr1);
		eAttr.appendChild(eAttr2);
		System.out.println(eAttr.toXML());

		SpecificationReader reader = new SpecificationReader();
		Set<String> strings2 = (Set<String>) reader.toAttribute(eAttr);
		Assert.assertEquals(strings, strings2);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void attributeToEmptySet() throws IllegalArgumentException, SecurityException, InstantiationException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		nu.xom.Element eAttr = new nu.xom.Element("attribute", SpecificationWriter.NS);
		eAttr.addAttribute(new nu.xom.Attribute("name", "test"));

		Set<String> strings = new HashSet<String>();

		eAttr.addAttribute(new nu.xom.Attribute("type", "SET"));
		System.out.println(eAttr.toXML());

		SpecificationReader reader = new SpecificationReader();
		Set<String> strings2 = (Set<String>) reader.toAttribute(eAttr);
		Assert.assertEquals(strings, strings2);
	}

	@Test
	public void testGetRangeInt() {
		SpecificationReader reader = new SpecificationReader();
		String xmlValue = "5 (3, 9)";
		ParameterRangeInt parameter = reader.getRangeInt(xmlValue);
		Assert.assertEquals(3, parameter.getLowerBound());
		Assert.assertEquals(9, parameter.getUpperBound());
		Assert.assertEquals((Integer) 5, parameter.getValue());
	}

	@Test
	public void ioParameterRangeInt() throws IllegalArgumentException, SecurityException, InstantiationException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		ParameterRangeInt parameter = new ParameterRangeInt(4, 2, 9);

		SpecificationWriter specificationWriter = new SpecificationWriter();
		nu.xom.Element element = specificationWriter.toElement("test", parameter);
		Assert.assertNotNull(element);

		SpecificationReader specificationReader = new SpecificationReader();
		ParameterRangeInt parameter2 = (ParameterRangeInt) specificationReader.toAttribute(element);

		Assert.assertEquals(parameter.getLowerBound(), parameter2.getLowerBound());
		Assert.assertEquals(parameter.getUpperBound(), parameter2.getUpperBound());
		Assert.assertEquals(parameter.getValue(), parameter2.getValue());
	}
}
