package net.sf.opendse.io;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import net.sf.opendse.io.CommonTest.E1;
import net.sf.opendse.model.Application;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Element;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.parameter.ParameterRangeDiscrete;
import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

public class SpecificationReaderTest {
	@Test
	public void toApplicationTest() throws ValidityException, ParsingException, IOException, ClassNotFoundException,
			InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		nu.xom.Element eApp = new nu.xom.Element("application", SpecificationWriter.NS);
		nu.xom.Element r1 = new nu.xom.Element("task", SpecificationWriter.NS);
		r1.addAttribute(new Attribute("id", "r1"));
		eApp.appendChild(r1);
		nu.xom.Element r2 = new nu.xom.Element("communication", SpecificationWriter.NS);
		r2.addAttribute(new Attribute("id", "r2"));
		eApp.appendChild(r2);
		nu.xom.Element l = new nu.xom.Element("dependency", SpecificationWriter.NS);
		l.addAttribute(new Attribute("id", "l"));
		l.addAttribute(new Attribute("source", "r1"));
		l.addAttribute(new Attribute("destination", "r2"));
		eApp.appendChild(l);

		SpecificationReader r = new SpecificationReader();
		Application<Task, Dependency> application = r.toApplication(eApp);

		Assert.assertNotNull(application.getVertex("r1"));
		Assert.assertNotNull(application.getVertex("r2"));
		Assert.assertNotNull(application.getEdge("l"));
	}

	@Test
	public void toArchitectureTest() throws ValidityException, ParsingException, IOException, ClassNotFoundException,
			InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		nu.xom.Element eArch = new nu.xom.Element("architecture", SpecificationWriter.NS);
		nu.xom.Element r1 = new nu.xom.Element("resource", SpecificationWriter.NS);
		r1.addAttribute(new Attribute("id", "r1"));
		eArch.appendChild(r1);
		nu.xom.Element r2 = new nu.xom.Element("resource", SpecificationWriter.NS);
		r2.addAttribute(new Attribute("id", "r2"));
		eArch.appendChild(r2);
		nu.xom.Element l = new nu.xom.Element("link", SpecificationWriter.NS);
		l.addAttribute(new Attribute("id", "l"));
		l.addAttribute(new Attribute("source", "r1"));
		l.addAttribute(new Attribute("destination", "r2"));
		eArch.appendChild(l);

		SpecificationReader r = new SpecificationReader();
		Architecture<Resource, Link> architecture = r.toArchitecture(eArch);

		Assert.assertNotNull(architecture.getVertex("r1"));
		Assert.assertNotNull(architecture.getVertex("r2"));
		Assert.assertNotNull(architecture.getEdge("l"));
	}

	@Test
	public void parseDependencyTest() throws ValidityException, ParsingException, IOException, ClassNotFoundException,
			InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		nu.xom.Element eLink = new Builder()
				.build("<dependency id='d1' source='t1' destination='c1' orientation='DIRECTED'/>", "")
				.getRootElement();
		Application<Task, Dependency> application = new Application<Task, Dependency>();
		Task d1 = new Task("t1");
		Task d2 = new Task("c1");
		application.addVertex(d1);
		application.addVertex(d2);

		SpecificationReader r = new SpecificationReader();
		r.parseDependency(eLink, application);

		Assert.assertTrue(application.isSuccessor(d1, d2));
	}

	@Test(expected = IllegalArgumentException.class)
	public void parseDependencySrcMissingTest()
			throws ValidityException, ParsingException, IOException, ClassNotFoundException, InstantiationException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		nu.xom.Element eLink = new Builder()
				.build("<dependency id='d1' source='t1' destination='c1' orientation='DIRECTED'/>", "")
				.getRootElement();
		Application<Task, Dependency> application = new Application<Task, Dependency>();
		Task d2 = new Task("c1");
		application.addVertex(d2);

		SpecificationReader r = new SpecificationReader();
		r.parseDependency(eLink, application);
	}

	@Test(expected = IllegalArgumentException.class)
	public void parseDependencyDstMissingTest()
			throws ValidityException, ParsingException, IOException, ClassNotFoundException, InstantiationException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		nu.xom.Element eLink = new Builder()
				.build("<dependency id='d1' source='t1' destination='c1' orientation='DIRECTED'/>", "")
				.getRootElement();
		Application<Task, Dependency> application = new Application<Task, Dependency>();
		Task d1 = new Task("t1");
		application.addVertex(d1);

		SpecificationReader r = new SpecificationReader();
		r.parseDependency(eLink, application);
	}

	@Test
	public void parseLinkTest() throws ValidityException, ParsingException, IOException, ClassNotFoundException,
			InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		nu.xom.Element eLink = new Builder()
				.build("<link id='l2' source='bus' destination='r2' orientation='UNDIRECTED'/>", "").getRootElement();
		Architecture<Resource, Link> architecture = new Architecture<Resource, Link>();
		Resource r1 = new Resource("bus");
		Resource r2 = new Resource("r2");
		architecture.addVertex(r1);
		architecture.addVertex(r2);

		SpecificationReader r = new SpecificationReader();
		r.parseLink(eLink, architecture);

		Assert.assertTrue(architecture.isNeighbor(r1, r2));
	}

	@Test(expected = IllegalArgumentException.class)
	public void parseLinkMissingSrcResourceTest()
			throws ValidityException, ParsingException, IOException, ClassNotFoundException, InstantiationException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		nu.xom.Element eLink = new Builder()
				.build("<link id='l2' source='bus' destination='r2' orientation='UNDIRECTED'/>", "").getRootElement();
		Architecture<Resource, Link> architecture = new Architecture<Resource, Link>();
		Resource r1 = new Resource("bus");
		architecture.addVertex(r1);

		SpecificationReader r = new SpecificationReader();
		r.parseLink(eLink, architecture);
	}

	@Test(expected = IllegalArgumentException.class)
	public void parseLinkMissingDstResourceTest()
			throws ValidityException, ParsingException, IOException, ClassNotFoundException, InstantiationException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		nu.xom.Element eLink = new Builder()
				.build("<link id='l2' source='bus' destination='r2' orientation='UNDIRECTED'/>", "").getRootElement();
		Architecture<Resource, Link> architecture = new Architecture<Resource, Link>();
		Resource r1 = new Resource("r2");
		architecture.addVertex(r1);

		SpecificationReader r = new SpecificationReader();
		r.parseLink(eLink, architecture);
	}

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
		ClassDictionaryDefault classDict = new ClassDictionaryDefault();

		eAttr.addAttribute(new nu.xom.Attribute("type", classDict.getType(Resource.class)));
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

		ClassDictionaryDefault classDict = new ClassDictionaryDefault();

		eAttr.addAttribute(new nu.xom.Attribute("type", classDict.getType(Integer.class)));
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

		ClassDictionaryDefault classDict = new ClassDictionaryDefault();

		eAttr.addAttribute(new nu.xom.Attribute("type", classDict.getType(String.class)));
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

		ClassDictionaryDefault classDict = new ClassDictionaryDefault();

		nu.xom.Element eAttr1 = new nu.xom.Element("attribute", SpecificationWriter.NS);
		eAttr1.addAttribute(new nu.xom.Attribute("name", "test1"));
		eAttr1.addAttribute(new nu.xom.Attribute("type", classDict.getType(String.class)));
		eAttr1.appendChild(s1);

		nu.xom.Element eAttr2 = new nu.xom.Element("attribute", SpecificationWriter.NS);
		eAttr2.addAttribute(new nu.xom.Attribute("name", "test2"));
		eAttr2.addAttribute(new nu.xom.Attribute("type", classDict.getType(String.class)));
		eAttr2.appendChild(s2);

		eAttr.addAttribute(new nu.xom.Attribute("type", classDict.getType(HashSet.class)));
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

		ClassDictionaryDefault classDict = new ClassDictionaryDefault();

		nu.xom.Element eAttr1 = new nu.xom.Element("attribute", SpecificationWriter.NS);
		eAttr1.addAttribute(new nu.xom.Attribute("name", "test1"));
		eAttr1.addAttribute(new nu.xom.Attribute("type", classDict.getType(String.class)));
		eAttr1.appendChild(s1);

		nu.xom.Element eAttr2 = new nu.xom.Element("attribute", SpecificationWriter.NS);
		eAttr2.addAttribute(new nu.xom.Attribute("name", "test2"));
		eAttr2.addAttribute(new nu.xom.Attribute("type", classDict.getType(String.class)));
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
		ParameterRangeDiscrete parameter = reader.getRangeInt(xmlValue);
		Assert.assertEquals(3, parameter.getLowerBound());
		Assert.assertEquals(9, parameter.getUpperBound());
		Assert.assertEquals((Integer) 5, parameter.getValue());
	}

	@Test
	public void ioParameterRangeInt() throws IllegalArgumentException, SecurityException, InstantiationException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		ParameterRangeDiscrete parameter = new ParameterRangeDiscrete(4, 2, 9);

		SpecificationWriter specificationWriter = new SpecificationWriter();
		nu.xom.Element element = specificationWriter.toElement("test", parameter);
		Assert.assertNotNull(element);

		SpecificationReader specificationReader = new SpecificationReader();
		ParameterRangeDiscrete parameter2 = (ParameterRangeDiscrete) specificationReader.toAttribute(element);

		Assert.assertEquals(parameter.getLowerBound(), parameter2.getLowerBound());
		Assert.assertEquals(parameter.getUpperBound(), parameter2.getUpperBound());
		Assert.assertEquals(parameter.getValue(), parameter2.getValue());
	}
}
