package net.sf.opendse.encoding.variables;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import net.sf.opendse.encoding.routing.CommunicationFlow;
import net.sf.opendse.model.Resource;

public class DDdRTest {

	@Test
	public void test() {
		CommunicationFlow mockFlow = mock(CommunicationFlow.class);
		Resource resource = new Resource("resource");
		DDdR dddrVar = new DDdR(mockFlow, resource);
		assertEquals(mockFlow, dddrVar.getCommunicationFlow());
		assertEquals(resource, dddrVar.getResource());
		assertEquals(dddrVar, new DDdR(mockFlow, resource));
	}

}
