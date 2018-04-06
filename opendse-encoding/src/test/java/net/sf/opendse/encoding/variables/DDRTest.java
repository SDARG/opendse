package net.sf.opendse.encoding.variables;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sf.opendse.encoding.routing.CommunicationFlow;
import net.sf.opendse.model.Resource;

import static org.mockito.Mockito.mock;

public class DDRTest {

	@Test
	public void test() {
		CommunicationFlow mockFlow = mock(CommunicationFlow.class);
		Resource resource = new Resource("resource");
		DDR ddrVar = new DDR(mockFlow, resource);
		assertEquals(mockFlow, ddrVar.getCommunicationFlow());
		assertEquals(resource, ddrVar.getResource());
		assertEquals(ddrVar, new DDR(mockFlow, resource));
	}
}
