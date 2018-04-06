package net.sf.opendse.encoding.application;

import static org.junit.Assert.*;

import org.junit.Test;
import org.opt4j.satdecoding.Constraint;

import edu.uci.ics.jung.graph.util.EdgeType;
import net.sf.opendse.encoding.variables.ApplicationVariable;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Application;
import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.properties.ApplicationElementPropertyService;
import net.sf.opendse.model.properties.ApplicationElementPropertyService.ActivationModes;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ApplicationModeEncodingTest {

	@Test
	public void testToConstraints() {
		ApplicationConstraintGeneratorManager generatorManager = mock(ApplicationConstraintGeneratorManager.class);
		Task task = new Task("task");
		Application<Task, Dependency> appl = new Application<Task, Dependency>();
		appl.addVertex(task);
		ApplicationModeConstraintGenerator generator = mock(ApplicationModeConstraintGenerator.class);
		when(generatorManager.getConstraintGenerator(ActivationModes.STATIC)).thenReturn(generator);
		Set<ApplicationVariable> vars = new HashSet<ApplicationVariable>();
		vars.add(Variables.varT(task));
		when(generator.toConstraints(vars)).thenReturn(new HashSet<Constraint>());
		ApplicationModeEncoding encoding = new ApplicationModeEncoding(generatorManager);
		encoding.toConstraints(appl);
		verify(generator).toConstraints(vars);
		verify(generatorManager).getConstraintGenerator(ActivationModes.STATIC);
	}

	@Test
	public void testFilterApplicationModes() {
		ApplicationConstraintGeneratorManager generatorManager = mock(ApplicationConstraintGeneratorManager.class);
		ApplicationModeEncoding encoding = new ApplicationModeEncoding(generatorManager);
		Task t1 = new Task("t1");
		Task t2 = new Communication("t2");
		Task t3 = new Task("t3");
		Task t4 = new Communication("t4");
		ApplicationElementPropertyService.setActivationMode(t1, ActivationModes.ALTERNATIVE);
		ApplicationElementPropertyService.setActivationMode(t2, ActivationModes.ALTERNATIVE);
		Dependency d1 = new Dependency("d1");
		Dependency d2 = new Dependency("d2");
		ApplicationElementPropertyService.setActivationMode(d1, ActivationModes.ALTERNATIVE);
		Application<Task, Dependency> appl = new Application<Task, Dependency>();
		appl.addEdge(d1, t1, t2, EdgeType.DIRECTED);
		appl.addEdge(d2, t3, t4, EdgeType.DIRECTED);
		Map<ActivationModes, Set<ApplicationVariable>> modeMap = encoding.filterApplicationModes(appl);
		assertEquals(2, modeMap.keySet().size());
		assertTrue(modeMap.keySet().contains(ActivationModes.ALTERNATIVE));
		assertTrue(modeMap.keySet().contains(ActivationModes.STATIC));
		assertEquals(3, modeMap.get(ActivationModes.STATIC).size());
		assertTrue(modeMap.get(ActivationModes.STATIC).contains(Variables.varT(t3)));
		assertTrue(modeMap.get(ActivationModes.STATIC).contains(Variables.varT(t4)));
		assertTrue(modeMap.get(ActivationModes.STATIC).contains(Variables.varDTT(d2, t3, t4)));
		assertEquals(3, modeMap.get(ActivationModes.ALTERNATIVE).size());
		assertTrue(modeMap.get(ActivationModes.ALTERNATIVE).contains(Variables.varT(t1)));
		assertTrue(modeMap.get(ActivationModes.ALTERNATIVE).contains(Variables.varT(t2)));
		assertTrue(modeMap.get(ActivationModes.ALTERNATIVE).contains(Variables.varDTT(d1, t1, t2)));
	}
}
