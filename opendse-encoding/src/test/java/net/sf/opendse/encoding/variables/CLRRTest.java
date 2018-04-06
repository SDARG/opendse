package net.sf.opendse.encoding.variables;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

public class CLRRTest {

	@Test(expected = IllegalArgumentException.class)
	public void testProcessInput() {
		new CLRR(new Task("task"), new Link("link"), new Resource("source"), new Resource("destination"));
	}

	@Test
	public void test() {
		Communication comm = new Communication("comm");
		Link link = new Link("link");
		Resource source = new Resource("source");
		Resource destination = new Resource("destination");
		CLRR clrrVar = new CLRR(comm, link, source, destination);
		assertEquals(comm, clrrVar.getCommunication());
		assertEquals(link, clrrVar.getLink());
		assertEquals(source, clrrVar.getSource());
		assertEquals(destination, clrrVar.getDestination());
		assertEquals(clrrVar, new CLRR(comm, link, source, destination));
	}

}
