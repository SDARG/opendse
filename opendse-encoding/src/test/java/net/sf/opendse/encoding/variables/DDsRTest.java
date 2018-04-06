package net.sf.opendse.encoding.variables;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import net.sf.opendse.encoding.routing.CommunicationFlow;
import net.sf.opendse.model.Resource;

public class DDsRTest {

	@Test
	public void test() {
		CommunicationFlow mockFlow = mock(CommunicationFlow.class);
		Resource resource = new Resource("resource");
		DDsR ddsrVar = new DDsR(mockFlow, resource);
		assertEquals(mockFlow, ddsrVar.getCommunicationFlow());
		assertEquals(resource, ddsrVar.getResource());
		assertEquals(ddsrVar, new DDsR(mockFlow, resource));
	}
	
}
