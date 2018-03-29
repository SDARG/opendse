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
