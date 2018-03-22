package net.sf.opendse.encoding.application;

import static org.junit.Assert.assertEquals;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.opt4j.satdecoding.Constraint;

import net.sf.opendse.encoding.variables.ApplicationVariable;
import net.sf.opendse.encoding.variables.DTT;
import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Task;
import verification.ConstraintVerifier;

public class StaticModeConstraintGeneratorTest {

	@Test
	public void test() {
		Task t1 = new Task("t1");
		Task t2 = new Communication("t2");
		Dependency dep = new Dependency("dep");
		Set<ApplicationVariable> applVars = new HashSet<ApplicationVariable>();
		T tVar1 = Variables.var(t1);
		T tVar2 = Variables.var(t2);
		DTT dttVar = Variables.var(dep, t1, t2);
		applVars.add(tVar1);
		applVars.add(tVar2);
		applVars.add(dttVar);
		StaticModeConstraintGenerator generator = new StaticModeConstraintGenerator();
		Set<Constraint> constraints = generator.toConstraints(applVars);
		assertEquals(3, constraints.size());
		ConstraintVerifier verifier = new ConstraintVerifier(new HashSet<Object>(), new HashSet<Object>(), constraints);
		verifier.verifyVariableActivated(tVar1);
		verifier.verifyVariableActivated(tVar2);
		verifier.verifyVariableActivated(dttVar);
	}
}
