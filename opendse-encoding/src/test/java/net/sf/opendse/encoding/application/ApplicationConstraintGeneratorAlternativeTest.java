package net.sf.opendse.encoding.application;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.opt4j.satdecoding.Constraint;

import edu.uci.ics.jung.graph.util.EdgeType;
import net.sf.opendse.encoding.variables.ApplicationVariable;
import net.sf.opendse.encoding.variables.DTT;
import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Application;
import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.properties.ApplicationElementPropertyService;
import verification.ConstraintVerifier;

public class ApplicationConstraintGeneratorAlternativeTest {

	protected class AlternativeApplication {
		protected Task t0 = new Task("t0");
		protected Task t1 = new Task("t1");
		protected Communication c0 = new Communication("c0");
		protected Communication c1 = new Communication("c1");
		protected Dependency d0 = new Dependency("d0");
		protected Dependency d1 = new Dependency("d1");
		protected Dependency d2 = new Dependency("d2");
		protected Dependency d3 = new Dependency("d3");
		protected Application<Task, Dependency> appl = new Application<Task, Dependency>();
		protected Set<Object> variables = new HashSet<Object>();
		protected Set<ApplicationVariable> applVars = new HashSet<ApplicationVariable>();
		protected Set<ApplicationVariable> func1 = new HashSet<ApplicationVariable>();
		protected Set<ApplicationVariable> func2 = new HashSet<ApplicationVariable>();
		protected T tVar0 = Variables.varT(t0);
		protected T tVar1 = Variables.varT(t1);
		protected T cVar0 = Variables.varT(c0);
		protected T cVar1 = Variables.varT(c1);
		protected DTT dttVar0 = Variables.varDTT(d0, t0, c1);
		protected DTT dttVar1 = Variables.varDTT(d1, t0, c0);
		protected DTT dttVar2 = Variables.varDTT(d2, c0, t1);
		protected DTT dttVar3 = Variables.varDTT(d3, t1, c1);

		protected AlternativeApplication() {
			variables.add(tVar1);
			variables.add(cVar0);
			variables.add(dttVar0);
			variables.add(dttVar1);
			variables.add(dttVar2);
			variables.add(dttVar3);
			applVars.add(tVar1);
			applVars.add(cVar0);
			applVars.add(dttVar0);
			applVars.add(dttVar1);
			applVars.add(dttVar2);
			applVars.add(dttVar3);

			func1.add(dttVar0);
			func2.add(dttVar1);
			func2.add(cVar0);
			func2.add(tVar1);
			func2.add(dttVar3);

			ApplicationElementPropertyService.setActivationMode(d0,
					ApplicationElementPropertyService.activationAttributeAlternative);
			ApplicationElementPropertyService.setActivationMode(d1,
					ApplicationElementPropertyService.activationAttributeAlternative);
			ApplicationElementPropertyService.setActivationMode(d2,
					ApplicationElementPropertyService.activationAttributeAlternative);
			ApplicationElementPropertyService.setActivationMode(d3,
					ApplicationElementPropertyService.activationAttributeAlternative);
			ApplicationElementPropertyService.setActivationMode(c0,
					ApplicationElementPropertyService.activationAttributeAlternative);
			ApplicationElementPropertyService.setActivationMode(t1,
					ApplicationElementPropertyService.activationAttributeAlternative);

			ApplicationElementPropertyService.setAlternativeAttributes(d0, "Function", "A");
			ApplicationElementPropertyService.setAlternativeAttributes(d1, "Function", "B");
			ApplicationElementPropertyService.setAlternativeAttributes(d2, "Function", "B");
			ApplicationElementPropertyService.setAlternativeAttributes(d3, "Function", "B");
			ApplicationElementPropertyService.setAlternativeAttributes(c0, "Function", "B");
			ApplicationElementPropertyService.setAlternativeAttributes(t1, "Function", "B");

			appl.addEdge(d0, t0, c1, EdgeType.DIRECTED);
			appl.addEdge(d1, t0, c0, EdgeType.DIRECTED);
			appl.addEdge(d2, c0, t1, EdgeType.DIRECTED);
			appl.addEdge(d3, t1, c1, EdgeType.DIRECTED);
		}
	}

	@Test
	public void testToConstraints() {
		AlternativeApplication problem = new AlternativeApplication();
		ApplicationConstraintGeneratorAlternative generator = new ApplicationConstraintGeneratorAlternative();
		Set<Constraint> cs = generator.toConstraints(problem.applVars);
		assertEquals(7, cs.size());
		ConstraintVerifier verifyMinimumActivity = new ConstraintVerifier(new HashSet<Object>(), new HashSet<Object>(),
				cs);
		verifyMinimumActivity.verifyAtLeastOneActive(problem.variables);
		Set<Object> active = new HashSet<Object>();
		active.add(problem.dttVar0);
		ConstraintVerifier verifyActivationA = new ConstraintVerifier(active, new HashSet<Object>(), cs);
		for (Object deactivated : problem.func2) {
			verifyActivationA.verifyVariableDeactivated(deactivated);
		}
		active.remove(problem.dttVar0);
		active.add(problem.dttVar1);
		ConstraintVerifier verifyActivationB = new ConstraintVerifier(active, new HashSet<Object>(), cs);
		verifyActivationB.verifyVariableDeactivated(problem.dttVar0);
		for (Object activated : problem.func2) {
			verifyActivationB.verifyVariableActivated(activated);
		}
	}

	@Test
	public void testFillFunctionToIdsMap() {
		AlternativeApplication problem = new AlternativeApplication();
		Map<String, Set<String>> map = new HashMap<String, Set<String>>();
		ApplicationConstraintGeneratorAlternative constraintGenerator = new ApplicationConstraintGeneratorAlternative();
		constraintGenerator.fillFunctionToIdsMap(map, problem.applVars);
		assertEquals(1, map.keySet().size());
		assertTrue(map.containsKey("Function"));
		Set<String> set = map.get("Function");
		assertEquals(2, set.size());
		assertTrue(set.contains("A"));
		assertTrue(set.contains("B"));
	}
}
