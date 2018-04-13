package net.sf.opendse.encoding.routing;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sf.opendse.encoding.variables.DTT;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Task;

public class CommunicationFlowTest {

	@Test(expected=IllegalArgumentException.class)
	public void testWrongInput() {
		Task t1 = new Task("t1");
		Communication comm = new Communication("comm");
		Task t2 = new Task("t2");
		Dependency d1 = new Dependency("d1");
		Dependency d2 = new Dependency("d2");
		DTT dtt1 = Variables.varDTT(d1, t1, comm);
		DTT dtt2 = Variables.varDTT(d2, t2, comm);
		new CommunicationFlow(dtt1, dtt2);
	}
	
	@Test
	public void test() {
		Task t1 = new Task("t1");
		Communication comm = new Communication("comm");
		Task t2 = new Task("t2");
		Dependency d1 = new Dependency("d1");
		Dependency d2 = new Dependency("d2");
		DTT dtt1 = Variables.varDTT(d1, t1, comm);
		DTT dtt2 = Variables.varDTT(d2, comm, t2);
		CommunicationFlow flow = new CommunicationFlow(dtt1, dtt2);
		assertEquals(dtt1, flow.getSourceDTT());
		assertEquals(dtt2, flow.getDestinationDTT());
		CommunicationFlow flow2 = new CommunicationFlow(dtt1, dtt2);
		assertEquals(flow, flow2);
		assertEquals(flow.hashCode(), flow2.hashCode());
		assertNotEquals(flow, t1);
		Task t3 = new Task("t3");
		Dependency d3 = new Dependency("d3");
		DTT dtt3 = Variables.varDTT(d3, comm, t3);
		assertNotEquals(flow, new CommunicationFlow(dtt1, dtt3));
	}
	
	@Test
	public void testToString() {
		Task t1 = new Task("t1");
		Communication comm = new Communication("comm");
		Task t2 = new Task("t2");
		Dependency d1 = new Dependency("d1");
		Dependency d2 = new Dependency("d2");
		DTT dtt1 = Variables.varDTT(d1, t1, comm);
		DTT dtt2 = Variables.varDTT(d2, comm, t2);
		CommunicationFlow flow = new CommunicationFlow(dtt1, dtt2);
		assertEquals("d1 => d2", flow.toString());
	}
}
