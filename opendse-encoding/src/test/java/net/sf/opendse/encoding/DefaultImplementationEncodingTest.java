package net.sf.opendse.encoding;

import static org.junit.Assert.*;

import org.junit.Test;
import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.Literal;

import net.sf.opendse.encoding.variables.ApplicationVariable;
import net.sf.opendse.encoding.variables.CR;
import net.sf.opendse.encoding.variables.DTT;
import net.sf.opendse.encoding.variables.InterfaceVariable;
import net.sf.opendse.encoding.variables.L;
import net.sf.opendse.encoding.variables.M;
import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.encoding.variables.Variable;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Application;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Routings;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;
import net.sf.opendse.optimization.SpecificationWrapper;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;

import java.util.HashSet;
import java.util.Set;

public class DefaultImplementationEncodingTest {

	@SuppressWarnings("unchecked")
	@Test
	public void testToConstraints() {
		Constraint applConstraint = new Constraint();
		applConstraint.add(Variables.p(Variables.varT(new Task("t"))));
		Constraint mappingConstraint = new Constraint();
		mappingConstraint.add(Variables.p(mock(M.class)));
		Constraint routingConstraint = new Constraint();
		routingConstraint.add(Variables.n(mock(CR.class)));
		Constraint allocationConstraint = new Constraint();
		allocationConstraint.add(Variables.p(mock(L.class)));
		Set<Constraint> applCs = new HashSet<Constraint>();
		applCs.add(applConstraint);
		Set<Constraint> mappingCs = new HashSet<Constraint>();
		mappingCs.add(mappingConstraint);
		Set<Constraint> routingConstraints = new HashSet<Constraint>();
		routingConstraints.add(routingConstraint);
		Set<Constraint> allocationConstraints = new HashSet<Constraint>();
		allocationConstraints.add(allocationConstraint);
		ApplicationEncoding applicationEncoding = mock(ApplicationEncoding.class);
		MappingEncoding mappingEncoding = mock(MappingEncoding.class);
		RoutingEncoding routingEncoding = mock(RoutingEncoding.class);
		AllocationEncoding allocationEncoding = mock(AllocationEncoding.class);
		Architecture<Resource, Link> arch = new Architecture<Resource, Link>();
		Application<Task, Dependency> appl = new Application<Task, Dependency>();
		Mappings<Task, Resource> mappings = new Mappings<Task, Resource>();
		Specification spec = new Specification(appl, arch, mappings);
		SpecificationWrapper wrapper = mock(SpecificationWrapper.class);
		when(wrapper.getSpecification()).thenReturn(spec);
		when(applicationEncoding.toConstraints(appl)).thenReturn(applCs);
		when(mappingEncoding.toConstraints(any(Mappings.class), any(Set.class))).thenReturn(mappingCs);
		when(routingEncoding.toConstraints(any(Set.class), any(Set.class), any(Routings.class)))
				.thenReturn(routingConstraints);
		when(allocationEncoding.toConstraints(any(Set.class), any(Set.class), any(Architecture.class)))
				.thenReturn(allocationConstraints);
		DefaultImplementationEncoding encoding = new DefaultImplementationEncoding(applicationEncoding, mappingEncoding,
				routingEncoding, allocationEncoding, wrapper);
		Set<Constraint> cs = encoding.toConstraints();
		assertEquals(4, cs.size());
		verify(applicationEncoding).toConstraints(appl);
		verify(mappingEncoding).toConstraints(any(Mappings.class), any(Set.class));
		verify(routingEncoding).toConstraints(any(Set.class), any(Set.class), any(Routings.class));
		verify(allocationEncoding).toConstraints(any(Set.class), any(Set.class), any(Architecture.class));
		assertEquals(4, encoding.getInterfaceVariables().size());
	}

	@Test
	public void testExtractVariables() {
		ApplicationEncoding applicationEncoding = mock(ApplicationEncoding.class);
		MappingEncoding mappingEncoding = mock(MappingEncoding.class);
		RoutingEncoding routingEncoding = mock(RoutingEncoding.class);
		AllocationEncoding allocationEncoding = mock(AllocationEncoding.class);
		SpecificationWrapper wrapper = mock(SpecificationWrapper.class);
		when(wrapper.getSpecification()).thenReturn(mock(Specification.class));
		DefaultImplementationEncoding encoding = new DefaultImplementationEncoding(applicationEncoding, mappingEncoding,
				routingEncoding, allocationEncoding, wrapper);
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
