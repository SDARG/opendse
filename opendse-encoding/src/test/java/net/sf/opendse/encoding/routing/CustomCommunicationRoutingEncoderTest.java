package net.sf.opendse.encoding.routing;

import static org.junit.Assert.*;

import org.junit.Test;
import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.Constraint.Operator;

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
		CommunicationRoutingEncoderCustom encoder = new CommunicationRoutingEncoderCustom(oneDirectionEncoder,
				cycleBreak, hierarchyEncoder, manager, additional);
		T mockT = mock(T.class);
		CommunicationFlow commFlow = mock(CommunicationFlow.class);
		Set<CommunicationFlow> commFlows = new HashSet<CommunicationFlow>();
		commFlows.add(commFlow);
		@SuppressWarnings("unchecked")
		Architecture<Resource, Link> routing = mock(Architecture.class);
		@SuppressWarnings("unchecked")
		Set<MappingVariable> mappings = mock(Set.class);
		Constraint oneDirection = new Constraint(Operator.EQ, 1);
		Set<Constraint> oneDirectionCs = new HashSet<Constraint>();
		oneDirectionCs.add(oneDirection);
		Constraint cycleBreakC = new Constraint(Operator.EQ, 2);
		Set<Constraint> cycleBreakCs = new HashSet<Constraint>();
		cycleBreakCs.add(cycleBreakC);
		Constraint hierarchyC = new Constraint(Operator.EQ, 3);
		Set<Constraint> hierarchyCs = new HashSet<Constraint>();
		hierarchyCs.add(hierarchyC);
		Constraint additionalC = new Constraint(Operator.EQ, 4);
		Set<Constraint> additionalCs = new HashSet<Constraint>();
		additionalCs.add(additionalC);
		Constraint flowC = new Constraint(Operator.EQ, 5);
		Set<Constraint> flowCs = new HashSet<Constraint>();
		flowCs.add(flowC);
		when(oneDirectionEncoder.toConstraints(mockT, routing)).thenReturn(oneDirectionCs);
		when(cycleBreak.toConstraints(mockT, routing)).thenReturn(cycleBreakCs);
		when(hierarchyEncoder.toConstraints(mockT, commFlows, routing)).thenReturn(hierarchyCs);
		when(additional.toConstraints(mockT, commFlows, routing)).thenReturn(additionalCs);
		CommunicationFlowRoutingEncoder mockEncoder = mock(CommunicationFlowRoutingEncoder.class);
		when(mockEncoder.toConstraints(commFlow, routing, mappings)).thenReturn(flowCs);
		when(manager.getEncoder(commFlow)).thenReturn(mockEncoder);
		Set<Constraint> cs = encoder.toConstraints(mockT, commFlows, routing, mappings);
		assertEquals(5, cs.size());
		verify(oneDirectionEncoder).toConstraints(mockT, routing);
		verify(cycleBreak).toConstraints(mockT, routing);
		verify(hierarchyEncoder).toConstraints(mockT, commFlows, routing);
		verify(additional).toConstraints(mockT, commFlows, routing);
		verify(manager).getEncoder(commFlow);
		verify(mockEncoder).toConstraints(commFlow, routing, mappings);
	}
}