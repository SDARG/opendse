package net.sf.opendse.encoding.application;

import static org.junit.Assert.*;

import org.junit.Test;
import org.opt4j.satdecoding.Constraint;

import edu.uci.ics.jung.graph.util.EdgeType;
import net.sf.opendse.encoding.variables.ApplicationVariable;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Application;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.properties.DependencyPropertyService;
import net.sf.opendse.model.properties.TaskPropertyService;
import net.sf.opendse.model.properties.TaskPropertyService.ActivationModes;

import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DefaultApplicationEncodingTest {

	@Test
	public void testToConstraints() {
		DependencyConstraintGenerator dependencyTaskConstraintGenerator = mock(
				DependencyConstraintGenerator.class);
		ApplicationConstraintGeneratorManager generatorManager = mock(ApplicationConstraintGeneratorManager.class);
		Task task = new Task("task");
		Application<Task, Dependency> appl = new Application<Task, Dependency>();
		appl.addVertex(task);
		ApplicationModeConstraintGenerator generator = mock(ApplicationModeConstraintGenerator.class);
		when(generatorManager.getConstraintGenerator(ActivationModes.STATIC.getXmlName())).thenReturn(generator);
		Set<ApplicationVariable> vars = new HashSet<ApplicationVariable>();
		vars.add(Variables.var(task));
		when(generator.toConstraints(vars, new HashSet<Constraint>())).thenReturn(new HashSet<ApplicationVariable>());
		DefaultApplicationEncoding encoding = new DefaultApplicationEncoding(dependencyTaskConstraintGenerator,
				generatorManager);
		encoding.toConstraints(appl, new HashSet<Constraint>());
		verify(generator).toConstraints(vars, new HashSet<Constraint>());
		verify(generatorManager).getConstraintGenerator(ActivationModes.STATIC.getXmlName());
		verify(dependencyTaskConstraintGenerator).toConstraints(new HashSet<ApplicationVariable>(),
				new HashSet<Constraint>());
	}

	@Test
	public void testFilterApplicationModes() {
		DependencyConstraintGenerator dependencyTaskConstraintGenerator = mock(
				DependencyConstraintGenerator.class);
		ApplicationConstraintGeneratorManager generatorManager = mock(ApplicationConstraintGeneratorManager.class);
		DefaultApplicationEncoding encoding = new DefaultApplicationEncoding(dependencyTaskConstraintGenerator,
				generatorManager);
		Task t1 = new Task("t1");
		Task t2 = new Task("t2");
		Task t3 = new Task("t3");
		Task t4 = new Task("t4");
		TaskPropertyService.setActivationMode(t1, ActivationModes.ALTERNATIVE);
		TaskPropertyService.setActivationMode(t2, ActivationModes.ALTERNATIVE);
		Dependency d1 = new Dependency("d1");
		Dependency d2 = new Dependency("d2");
		DependencyPropertyService.setActivationMode(d1,
				net.sf.opendse.model.properties.DependencyPropertyService.ActivationModes.ALTERNATIVE);
		Application<Task, Dependency> appl = new Application<Task, Dependency>();
		appl.addEdge(d1, t1, t2, EdgeType.DIRECTED);
		appl.addEdge(d2, t3, t4, EdgeType.DIRECTED);
		Map<String, Set<ApplicationVariable>> modeMap = encoding.filterApplicationModes(appl);
		assertEquals(2, modeMap.keySet().size());
		assertTrue(modeMap.keySet().contains(ActivationModes.ALTERNATIVE.getXmlName()));
		assertTrue(modeMap.keySet().contains(ActivationModes.STATIC.getXmlName()));
		assertEquals(3, modeMap.get(ActivationModes.STATIC.getXmlName()).size());
		assertTrue(modeMap.get(ActivationModes.STATIC.getXmlName()).contains(Variables.var(t3)));
		assertTrue(modeMap.get(ActivationModes.STATIC.getXmlName()).contains(Variables.var(t4)));
		assertTrue(modeMap.get(ActivationModes.STATIC.getXmlName()).contains(Variables.var(d2, t3, t4)));
		assertEquals(3, modeMap.get(ActivationModes.ALTERNATIVE.getXmlName()).size());
		assertTrue(modeMap.get(ActivationModes.ALTERNATIVE.getXmlName()).contains(Variables.var(t1)));
		assertTrue(modeMap.get(ActivationModes.ALTERNATIVE.getXmlName()).contains(Variables.var(t2)));
		assertTrue(modeMap.get(ActivationModes.ALTERNATIVE.getXmlName()).contains(Variables.var(d1, t1, t2)));
	}

}
