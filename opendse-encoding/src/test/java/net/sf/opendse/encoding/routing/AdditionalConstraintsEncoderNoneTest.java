package net.sf.opendse.encoding.routing;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;

import static org.mockito.Mockito.mock;

import java.util.Set;

public class AdditionalConstraintsEncoderNoneTest {

	@Test
	public void test() {
		T mockT = mock(T.class);
		@SuppressWarnings("unchecked")
		Set<CommunicationFlow> commFlows = mock(Set.class);
		@SuppressWarnings("unchecked")
		Architecture<Resource, Link> routing = mock(Architecture.class);
		AdditionalRoutingConstraintsEncoderNone encoder = new AdditionalRoutingConstraintsEncoderNone();
		assertTrue(encoder.toConstraints(mockT, commFlows, routing).isEmpty());
	}

}
