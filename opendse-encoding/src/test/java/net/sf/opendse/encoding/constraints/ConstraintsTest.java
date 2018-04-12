package net.sf.opendse.encoding.constraints;

import static org.junit.Assert.*;

import org.junit.Test;
import org.opt4j.satdecoding.Constraint;

import net.sf.opendse.encoding.variables.Variable;
import verification.ConstraintVerifier;

import static org.mockito.Mockito.mock;

import java.util.HashSet;
import java.util.Set;

public class ConstraintsTest {

	@Test
	public void testNegativeImplication() {
		Variable a = mock(Variable.class);
		Variable b = mock(Variable.class);
		Set<Constraint> negImplication = new HashSet<Constraint>();
		negImplication.add(Constraints.generateNegativeImplication(a, b));
		assertEquals(1, negImplication.size());
		Set<Object> activated = new HashSet<Object>();
		activated.add(a);
		ConstraintVerifier verifyImplication = new ConstraintVerifier(activated, new HashSet<Object>(), negImplication);
		verifyImplication.verifyVariableNotFixed(b);
		ConstraintVerifier verifyFreedom = new ConstraintVerifier(new HashSet<Object>(), activated, negImplication);
		verifyFreedom.verifyVariableDeactivated(b);;
	}
	
	@Test
	public void testDistributedActivationConstraint() {
		Variable a = mock(Variable.class);
		Variable b = mock(Variable.class);
		Set<Variable> conditions = new HashSet<Variable>();
		conditions.add(a);
		conditions.add(b);
		Variable c = mock(Variable.class);
		Set<Constraint> distributedActivation = new HashSet<Constraint>();
		distributedActivation.add(Constraints.generateDistributedActivationConstraint(conditions, c));
		assertEquals(1, distributedActivation.size());
		Set<Object> activated = new HashSet<Object>();
		Set<Object> deactivated = new HashSet<Object>();
		activated.add(a);
		activated.add(b);
		ConstraintVerifier verifyDeactivation = new ConstraintVerifier(activated, deactivated, distributedActivation);
		verifyDeactivation.verifyVariableActivated(c);
		activated.remove(a);
		deactivated.add(a);
		ConstraintVerifier verifyFreedom = new ConstraintVerifier(activated, deactivated, distributedActivation);
		verifyFreedom.verifyVariableNotFixed(c);
	}
	
	@Test
	public void testAndConstraints() {
		Variable a = mock(Variable.class);
		Variable b = mock(Variable.class);
		Set<Variable> conditions = new HashSet<Variable>();
		conditions.add(a);
		conditions.add(b);
		Variable c = mock(Variable.class);
		Set<Constraint> andConstraints = Constraints.generateAndConstraints(conditions, c);
		assertEquals(3, andConstraints.size());
		Set<Object> activated = new HashSet<Object>();
		activated.add(a);
		Set<Object> deactivated = new HashSet<Object>();
		deactivated.add(b);
		ConstraintVerifier verifyOneDeactivated = new ConstraintVerifier(activated, deactivated, andConstraints);
		verifyOneDeactivated.verifyVariableDeactivated(c);
		activated.remove(a);
		deactivated.add(a);
		ConstraintVerifier verifyDeactivation = new ConstraintVerifier(activated, deactivated, andConstraints);
		verifyDeactivation.verifyVariableDeactivated(c);
		deactivated.clear();
		activated.add(a);
		activated.add(b);
		ConstraintVerifier verifyActivation = new ConstraintVerifier(activated, deactivated, andConstraints);
		verifyActivation.verifyVariableActivated(c);
	}
	
	@Test
	public void testOrConstraints() {
		Variable a = mock(Variable.class);
		Variable b = mock(Variable.class);
		Set<Variable> conditions = new HashSet<Variable>();
		conditions.add(a);
		conditions.add(b);
		Variable c = mock(Variable.class);
		Set<Constraint> orConstraints = Constraints.generateOrConstraints(conditions, c);
		assertEquals(3, orConstraints.size());
		Set<Object> activated = new HashSet<Object>();
		activated.add(a);
		Set<Object> deactivated = new HashSet<Object>();
		deactivated.add(b);
		ConstraintVerifier verifyActivation = new ConstraintVerifier(activated, deactivated, orConstraints);
		verifyActivation.verifyVariableActivated(c);
		activated.remove(a);
		deactivated.add(a);
		ConstraintVerifier verifyDeactivation = new ConstraintVerifier(activated, deactivated, orConstraints);
		verifyDeactivation.verifyVariableDeactivated(c);
	}
	
	@Test
	public void testMinimalRequirement() {
		Variable a = mock(Variable.class);
		Variable b = mock(Variable.class);
		Set<Variable> conditions = new HashSet<Variable>();
		conditions.add(a);
		conditions.add(b);
		Variable c = mock(Variable.class);
		Set<Constraint> minimalRequirement = new HashSet<Constraint>();
		minimalRequirement.add(Constraints.generateMinimalRequirementConstraint(conditions, c));
		assertEquals(1, minimalRequirement.size());
		Set<Object> deactivated = new HashSet<Object>();
		deactivated.add(a);
		deactivated.add(b);
		ConstraintVerifier verifyDeactivation = new ConstraintVerifier(new HashSet<Object>(), deactivated, minimalRequirement);
		verifyDeactivation.verifyVariableDeactivated(c);
		deactivated.remove(a);
		Set<Object> activated = new HashSet<Object>();
		activated.add(a);
		ConstraintVerifier verifyFreedom = new ConstraintVerifier(activated, deactivated, minimalRequirement);
		verifyFreedom.verifyVariableNotFixed(c);
	}
	
	@Test
	public void testPositiveImplication() {
		Variable a = mock(Variable.class);
		Variable b = mock(Variable.class);
		Set<Constraint> posImplication = new HashSet<Constraint>();
		posImplication.add(Constraints.generatePositiveImplication(a, b));
		assertEquals(1, posImplication.size());
		Set<Object> activated = new HashSet<Object>();
		activated.add(a);
		ConstraintVerifier verifyImplication = new ConstraintVerifier(activated, new HashSet<Object>(), posImplication);
		verifyImplication.verifyVariableActivated(b);
		ConstraintVerifier verifyFreedom = new ConstraintVerifier(new HashSet<Object>(), activated, posImplication);
		verifyFreedom.verifyVariableNotFixed(b);
	}

}
