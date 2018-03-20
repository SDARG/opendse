package net.sf.opendse.encoding.application;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.opt4j.satdecoding.Constraint;

import net.sf.opendse.encoding.variables.ApplicationVariable;
import net.sf.opendse.encoding.variables.DTT;
import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Task;
import verification.ConstraintVerifier;

public class StaticModeConstraintGeneratorTest {

	@Test
	public void test() {
		Task t1 = new Task("t1");
		Task t2 = new Task("t2");
		Dependency dep = new Dependency("dep");
		Set<ApplicationVariable> applVars = new HashSet<ApplicationVariable>();
		T tVar1 = Variables.var(t1);
		T tVar2 = Variables.var(t2);
		DTT dttVar = Variables.var(dep, t1, t2);
		applVars.add(tVar1);
		applVars.add(tVar2);
		applVars.add(dttVar);
		Set<Constraint> constraints = new HashSet<Constraint>();
		StaticModeConstraintGenerator generator = new StaticModeConstraintGenerator();
		generator.toConstraints(applVars, constraints);
		ConstraintVerifier verifier = new ConstraintVerifier(new HashSet<Object>(), new HashSet<Object>(), constraints);
		verifier.verifyVariableActivated(tVar1);
		verifier.verifyVariableActivated(tVar2);
		verifier.verifyVariableActivated(dttVar);
	}
}
