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
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ApplicationEncodingModeTest {

	@Test
	public void testToConstraints() {
		ApplicationConstraintManager generatorManager = mock(ApplicationConstraintManager.class);
		Task task = new Task("task");
		Application<Task, Dependency> appl = new Application<Task, Dependency>();
		appl.addVertex(task);
		ApplicationConstraintGenerator generator = mock(ApplicationConstraintGenerator.class);
		when(generatorManager.getConstraintGenerator(ApplicationElementPropertyService.activationAttributeStatic))
				.thenReturn(generator);
		Set<ApplicationVariable> vars = new HashSet<ApplicationVariable>();
		vars.add(Variables.varT(task));
		when(generator.toConstraints(vars)).thenReturn(new HashSet<Constraint>());
		ApplicationEncodingMode encoding = new ApplicationEncodingMode(generatorManager);
		encoding.toConstraints(appl);
		verify(generator).toConstraints(vars);
		verify(generatorManager).getConstraintGenerator(ApplicationElementPropertyService.activationAttributeStatic);
	}

	@Test
	public void testFilterApplicationModes() {
		ApplicationConstraintManager generatorManager = mock(ApplicationConstraintManager.class);
		ApplicationEncodingMode encoding = new ApplicationEncodingMode(generatorManager);
		Task t1 = new Task("t1");
		Task t2 = new Communication("t2");
		Task t3 = new Task("t3");
		Task t4 = new Communication("t4");
		ApplicationElementPropertyService.setActivationMode(t1,
				ApplicationElementPropertyService.activationAttributeAlternative);
		ApplicationElementPropertyService.setActivationMode(t2,
				ApplicationElementPropertyService.activationAttributeAlternative);
		Dependency d1 = new Dependency("d1");
		Dependency d2 = new Dependency("d2");
		ApplicationElementPropertyService.setActivationMode(d1,
				ApplicationElementPropertyService.activationAttributeAlternative);
		Application<Task, Dependency> appl = new Application<Task, Dependency>();
		appl.addEdge(d1, t1, t2, EdgeType.DIRECTED);
		appl.addEdge(d2, t3, t4, EdgeType.DIRECTED);
		Map<String, Set<ApplicationVariable>> modeMap = encoding.filterApplicationModes(appl);
		assertEquals(2, modeMap.keySet().size());
		assertTrue(modeMap.keySet().contains(ApplicationElementPropertyService.activationAttributeAlternative));
		assertTrue(modeMap.keySet().contains(ApplicationElementPropertyService.activationAttributeStatic));
		assertEquals(3, modeMap.get(ApplicationElementPropertyService.activationAttributeStatic).size());
		assertTrue(
				modeMap.get(ApplicationElementPropertyService.activationAttributeStatic).contains(Variables.varT(t3)));
		assertTrue(
				modeMap.get(ApplicationElementPropertyService.activationAttributeStatic).contains(Variables.varT(t4)));
		assertTrue(modeMap.get(ApplicationElementPropertyService.activationAttributeStatic)
				.contains(Variables.varDTT(d2, t3, t4)));
		assertEquals(3, modeMap.get(ApplicationElementPropertyService.activationAttributeAlternative).size());
		assertTrue(modeMap.get(ApplicationElementPropertyService.activationAttributeAlternative)
				.contains(Variables.varT(t1)));
		assertTrue(modeMap.get(ApplicationElementPropertyService.activationAttributeAlternative)
				.contains(Variables.varT(t2)));
		assertTrue(modeMap.get(ApplicationElementPropertyService.activationAttributeAlternative)
				.contains(Variables.varDTT(d1, t1, t2)));
	}
}
