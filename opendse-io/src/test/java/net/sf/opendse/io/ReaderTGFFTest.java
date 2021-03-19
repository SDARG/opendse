package net.sf.opendse.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import net.sf.opendse.model.SpecificationTypeBased;
import net.sf.opendse.model.Task;

import static net.sf.opendse.io.CommonTest.epsilon;

public class ReaderTGFFTest {

	protected static final String testFile = "specs/tgff/opendse_example.tgff";

	/**
	 * Test method for {@link net.sf.opendse.io.ReaderTGFF#read(java.lang.String)}.
	 */
	@Test
	public void testReadString() {

		ReaderTGFF reader = new ReaderTGFF();
		SpecificationTypeBased spec = reader.read(testFile);

		Assert.assertNotNull(spec);
	}

	/**
	 * Test method for {@link net.sf.opendse.io.ReaderTGFF#read(java.io.File)}.
	 */
	@Test
	public void testReadFile() {

		ReaderTGFF reader = new ReaderTGFF();
		File file = new File(testFile);
		SpecificationTypeBased spec = reader.read(file);

		Assert.assertNotNull(spec);
	}

	/**
	 * Test method for
	 * {@link net.sf.opendse.io.ReaderTGFF#read(java.io.InputStream)}.
	 */
	@Test
	public void testReadInputStream() {
		ReaderTGFF reader = new ReaderTGFF();
		File file = new File(testFile);

		SpecificationTypeBased spec = null;
		try {
			spec = reader.read(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		Assert.assertNotNull(spec);
	}

	/**
	 * Test method for
	 * {@link net.sf.opendse.io.ReaderTGFF#toSpecification(java.util.List)}.
	 */
	@Test
	public void testToSpecification() {

		List<String> in = Arrays.asList("@HYPERPERIOD 300", "@COMMUN_QUANT 0 {", "# type    quantity", "0          5",
				"1          6", "}",

				"@TASK_GRAPH 0 {", "PERIOD 300", "TASK t0_0	TYPE 1", "TASK t0_1	TYPE 2", "TASK t0_2	TYPE 2",
				"TASK t0_3	TYPE 2", "ARC a0_0 	FROM t0_0  TO  t0_1 TYPE 0", "ARC a0_1 	FROM t0_1  TO  t0_2 TYPE 0",
				"ARC a0_2 	FROM t0_0  TO  t0_3 TYPE 1", "HARD_DEADLINE d0_0 ON t0_2 AT 300",
				"SOFT_DEADLINE d0_1 ON t0_3 AT 200", "}",

				"@CORE 0 {", "# price      area", "79.0597    0.219023", "#-----------",
				"# type version valid      task_time", "0    0       1          10", "1    0       1          7",
				"2    0       1          11", "}",

				"@CORE 1 {", "# price      area", "72.405     0.166029", "#-----------",
				"# type version valid      task_time", "0    0       1          8", "1    0       0          9",
				"2    0       1          11", "}",

				"@CORE 2 {", "# price      area", "97.0382    0.185649", "#-----------",
				"# type version valid      task_time", "0    0       1          10", "1    0       0          8",
				"2    0       1          12", "}",

				"@WIRING 0", "{", "# max_buffer_size", "491", "}");

		SpecificationTypeBased spec = new ReaderTGFF().toSpecification(in);

		Assert.assertNotNull(spec);
		Assert.assertNotNull(spec.getApplication());
		Assert.assertNotNull(spec.getResourceTypes());
		Assert.assertNotNull(spec.getLinkTypes());
		Assert.assertNotNull(spec.getMappings());
	}

	/**
	 * Test method for
	 * {@link net.sf.opendse.io.ReaderTGFF#toApplication(java.util.List)}.
	 */
	@Test
	public void testToApplication() {

		List<String> in = Arrays.asList("@HYPERPERIOD 300", "@COMMUN_QUANT 0 {", "# type    quantity", "0          5",
				"1          6", "}",

				"@TASK_GRAPH 0 {", "PERIOD 300",

				"TASK t0_0	TYPE 1", "TASK t0_1	TYPE 2", "TASK t0_2	TYPE 2", "TASK t0_3	TYPE 2",

				"ARC a0_0 	FROM t0_0  TO  t0_1 TYPE 0", "ARC a0_1 	FROM t0_1  TO  t0_2 TYPE 0",
				"ARC a0_2 	FROM t0_0  TO  t0_3 TYPE 1",

				"HARD_DEADLINE d0_0 ON t0_2 AT 300", "SOFT_DEADLINE d0_1 ON t0_3 AT 200", "}");

		Application<Task, Dependency> application = new ReaderTGFF().toApplication(in);

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
	 * {@link net.sf.opendse.io.ReaderTGFF#toResourceTypes(java.util.List)}.
	 */
	@Test
	public void testToResourceTypes() {

		List<String> in = Arrays.asList("@HYPERPERIOD 300", "@COMMUN_QUANT 0 {", "# type    quantity", "0          5",
				"1          6", "}",

				"@TASK_GRAPH 0 {", "PERIOD 300", "TASK t0_0	TYPE 1", "TASK t0_1	TYPE 2", "TASK t0_2	TYPE 2",
				"TASK t0_3	TYPE 2", "ARC a0_0 	FROM t0_0  TO  t0_1 TYPE 0", "ARC a0_1 	FROM t0_1  TO  t0_2 TYPE 0",
				"ARC a0_2 	FROM t0_0  TO  t0_3 TYPE 1", "HARD_DEADLINE d0_0 ON t0_2 AT 300",
				"SOFT_DEADLINE d0_1 ON t0_3 AT 200", "}",

				"@CORE 0 {", "# price      area", "79.0597    0.219023", "#-----------",
				"# type version valid      task_time", "0    0       1          10", "1    0       1          7",
				"2    0       1          11", "}",

				"@CORE 1 {", "# price      area", "72.405     0.166029", "#-----------",
				"# type version valid      task_time", "0    0       1          8", "1    0       0          9",
				"2    0       1          11", "}",

				"@CORE 2 {", "# price      area", "97.0382    0.185649", "#-----------",
				"# type version valid      task_time", "0    0       1          10", "1    0       0          8",
				"2    0       1          12", "}",

				"@WIRING 0", "{", "# max_buffer_size", "491", "}");

		ReaderTGFF reader = new ReaderTGFF();
		ResourceTypes<Resource> resourceTypes = reader.toResourceTypes(in);

		Assert.assertEquals(3, resourceTypes.values().size());

		for (int i = 0; i < 3; i++) {
			Assert.assertTrue(resourceTypes.containsKey("r" + i));
		}
	}

	/**
	 * Test method for
	 * {@link net.sf.opendse.io.ReaderTGFF#toMappings(java.util.List, net.sf.opendse.model.ResourceTypes)}.
	 */
	@Test
	public void testToMappings() {

		List<String> in = Arrays.asList("@HYPERPERIOD 300", "@COMMUN_QUANT 0 {", "# type    quantity", "0          5",
				"1          6", "}",

				"@TASK_GRAPH 0 {", "PERIOD 300", "TASK t0_0	TYPE 1", "TASK t0_1	TYPE 2", "TASK t0_2	TYPE 2",
				"TASK t0_3	TYPE 2", "ARC a0_0 	FROM t0_0  TO  t0_1 TYPE 0", "ARC a0_1 	FROM t0_1  TO  t0_2 TYPE 0",
				"ARC a0_2 	FROM t0_0  TO  t0_3 TYPE 1", "HARD_DEADLINE d0_0 ON t0_2 AT 300",
				"SOFT_DEADLINE d0_1 ON t0_3 AT 200", "}",

				"@CORE 0 {", "# price      area", "79.0597    0.219023", "#-----------",
				"# type version valid      task_time", "0    0       1          10", "1    0       1          7",
				"2    0       1          11", "}",

				"@CORE 1 {", "# price      area", "72.405     0.166029", "#-----------",
				"# type version valid      task_time", "0    0       1          8", "1    0       0          9",
				"2    0       1          11", "}",

				"@CORE 2 {", "# price      area", "97.0382    0.185649", "#-----------",
				"# type version valid      task_time", "0    0       1          10", "1    0       0          8",
				"2    0       1          12", "}",

				"@WIRING 0", "{", "# max_buffer_size", "491", "}");

		ReaderTGFF reader = new ReaderTGFF();
		reader.toApplication(in);
		ResourceTypes<Resource> resourceTypes = reader.toResourceTypes(in);

		Mappings<Task, Resource> mappings = reader.toMappings(in, resourceTypes);

		Assert.assertEquals(10, mappings.size());

		List<String> ids = new ArrayList<String>(
				Arrays.asList("m_t0_0_0_r0", "m_t0_1_0_r0", "m_t0_2_0_r0", "m_t0_3_0_r0", "m_t0_1_0_r1", "m_t0_2_0_r1",
						"m_t0_3_0_r1", "m_t0_1_0_r2", "m_t0_2_0_r2", "m_t0_3_0_r2"));
		List<Double> times = new ArrayList<Double>(Arrays.asList(7.0, 8.0, 9.0, 11.0, 12.0));

		for (Mapping<Task, Resource> mapping : mappings) {
			Assert.assertTrue(ids.contains(mapping.getId()));
			Assert.assertTrue(times.contains(mapping.getAttribute("task_time")));
		}
	}

	/**
	 * Test method for
	 * {@link net.sf.opendse.io.ReaderTGFF#toLinkTypes(java.util.List)}.
	 */
	@Test
	public void testToLinkTypes() {

		List<String> in = Arrays.asList("@WIRING 0", "{", "# max_buffer_size", "491", "}");
		LinkTypes<Link> linkTypes = new ReaderTGFF().toLinkTypes(in);

		Assert.assertEquals(1, linkTypes.size());
		Assert.assertTrue(linkTypes.containsKey(ReaderTGFF.WIRE));
		Assert.assertNotNull(linkTypes.get(ReaderTGFF.WIRE).getAttribute("max_buffer_size"));
	}

	/**
	 * Test method for
	 * {@link net.sf.opendse.io.ReaderTGFF#importTaskGraph(java.lang.String, java.util.Iterator, net.sf.opendse.model.Application)}.
	 */
	@Test
	public void testImportTaskGraph() {

		List<String> in = Arrays.asList("PERIOD 300", "TASK t0_0	TYPE 1", "TASK t0_1	TYPE 2", "TASK t0_2	TYPE 2",
				"TASK t0_3	TYPE 2",

				"# a comment",

				"ARC a0_0 	FROM t0_0  TO  t0_1 TYPE 0", "ARC a0_1 	FROM t0_1  TO  t0_2 TYPE 0",
				"ARC a0_2 	FROM t0_0  TO  t0_3 TYPE 1", "HARD_DEADLINE d0_0 ON t0_2 AT 300",

				"SOFT_DEADLINE d0_1 ON t0_3 AT 200", "}");

		Iterator<String> it = in.iterator();
		Application<Task, Dependency> application = new Application<Task, Dependency>();
		String line = "@TASK_GRAPH 0 test";

		new ReaderTGFF().importTaskGraph(line, it, application);

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
	 * {@link net.sf.opendse.io.ReaderTGFF#addTask(java.lang.String, java.lang.String, double, net.sf.opendse.model.Application)}.
	 */
	@Test
	public void testAddTask() {

		Application<Task, Dependency> application = new Application<Task, Dependency>();

		String line = "	TASK test0_0 TYPE 0";
		String suffix = "_0";
		double period = 0.0;

		ReaderTGFF reader = new ReaderTGFF();
		reader.addTask(line, suffix, period, application);

		String id = "test0_0_0";

		Assert.assertEquals(1, application.getVertexCount());
		Assert.assertEquals(id, application.iterator().next().getId());
	}

	/**
	 * Test method for
	 * {@link net.sf.opendse.io.ReaderTGFF#addCommunication(java.lang.String, java.lang.String, double, net.sf.opendse.model.Application)}.
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

		ReaderTGFF reader = new ReaderTGFF();

		List<String> in = Arrays.asList("# type    quantity", "0          5", "1          6", "}");
		Iterator<String> it = in.iterator();

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
	 * {@link net.sf.opendse.io.ReaderTGFF#addDeadline(java.lang.String, java.lang.String, net.sf.opendse.model.Application, java.lang.String)}.
	 */
	@Test
	public void testAddDeadline() {

		String line = "HARD_DEADLINE d0_0 ON sink AT 0.0003";
		String suffix = "_0";

		Task task0 = new Task("sink_0");

		Application<Task, Dependency> application = new Application<Task, Dependency>();
		application.addVertex(task0);

		String deadlineType = ReaderTGFF.HARD_DEADLINE;

		ReaderTGFF reader = new ReaderTGFF();
		reader.addDeadline(line, suffix, application, deadlineType);

		Assert.assertNotNull(application.iterator().next().getAttribute(deadlineType));
		Assert.assertEquals(0.0003, application.iterator().next().getAttribute(deadlineType), epsilon);
	}

	/**
	 * Test method for invalid input for
	 * {@link net.sf.opendse.io.ReaderTGFF#addDeadline(java.lang.String, java.lang.String, net.sf.opendse.model.Application, java.lang.String)}.
	 */
	@Test(expected = AssertionError.class)
	public void testAddDeadlineInvalid() {

		String line = "HARD_DEADLINE d0_0 ON sink AT 0.0003 wrong_number_of_entries";
		String suffix = "_0";

		Task task0 = new Task("sink_0");

		Application<Task, Dependency> application = new Application<Task, Dependency>();
		application.addVertex(task0);

		String deadlineType = ReaderTGFF.HARD_DEADLINE;

		ReaderTGFF reader = new ReaderTGFF();
		reader.addDeadline(line, suffix, application, deadlineType);
	}

	/**
	 * Test method for
	 * {@link net.sf.opendse.io.ReaderTGFF#addDeadline(java.lang.String, java.lang.String, net.sf.opendse.model.Application, java.lang.String)}.
	 */
	@Test(expected = AssertionError.class)
	public void testAddDeadlineToNonExistantTask() {

		String line = "HARD_DEADLINE d0_0 ON sink AT 0.0003";
		String suffix = "_0";

		Application<Task, Dependency> application = new Application<Task, Dependency>();

		new ReaderTGFF().addDeadline(line, suffix, application, ReaderTGFF.HARD_DEADLINE);
	}

	/**
	 * Test method for
	 * {@link net.sf.opendse.io.ReaderTGFF#importCore(java.lang.String, java.util.Iterator, net.sf.opendse.model.ResourceTypes)}.
	 */
	@Test
	public void testImportCore() {

		ResourceTypes<Resource> resourceTypes = new ResourceTypes<Resource>();

		String name = "@CORE 0 test";
		List<String> in = Arrays.asList("# price      area", "79.0597    0.219023", "#-----------",
				"# type version valid      task_time", "0    0       1          10", "1    0       1          7",
				"2    0       1          11", "}",

				"@CORE 1 {", "# price      area", "72.405     0.166029", "#-----------",
				"# type version valid      task_time", "0    0       1          8", "1    0       0          9",
				"2    0       1          11", "}",

				"@CORE 2 {", "# price      area", "97.0382    0.185649", "#-----------",
				"# type version valid      task_time", "0    0       1          10", "1    0       0          8",
				"2    0       1          12", "}",

				"@WIRING 0", "{", "# max_buffer_size", "491", "}");
		Iterator<String> it = in.iterator();

		ReaderTGFF reader = new ReaderTGFF();
		reader.importCore(name, it, resourceTypes);

		String id = "r0";

		Assert.assertEquals(1, resourceTypes.size());
		Assert.assertTrue(resourceTypes.containsKey(id));

		Resource resource = resourceTypes.get(id);

		Assert.assertEquals(79.0597, resource.getAttribute("price"), epsilon);
		Assert.assertEquals(0.219023, resource.getAttribute("area"), epsilon);
	}

	/**
	 * Test method for
	 * {@link net.sf.opendse.io.ReaderTGFF#importMappings(java.lang.String, java.util.Iterator, net.sf.opendse.model.ResourceTypes, net.sf.opendse.model.Mappings)}.
	 */
	@Test
	public void testImportMappings() {

		List<String> in = Arrays.asList("@HYPERPERIOD 300", "@COMMUN_QUANT 0 {", "# type    quantity", "0          5",
				"1          6", "}",

				"@TASK_GRAPH 0 {", "PERIOD 300", "TASK t0_0	TYPE 1", "TASK t0_1	TYPE 2", "TASK t0_2	TYPE 2",
				"TASK t0_3	TYPE 2", "ARC a0_0 	FROM t0_0  TO  t0_1 TYPE 0", "ARC a0_1 	FROM t0_1  TO  t0_2 TYPE 0",
				"ARC a0_2 	FROM t0_0  TO  t0_3 TYPE 1", "HARD_DEADLINE d0_0 ON t0_2 AT 300",
				"SOFT_DEADLINE d0_1 ON t0_3 AT 200", "}",

				"@CORE 0 {", "# price      area", "79.0597    0.219023", "#-----------",
				"# type version valid      task_time", "0    0       1          10", "1    0       1          7",
				"2    0       1          11", "}",

				"@CORE 1 {", "# price      area", "72.405     0.166029", "#-----------",
				"# type version valid      task_time", "0    0       1          8", "1    0       0          9",
				"2    0       1          11", "}",

				"@CORE 2 {", "# price      area", "97.0382    0.185649", "#-----------",
				"# type version valid      task_time", "0    0       1          10", "1    0       0          8",
				"2    0       1          12", "}",

				"@WIRING 0", "{", "# max_buffer_size", "491", "}");

		ReaderTGFF reader = new ReaderTGFF();
		reader.toApplication(in);
		ResourceTypes<Resource> resourceTypes = reader.toResourceTypes(in);

		List<String> core = Arrays.asList("# price area", "79.0597 0.219023", "#-----------",
				"# type version valid task_time", "0 0 1 10", "1 0 1 7", "2 0 1 11", "}",

				"@CORE 1", "{", "# price area", "72.405 0.166029", "#-----------", "# type version valid task_time",
				"0 0 1 8", "1 0 0 9", "2 0 1 11", "}",

				"@CORE 2", "{", "# price area", "97.0382 0.185649", "#-----------", "# type version valid task_time",
				"0 0 1 10", "1 0 0 8", "2 0 1 12", "}",

				"@WIRING 0", "{", "# max_buffer_size", "491", "}");

		Iterator<String> it = core.iterator();

		Mappings<Task, Resource> mappings = new Mappings<Task, Resource>();

		String name = "@CORE 0 test";
		reader.importMappings(name, it, resourceTypes, mappings);

		Assert.assertEquals(4, mappings.size());

		List<String> ids = new ArrayList<String>(
				Arrays.asList("m_t0_0_0_r0", "m_t0_1_0_r0", "m_t0_2_0_r0", "m_t0_3_0_r0"));
		List<Double> times = new ArrayList<Double>(Arrays.asList(7.0, 11.0));

		for (Mapping<Task, Resource> mapping : mappings) {

			Assert.assertTrue(ids.contains(mapping.getId()));
			Assert.assertTrue(times.contains(mapping.getAttribute("task_time")));
		}
	}

	/**
	 * Test method for
	 * {@link net.sf.opendse.io.ReaderTGFF#importMessageSizes(java.util.Iterator)}.
	 */
	@Test
	public void testImportMessageSizes() {

		ReaderTGFF reader = new ReaderTGFF();

		List<String> in = Arrays.asList("# type    quantity", "   0     5", "   1    6", "}");
		Iterator<String> it = in.iterator();

		Map<String, Double> messageSizes = reader.importMessageSizes(it);

		Assert.assertEquals(2, messageSizes.size());

		Assert.assertTrue(messageSizes.keySet().contains("0"));
		Assert.assertTrue(messageSizes.keySet().contains("1"));

		Assert.assertEquals(5l, messageSizes.get("0").longValue());
		Assert.assertEquals(6l, messageSizes.get("1").longValue());
	}

	/**
	 * Test method for
	 * {@link net.sf.opendse.io.ReaderTGFF#importWireLink(java.util.Iterator, net.sf.opendse.model.LinkTypes)}.
	 */
	@Test
	public void testImportLink() {

		List<String> in = Arrays.asList("# max_buffer_size", "491", "# a comment }", "}");

		Iterator<String> it = in.iterator();

		LinkTypes<Link> linkTypes = new LinkTypes<Link>();

		new ReaderTGFF().importLink(it, linkTypes);

		Assert.assertEquals(1, linkTypes.size());
		Assert.assertTrue(linkTypes.containsKey(ReaderTGFF.WIRE));
		Assert.assertNotNull(linkTypes.get(ReaderTGFF.WIRE).getAttribute("max_buffer_size"));
	}

	/**
	 * Test method for
	 * {@link net.sf.opendse.io.ReaderTGFF#importHyperperiod(java.lang.String)}.
	 */
	@Test
	public void testImportHyperperiod() {

		String line = "@HYPERPERIOD 300";
		Double hyperperiod = new ReaderTGFF().importHyperperiod(line);

		Assert.assertEquals(300l, hyperperiod.longValue());
	}

	/**
	 * Test method for
	 * {@link net.sf.opendse.io.ReaderTGFF#isComment(java.lang.String)}.
	 */
	@Test
	public void testIsComment() {

		ReaderTGFF reader = new ReaderTGFF();

		Assert.assertTrue(reader.isComment("# a comment"));
		Assert.assertFalse(reader.isComment("not a comment"));
	}

	/**
	 * Test method for
	 * {@link net.sf.opendse.io.ReaderTGFF#isClosing(java.lang.String)}.
	 */
	@Test
	public void testIsClosing() {

		ReaderTGFF reader = new ReaderTGFF();

		Assert.assertTrue(reader.isClosing("closing line: }"));
		Assert.assertFalse(reader.isClosing("# non-closing line..."));
	}

	/**
	 * Test method for {@link net.sf.opendse.io.ReaderTGFF#skip(java.lang.String)}.
	 */
	@Test
	public void testSkip() {

		ReaderTGFF reader = new ReaderTGFF();

		Assert.assertTrue(reader.skip("line to be skipped"));
		Assert.assertFalse(reader.skip(ReaderTGFF.HEADER));
	}
}
