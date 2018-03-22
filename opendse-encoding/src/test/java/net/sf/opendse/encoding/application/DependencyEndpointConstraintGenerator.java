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

public class DependencyEndpointConstraintGenerator {

	@Test
	public void test() {
		Task t1 = new Task("t1");
		Task t2 = new Communication("t2");
		Dependency dep = new Dependency("dep");
		T tVar1 = Variables.var(t1);
		T tVar2 = Variables.var(t2);
		DTT dttVar = Variables.var(dep, t1, t2);
		Set<Object> deactivated = new HashSet<Object>();
		deactivated.add(tVar2);
		deactivated.add(tVar1);
		DependencyEndPointConstraintGenerator generator = new DependencyEndPointConstraintGenerator();
		Set<Constraint> input = new HashSet<Constraint>();
		Set<ApplicationVariable> applVars = new HashSet<ApplicationVariable>();
		applVars.add(dttVar);
		applVars.add(tVar2);
		applVars.add(tVar1);
		generator.toConstraints(applVars, input);
		assertEquals(2, input.size());
		ConstraintVerifier bothInactive = new ConstraintVerifier(new HashSet<Object>(), deactivated, input);
		bothInactive.verifyVariableDeactivated(dttVar);
		deactivated.remove(tVar1);
		ConstraintVerifier oneInactive1 = new ConstraintVerifier(new HashSet<Object>(), deactivated, input);
		oneInactive1.verifyVariableDeactivated(dttVar);
		deactivated.remove(tVar2);
		deactivated.add(tVar1);
		ConstraintVerifier oneInactive2 = new ConstraintVerifier(new HashSet<Object>(), deactivated, input);
		oneInactive2.verifyVariableDeactivated(dttVar);
	}

}
