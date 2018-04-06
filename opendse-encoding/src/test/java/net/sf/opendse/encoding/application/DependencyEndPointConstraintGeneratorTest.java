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

public class DependencyEndPointConstraintGeneratorTest {

	@Test
	public void test() {
		Task t1 = new Task("t1");
		Task t2 = new Communication("t2");
		Dependency dep = new Dependency("dep");
		T tVar1 = Variables.varT(t1);
		T tVar2 = Variables.varT(t2);
		DTT dttVar = Variables.varDTT(dep, t1, t2);
		Set<Object> deactivated = new HashSet<Object>();
		deactivated.add(tVar2);
		deactivated.add(tVar1);
		DependencyEndPointConstraintGenerator generator = new DependencyEndPointConstraintGenerator();
		Set<ApplicationVariable> applVars = new HashSet<ApplicationVariable>();
		applVars.add(dttVar);
		applVars.add(tVar2);
		applVars.add(tVar1);
		Set<Constraint> constraints = generator.toConstraints(applVars);
		assertEquals(2, constraints.size());
		ConstraintVerifier bothInactive = new ConstraintVerifier(new HashSet<Object>(), deactivated, constraints);
		bothInactive.verifyVariableDeactivated(dttVar);
		deactivated.remove(tVar1);
		ConstraintVerifier oneInactive1 = new ConstraintVerifier(new HashSet<Object>(), deactivated, constraints);
		oneInactive1.verifyVariableDeactivated(dttVar);
		deactivated.remove(tVar2);
		deactivated.add(tVar1);
		ConstraintVerifier oneInactive2 = new ConstraintVerifier(new HashSet<Object>(), deactivated, constraints);
		oneInactive2.verifyVariableDeactivated(dttVar);
	}
}
