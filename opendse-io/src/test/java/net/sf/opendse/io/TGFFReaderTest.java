package net.sf.opendse.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import net.sf.opendse.model.Application;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.LinkTypes;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.ResourceTypes;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.TypeBasedSpecification;

public class TGFFReaderTest {

	protected static final String testFile = "specs/opendse_example.tgff";

	/**
	 * Test method for
	 * {@link net.sf.opendse.io.TGFFReader#read(java.lang.String)}.
	 */
	@Test
	public void testReadString() {

		TGFFReader reader = new TGFFReader();
		TypeBasedSpecification spec = reader.read(testFile);

		Assert.assertNotNull(spec);
	}

	/**
	 * Test method for {@link net.sf.opendse.io.TGFFReader#read(java.io.File)}.
	 */
	@Test
	public void testReadFile() {

		TGFFReader reader = new TGFFReader();
		File file = new File(testFile);
		TypeBasedSpecification spec = reader.read(file);

		Assert.assertNotNull(spec);
	}

	/**
	 * Test method for
	 * {@link net.sf.opendse.io.TGFFReader#read(java.io.InputStream)}.
	 */
	@Test
	public void testReadInputStream() {
		TGFFReader reader = new TGFFReader();
		File file = new File(testFile);

		TypeBasedSpecification spec = null;
		try {
			spec = reader.read(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		Assert.assertNotNull(spec);
	}

	/**
	 * Test method for
	 * {@link net.sf.opendse.io.TGFFReader#toSpecification(java.util.List)}.
	 */
	@Test
	public void testToSpecification() {

		List<String> in = importFile(testFile, 0);
		TypeBasedSpecification spec = new TGFFReader().toSpecification(in);

		Assert.assertNotNull(spec);
		Assert.assertNotNull(spec.getApplication());
		Assert.assertNotNull(spec.getResourceTypes());
		Assert.assertNotNull(spec.getLinkTypes());
		Assert.assertNotNull(spec.getMappings());

	}

	/**
	 * Test method for
	 * {@link net.sf.opendse.io.TGFFReader#toApplication(java.util.List)}.
	 */
	@Test
	public void testToApplication() {

		List<String> in = importFile(testFile, 0);
		Application<Task, Dependency> application = new TGFFReader().toApplication(in);

		Assert.assertNotNull(application);

		Assert.assertEquals(7, application.getVertexCount());
		Assert.assertEquals(6, application.getEdgeCount());

		List<String> ids = new ArrayList<String>(
				Arrays.asList("t0_0_0", "t0_1_0", "t0_2_0", "t0_3_0", "a0_0", "a0_1", "a0_2"));

		for (Task task : application.getVertices()) {
			Assert.assertTrue(ids.contains(task.getId()));
		}

		List<String> edges = new ArrayList<String>(
				Arrays.asList("a0_0_0", "a0_0_1", "a0_1_0", "a0_1_1", "a0_2_0", "a0_2_1"));

		for (Dependency edge : application.getEdges()) {
			Assert.assertTrue(edges.contains(edge.getId()));
		}

	}

	/**
	 * Test method for
	 * {@link net.sf.opendse.io.TGFFReader#toResourceTypes(java.util.List)}.
	 */
	@Test
	public void testToResourceTypes() {

		List<String> in = importFile(testFile, 0);

		TGFFReader reader = new TGFFReader();
		ResourceTypes<Resource> resourceTypes = reader.toResourceTypes(in);

		Assert.assertEquals(3, resourceTypes.values().size());

		for (int i = 0; i < 3; i++) {
			Assert.assertTrue(resourceTypes.containsKey("r" + i));
		}
	}

	/**
	 * Test method for
	 * {@link net.sf.opendse.io.TGFFReader#toMappings(java.util.List, net.sf.opendse.model.ResourceTypes)}.
	 */
	@Test
	public void testToMappings() {

		List<String> in = importFile(testFile, 0);

		TGFFReader reader = new TGFFReader();
		reader.toApplication(in);
		ResourceTypes<Resource> resourceTypes = reader.toResourceTypes(in);

		Mappings<Task, Resource> mappings = reader.toMappings(in, resourceTypes);

		Assert.assertEquals(10, mappings.size());

		List<String> ids = new ArrayList<String>(
				Arrays.asList("m_t0_0_0_r0", "m_t0_1_0_r0", "m_t0_2_0_r0", "m_t0_3_0_r0", "m_t0_1_0_r1", "m_t0_2_0_r1",
						"m_t0_3_0_r1", "m_t0_1_0_r2", "m_t0_2_0_r2", "m_t0_3_0_r2"));
		List<String> times = new ArrayList<String>(Arrays.asList("7", "8", "9", "11", "12"));

		for (Mapping<Task, Resource> mapping : mappings) {
			Assert.assertTrue(ids.contains(mapping.getId()));
			Assert.assertTrue(times.contains(mapping.getAttribute("task_time")));
		}
	}

	/**
	 * Test method for
	 * {@link net.sf.opendse.io.TGFFReader#toLinkTypes(java.util.List)}.
	 */
	@Test
	public void testToLinkTypes() {

		LinkTypes<Link> linkTypes = new TGFFReader().toLinkTypes(importFile(testFile, 0));

		Assert.assertEquals(1, linkTypes.size());
		Assert.assertTrue(linkTypes.containsKey(TGFFReader.WIRE));
		Assert.assertNotNull(linkTypes.get(TGFFReader.WIRE).getAttribute("max_buffer_size"));
	}

	/**
	 * Test method for
	 * {@link net.sf.opendse.io.TGFFReader#importTaskGraph(java.lang.String, java.util.Iterator, net.sf.opendse.model.Application)}.
	 */
	@Test
	public void testImportTaskGraph() {

		String line = "@TASK_GRAPH 0 test";
		Iterator<String> it = importFile(testFile, 11).iterator();
		Application<Task, Dependency> application = new Application<Task, Dependency>();

		new TGFFReader().importTaskGraph(line, it, application);

		Assert.assertNotNull(application);

		Assert.assertEquals(7, application.getVertexCount());
		Assert.assertEquals(6, application.getEdgeCount());

		List<String> ids = new ArrayList<String>(
				Arrays.asList("t0_0_0", "t0_1_0", "t0_2_0", "t0_3_0", "a0_0", "a0_1", "a0_2"));

		for (Task task : application.getVertices()) {
			Assert.assertTrue(ids.contains(task.getId()));
		}

		List<String> edges = new ArrayList<String>(
				Arrays.asList("a0_0_0", "a0_0_1", "a0_1_0", "a0_1_1", "a0_2_0", "a0_2_1"));

		for (Dependency edge : application.getEdges()) {
			Assert.assertTrue(edges.contains(edge.getId()));
		}
	}

	/**
	 * Test method for
	 * {@link net.sf.opendse.io.TGFFReader#addTask(java.lang.String, java.lang.String, double, net.sf.opendse.model.Application)}.
	 */
	@Test
	public void testAddTask() {

		Application<Task, Dependency> application = new Application<Task, Dependency>();

		String line = "	TASK test0_0 TYPE 0";
		String suffix = "_0";
		double period = 0.0;

		TGFFReader reader = new TGFFReader();
		reader.addTask(line, suffix, period, application);

		String id = "test0_0_0";

		Assert.assertEquals(1, application.getVertexCount());
		Assert.assertEquals(id, application.iterator().next().getId());
	}

	/**
	 * Test method for
	 * {@link net.sf.opendse.io.TGFFReader#addCommunication(java.lang.String, java.lang.String, double, net.sf.opendse.model.Application)}.
	 */
	@Test
	public void testAddCommunication() {

		String line = "ARC a0_0 FROM test0_0 TO test0_1 TYPE 0";
		String suffix = "_0";
		double period = 0.0;

		Task task0 = new Task("test0_0_0");
		Task task1 = new Task("test0_1_0");

		Application<Task, Dependency> application = new Application<Task, Dependency>();
		application.addVertex(task0);
		application.addVertex(task1);

		TGFFReader reader = new TGFFReader();

		Iterator<String> it = importFile(testFile, 3).iterator();
		reader.messageSizes = reader.importMessageSizes(it);

		reader.addCommunication(line, suffix, period, application);

		String comm = "a0_0";

		Assert.assertEquals(3, application.getVertexCount());
		Assert.assertNotNull(application.getVertex(comm));

		Assert.assertEquals(2, application.getEdgeCount());
		Assert.assertNotNull(application.getEdge(comm + "_0"));
		Assert.assertNotNull(application.getEdge(comm + "_1"));
	}

	/**
	 * Test method for
	 * {@link net.sf.opendse.io.TGFFReader#addDeadline(java.lang.String, java.lang.String, net.sf.opendse.model.Application, java.lang.String)}.
	 */
	@Test
	public void testAddDeadline() {

		String line = "HARD_DEADLINE d0_0 ON sink AT 0.0003";
		String suffix = "_0";

		Task task0 = new Task("sink_0");

		Application<Task, Dependency> application = new Application<Task, Dependency>();
		application.addVertex(task0);

		String deadlineType = TGFFReader.HARD_DEADLINE;

		TGFFReader reader = new TGFFReader();
		reader.addDeadline(line, suffix, application, deadlineType);

		Assert.assertNotNull(application.iterator().next().getAttribute(deadlineType));
		Assert.assertEquals(0.0003, application.iterator().next().getAttribute(deadlineType));
	}

	/**
	 * Test method for
	 * {@link net.sf.opendse.io.TGFFReader#importCore(java.lang.String, java.util.Iterator, net.sf.opendse.model.ResourceTypes)}.
	 */
	@Test
	public void testImportCore() {

		ResourceTypes<Resource> resourceTypes = new ResourceTypes<Resource>();

		String name = "@CORE 0 test";
		Iterator<String> it = importFile(testFile, 30).iterator();

		TGFFReader reader = new TGFFReader();
		reader.importCore(name, it, resourceTypes);

		String id = "r0";

		Assert.assertEquals(1, resourceTypes.size());
		Assert.assertTrue(resourceTypes.containsKey(id));

		Resource resource = resourceTypes.get(id);

		Assert.assertEquals("79.0597", resource.getAttribute("price"));
		Assert.assertEquals("0.219023", resource.getAttribute("area"));
	}

	/**
	 * Test method for
	 * {@link net.sf.opendse.io.TGFFReader#importMappings(java.lang.String, java.util.Iterator, net.sf.opendse.model.ResourceTypes, net.sf.opendse.model.Mappings)}.
	 */
	@Test
	public void testImportMappings() {

		List<String> in = importFile(testFile, 0);

		TGFFReader reader = new TGFFReader();
		reader.toApplication(in);
		ResourceTypes<Resource> resourceTypes = reader.toResourceTypes(in);

		Iterator<String> it = importFile(testFile, 30).iterator();
		String name = "@CORE 0 test";

		Mappings<Task, Resource> mappings = new Mappings<Task, Resource>();
		reader.importMappings(name, it, resourceTypes, mappings);

		Assert.assertEquals(4, mappings.size());

		List<String> ids = new ArrayList<String>(
				Arrays.asList("m_t0_0_0_r0", "m_t0_1_0_r0", "m_t0_2_0_r0", "m_t0_3_0_r0"));
		List<String> times = new ArrayList<String>(Arrays.asList("7", "11"));

		for (Mapping<Task, Resource> mapping : mappings) {

			Assert.assertTrue(ids.contains(mapping.getId()));
			Assert.assertTrue(times.contains(mapping.getAttribute("task_time")));
		}
	}

	/**
	 * Test method for
	 * {@link net.sf.opendse.io.TGFFReader#importMessageSizes(java.util.Iterator)}.
	 */
	@Test
	public void testImportMessageSizes() {

		TGFFReader reader = new TGFFReader();

		Iterator<String> it = importFile(testFile, 3).iterator();
		Map<String, Double> messageSizes = reader.importMessageSizes(it);

		Assert.assertEquals(2, messageSizes.size());

		Assert.assertTrue(messageSizes.keySet().contains("0"));
		Assert.assertTrue(messageSizes.keySet().contains("1"));

		Assert.assertEquals(5l, messageSizes.get("0").longValue());
		Assert.assertEquals(6l, messageSizes.get("1").longValue());
	}

	/**
	 * Test method for
	 * {@link net.sf.opendse.io.TGFFReader#importWireLink(java.util.Iterator, net.sf.opendse.model.LinkTypes)}.
	 */
	@Test
	public void testImportWireLink() {

		Iterator<String> it = importFile(testFile, 67).iterator();
		LinkTypes<Link> linkTypes = new LinkTypes<Link>();

		new TGFFReader().importWireLink(it, linkTypes);

		Assert.assertEquals(1, linkTypes.size());
		Assert.assertTrue(linkTypes.containsKey(TGFFReader.WIRE));
		Assert.assertNotNull(linkTypes.get(TGFFReader.WIRE).getAttribute("max_buffer_size"));
	}

	/**
	 * Test method for
	 * {@link net.sf.opendse.io.TGFFReader#importHyperperiod(java.lang.String)}.
	 */
	@Test
	public void testImportHyperperiod() {

		String line = "@HYPERPERIOD 300";
		Double hyperperiod = new TGFFReader().importHyperperiod(line);

		Assert.assertEquals(300l, hyperperiod.longValue());
	}

	/**
	 * Test method for
	 * {@link net.sf.opendse.io.TGFFReader#isComment(java.lang.String)}.
	 */
	@Test
	public void testIsComment() {

		TGFFReader reader = new TGFFReader();

		Assert.assertTrue(reader.isComment("# a comment"));
		Assert.assertTrue(!reader.isComment("not a comment"));
	}

	/**
	 * Test method for
	 * {@link net.sf.opendse.io.TGFFReader#isClosing(java.lang.String)}.
	 */
	@Test
	public void testIsClosing() {

		TGFFReader reader = new TGFFReader();

		Assert.assertTrue(reader.isClosing("closing line: }"));
		Assert.assertTrue(!reader.isClosing("# non-closing line..."));
	}

	/**
	 * Test method for
	 * {@link net.sf.opendse.io.TGFFReader#skip(java.lang.String)}.
	 */
	@Test
	public void testSkip() {

		TGFFReader reader = new TGFFReader();

		Assert.assertTrue(reader.skip("line to be skipped"));
		Assert.assertTrue(!reader.skip(TGFFReader.HEADER));
	}

	public static List<String> importFile(String file, int startingLine) {

		List<String> lines = new ArrayList<String>();

		try {
			BufferedReader br = new BufferedReader(new FileReader(file));

			String currentLine;
			int counter = 0;

			while ((counter < startingLine) && (currentLine = br.readLine()) != null) {
				counter++;
			}

			while ((currentLine = br.readLine()) != null) {
				lines.add(currentLine);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return lines;
	}

}
