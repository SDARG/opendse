package net.sf.opendse.encoding.routing;

import static org.junit.Assert.*;

import org.junit.Test;
import org.opt4j.satdecoding.Constraint;

import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.util.HashSet;
import java.util.Set;

public class CustomCommunicationRoutingEncoderTest {

	@Test
	public void test() {
		CycleBreakEncoder cycleBreak = mock(CycleBreakEncoder.class);
		CommunicationHierarchyEncoder hierarchyEncoder = mock(CommunicationHierarchyEncoder.class);
		CommunicationFlowRoutingManager manager = mock(CommunicationFlowRoutingManager.class);
		AdditionalRoutingConstraintsEncoder additional = mock(AdditionalRoutingConstraintsEncoder.class);
		OneDirectionEncoder oneDirectionEncoder = mock(OneDirectionEncoder.class);
		CustomCommunicationRoutingEncoder encoder = new CustomCommunicationRoutingEncoder(oneDirectionEncoder,
				cycleBreak, hierarchyEncoder, manager, additional);
		T mockT = mock(T.class);
		CommunicationFlow commFlow = mock(CommunicationFlow.class);
		Set<CommunicationFlow> commFlows = new HashSet<CommunicationFlow>();
		commFlows.add(commFlow);
		@SuppressWarnings("unchecked")
		Architecture<Resource, Link> routing = mock(Architecture.class);
		@SuppressWarnings("unchecked")
		Set<MappingVariable> mappings = mock(Set.class);
		when(oneDirectionEncoder.toConstraints(mockT, routing)).thenReturn(new HashSet<Constraint>());
		when(cycleBreak.toConstraints(mockT, routing)).thenReturn(new HashSet<Constraint>());
		when(hierarchyEncoder.toConstraints(mockT, commFlows, routing)).thenReturn(new HashSet<Constraint>());
		when(additional.toConstraints(mockT, commFlows, routing)).thenReturn(new HashSet<Constraint>());
		CommunicationFlowRoutingEncoder mockEncoder = mock(CommunicationFlowRoutingEncoder.class);
		when(mockEncoder.toConstraints(commFlow, routing, mappings)).thenReturn(new HashSet<Constraint>());
		when(manager.getEncoder(commFlow)).thenReturn(mockEncoder);
		Set<Constraint> cs = encoder.toConstraints(mockT, commFlows, routing, mappings);
		assertTrue(cs.isEmpty());
		verify(oneDirectionEncoder).toConstraints(mockT, routing);
		verify(cycleBreak).toConstraints(mockT, routing);
		verify(hierarchyEncoder).toConstraints(mockT, commFlows, routing);
		verify(additional).toConstraints(mockT, commFlows, routing);
		verify(manager).getEncoder(commFlow);
		verify(mockEncoder).toConstraints(commFlow, routing, mappings);
	}
}