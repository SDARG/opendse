package net.sf.opendse.encoding;

import static org.junit.Assert.*;

import org.junit.Test;
import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.Literal;

import net.sf.opendse.encoding.variables.ApplicationVariable;
import net.sf.opendse.encoding.variables.DTT;
import net.sf.opendse.encoding.variables.InterfaceVariable;
import net.sf.opendse.encoding.variables.M;
import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.encoding.variables.Variable;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

public class DefaultImplementationEncodingTest {

	@Test
	public void testExtractVariables() {
		ApplicationEncoding applicationEncoding = mock(ApplicationEncoding.class);
		MappingEncoding mappingEncoding = mock(MappingEncoding.class);
		RoutingEncoding routingEncoding = mock(RoutingEncoding.class);
		AllocationEncoding allocationEncoding = mock(AllocationEncoding.class);
		DefaultImplementationEncoding encoding = new DefaultImplementationEncoding(applicationEncoding, mappingEncoding,
				routingEncoding, allocationEncoding);
		T mockT = mock(T.class);
		DTT mockDTT = mock(DTT.class);
		M mockM = mock(M.class);
		Variable mockVar = mock(Variable.class);
		Set<Literal> literals = new HashSet<Literal>();
		literals.add(new Literal(mockT, true));
		literals.add(new Literal(mockDTT, true));
		literals.add(new Literal(mockM, true));
		literals.add(new Literal(mockVar, true));
		Constraint mockConstraint = mock(Constraint.class);
		when(mockConstraint.getLiterals()).thenReturn(literals);
		Set<Constraint> constraints = new HashSet<Constraint>();
		constraints.add(mockConstraint);
		Set<InterfaceVariable> applVars = encoding.extractVariables(constraints, ApplicationVariable.class);
		Set<InterfaceVariable> mappingVars = encoding.extractVariables(constraints, MappingVariable.class);
		assertEquals(2, applVars.size());
		assertTrue(applVars.contains(mockT));
		assertTrue(applVars.contains(mockDTT));
		assertEquals(1, mappingVars.size());
		assertTrue(mappingVars.contains(mockM));
	}
}
