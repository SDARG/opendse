package net.sf.opendse.encoding.routing;

import static org.junit.Assert.*;

import org.junit.Test;
import org.opt4j.satdecoding.Constraint;

import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

public class CustomCommunicationFlowRoutingEncoderTest {

	@Test
	public void test() {
		ActivationEncoder activationEncoder = mock(ActivationEncoder.class);
		EndNodeEncoder endNodeEncoder = mock(EndNodeEncoder.class);
		RoutingResourceEncoder interimNodeEncoder = mock(RoutingResourceEncoder.class);
		RoutingEdgeEncoder edgeEncoder = mock(RoutingEdgeEncoder.class);
		CustomCommunicationFlowRoutingEncoder encoder = new CustomCommunicationFlowRoutingEncoder(activationEncoder, endNodeEncoder, interimNodeEncoder, edgeEncoder);
		CommunicationFlow commFlow = mock(CommunicationFlow.class);
		@SuppressWarnings("unchecked")
		Architecture<Resource, Link> routing = mock(Architecture.class);
		@SuppressWarnings("unchecked")
		Set<MappingVariable> mappingVariables = mock(Set.class);
		when(activationEncoder.toConstraints(commFlow, routing)).thenReturn(new HashSet<Constraint>());
		when(endNodeEncoder.toConstraints(commFlow, routing, mappingVariables)).thenReturn(new HashSet<Constraint>());
		when(interimNodeEncoder.toConstraints(commFlow, routing)).thenReturn(new HashSet<Constraint>());
		when(edgeEncoder.toConstraints(commFlow, routing)).thenReturn(new HashSet<Constraint>());
		Set<Constraint> cs = encoder.toConstraints(commFlow, routing, mappingVariables);
		assertTrue(cs.isEmpty());
		verify(activationEncoder).toConstraints(commFlow, routing);
		verify(endNodeEncoder).toConstraints(commFlow, routing, mappingVariables);
		verify(interimNodeEncoder).toConstraints(commFlow, routing);
		verify(edgeEncoder).toConstraints(commFlow, routing);
	}
}