package net.sf.opendse.encoding.routing;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;

import org.junit.Test;
import org.opt4j.satdecoding.Constraint;

import net.sf.opendse.encoding.variables.ApplicationVariable;
import net.sf.opendse.encoding.variables.DTT;
import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Routings;
import net.sf.opendse.model.Task;

public class FlexibleRoutingEncodingTest {

	@SuppressWarnings("unchecked")
	@Test
	public void testToConstraints() {
		Task t1 = new Task("t1");
		Task t2 = new Task("t2");
		Task t3 = new Task("t3");
		Communication comm = new Communication("comm");
		Dependency d1 = new Dependency("d1");
		Dependency d2 = new Dependency("d2");
		Dependency d3 = new Dependency("d3");
		Set<ApplicationVariable> applVars = new HashSet<ApplicationVariable>();
		T tvar1 = Variables.varT(t1);
		applVars.add(tvar1);
		T tvar2 = Variables.varT(t2);
		applVars.add(tvar2);
		T tvar3 = Variables.varT(t3);
		applVars.add(tvar3);
		T commVar = Variables.varT(comm);
		applVars.add(commVar);
		DTT dttVar1 = Variables.varDTT(d1, t1, comm);
		applVars.add(dttVar1);
		DTT dttVar2 = Variables.varDTT(d2, comm, t2);
		applVars.add(dttVar2);
		DTT dttVar3 = Variables.varDTT(d3, comm, t3);
		applVars.add(dttVar3);
		Set<DTT> dependencyVariables = new HashSet<DTT>();
		dependencyVariables.add(dttVar1);
		dependencyVariables.add(dttVar2);
		dependencyVariables.add(dttVar3);

		CommunicationRoutingEncoderManager routingEncoderManager = mock(CommunicationRoutingEncoderManager.class);
		CommunicationRoutingEncoder routingEncoder = mock(CommunicationRoutingEncoder.class);
		when(routingEncoderManager.getRoutingEncoder(commVar, dependencyVariables)).thenReturn(routingEncoder);
		when(routingEncoder.toConstraints(any(T.class), any(Set.class), any(Architecture.class),
				any(Set.class))).thenReturn(new HashSet<Constraint>());
		FlexibleRoutingEncoding routingEncoding = new FlexibleRoutingEncoding(routingEncoderManager);
		Set<Constraint> constraints = routingEncoding.toConstraints(applVars, new HashSet<MappingVariable>(),
				new Routings<Task, Resource, Link>());
		assertTrue(constraints.isEmpty());
		verify(routingEncoderManager).getRoutingEncoder(commVar, dependencyVariables);
		verify(routingEncoder).toConstraints(any(T.class), any(Set.class), any(Architecture.class),
				any(Set.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUnknownApplVar() {
		Set<ApplicationVariable> applVars = new HashSet<ApplicationVariable>();
		ApplicationVariable unknown = mock(ApplicationVariable.class);
		applVars.add(unknown);
		CommunicationRoutingEncoderManager routingEncoderManager = mock(CommunicationRoutingEncoderManager.class);
		FlexibleRoutingEncoding routingEncoding = new FlexibleRoutingEncoding(routingEncoderManager);
		routingEncoding.makeDependencyMap(applVars);
	}

	@Test
	public void testFillDependencyMap() {
		Task t1 = new Task("t1");
		Task t2 = new Task("t2");
		Task t3 = new Task("t3");
		Communication comm = new Communication("comm");
		Dependency d1 = new Dependency("d1");
		Dependency d2 = new Dependency("d2");
		Dependency d3 = new Dependency("d3");
		Set<ApplicationVariable> applVars = new HashSet<ApplicationVariable>();
		T tvar1 = Variables.varT(t1);
		applVars.add(tvar1);
		T tvar2 = Variables.varT(t2);
		applVars.add(tvar2);
		T tvar3 = Variables.varT(t3);
		applVars.add(tvar3);
		T commVar = Variables.varT(comm);
		applVars.add(commVar);
		DTT dttVar1 = Variables.varDTT(d1, t1, comm);
		applVars.add(dttVar1);
		DTT dttVar2 = Variables.varDTT(d2, comm, t2);
		applVars.add(dttVar2);
		DTT dttVar3 = Variables.varDTT(d3, comm, t3);
		applVars.add(dttVar3);
		CommunicationRoutingEncoderManager routingEncoderManager = mock(CommunicationRoutingEncoderManager.class);
		FlexibleRoutingEncoding routingEncoding = new FlexibleRoutingEncoding(routingEncoderManager);
		Map<T, Set<DTT>> dependencyMap = routingEncoding.makeDependencyMap(applVars);
		assertTrue(dependencyMap.containsKey(commVar));
		assertEquals(1, dependencyMap.keySet().size());
		assertEquals(3, dependencyMap.get(commVar).size());
		assertTrue(dependencyMap.get(commVar).contains(dttVar1));
		assertTrue(dependencyMap.get(commVar).contains(dttVar2));
		assertTrue(dependencyMap.get(commVar).contains(dttVar3));
	}

}
