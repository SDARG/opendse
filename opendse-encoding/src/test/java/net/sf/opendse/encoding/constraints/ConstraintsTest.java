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
	public void testAndVariable() {
		Variable a = mock(Variable.class);
		Variable b = mock(Variable.class);
		Set<Constraint> cs = new HashSet<Constraint>();
		Set<Variable> arguments = new HashSet<Variable>();
		arguments.add(a);
		arguments.add(b);
		Variable and = Constraints.generateAndVariable(arguments, cs);
		ConstraintVerifier verifyOneActive = new ConstraintVerifier(cs);
		verifyOneActive.activateVariable(a);
		verifyOneActive.deactivateVariable(b);
		verifyOneActive.verifyVariableDeactivated(and);
		ConstraintVerifier verifyBothActive = new ConstraintVerifier(cs);
		verifyBothActive.activateVariable(a);
		verifyBothActive.activateVariable(b);
		verifyBothActive.verifyVariableActivated(and);
	}

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
		verifyFreedom.verifyVariableDeactivated(b);
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
	public void testGenerateEqualityConstraint() {
		Variable var1 = mock(Variable.class);
		Variable var2 = mock(Variable.class);
		Set<Constraint> cs = new HashSet<Constraint>();
		cs.add(Constraints.generateEqualityConstraint(var1, var2));
		Set<Object> active = new HashSet<Object>();
		assertEquals(2, cs.iterator().next().size());
		active.add(var1);
		ConstraintVerifier verifyActive1 = new ConstraintVerifier(active, new HashSet<Object>(), cs);
		verifyActive1.verifyVariableActivated(var2);
		active.remove(var1);
		active.add(var2);
		ConstraintVerifier verifyActive2 = new ConstraintVerifier(active, new HashSet<Object>(), cs);
		verifyActive2.verifyVariableActivated(var1);
		Set<Object> unactive = new HashSet<Object>();
		unactive.add(var1);
		ConstraintVerifier verifyUnactive1 = new ConstraintVerifier(new HashSet<Object>(), unactive, cs);
		verifyUnactive1.verifyVariableDeactivated(var2);
		unactive.remove(var1);
		unactive.add(var2);
		ConstraintVerifier verifyUnactive2 = new ConstraintVerifier(new HashSet<Object>(), unactive, cs);
		verifyUnactive2.verifyVariableDeactivated(var1);
	}

	@Test
	public void testPickNConstraint() {
		Variable var1 = mock(Variable.class);
		Variable var2 = mock(Variable.class);
		Set<Variable> vars = new HashSet<Variable>();
		vars.add(var1);
		vars.add(var2);
		Constraint c = Constraints.generatePickExactlyNConstraint(vars, 1);
		assertEquals(2, c.size());
		Set<Constraint> cs = new HashSet<Constraint>();
		cs.add(c);
		Set<Object> active = new HashSet<Object>();
		active.add(var1);
		ConstraintVerifier verifier1 = new ConstraintVerifier(active, new HashSet<Object>(), cs);
		verifier1.verifyVariableDeactivated(var2);
		c = Constraints.generatePickExactlyNConstraint(vars, 2);
		cs.clear();
		cs.add(c);
		ConstraintVerifier verifier2 = new ConstraintVerifier(active, new HashSet<Object>(), cs);
		verifier2.verifyVariableActivated(var2);
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
		ConstraintVerifier verifyDeactivation = new ConstraintVerifier(new HashSet<Object>(), deactivated,
				minimalRequirement);
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
