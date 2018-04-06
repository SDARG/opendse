package net.sf.opendse.encoding.variables;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sf.opendse.encoding.routing.CommunicationFlow;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;

import static org.mockito.Mockito.mock;

public class DDLRRTest {

	@Test
	public void test() {
		CommunicationFlow mockFlow = mock(CommunicationFlow.class);
		Link link = new Link("link");
		Resource src = new Resource("src");
		Resource dest = new Resource("dest");
		DDLRR ddlrrVar = new DDLRR(mockFlow, link, src, dest);
		assertEquals(mockFlow, ddlrrVar.getCommunicationFlow());
		assertEquals(link, ddlrrVar.getLink());
		assertEquals(src, ddlrrVar.getSourceResource());
		assertEquals(dest, ddlrrVar.getDestResource());
		assertEquals(ddlrrVar, new DDLRR(mockFlow, link, src, dest));
	}

}
