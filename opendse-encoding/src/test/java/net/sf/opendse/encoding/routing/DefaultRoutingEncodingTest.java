package net.sf.opendse.encoding.routing;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import net.sf.opendse.encoding.variables.ApplicationVariable;
import net.sf.opendse.encoding.variables.DTT;
import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Task;

public class DefaultRoutingEncodingTest {

	@Test
	public void testFindCommunicationFlows() {
		Task t1 = new Task("t1");
		Task t2 = new Task("t2");
		Task t3 = new Task("t3");
		Communication comm = new Communication("comm");
		Dependency d1 = new Dependency("d1");
		Dependency d2 = new Dependency("d2");
		Dependency d3 = new Dependency("d3");
		T tVar1 = Variables.var(t1);
		T tVar2 = Variables.var(t2);
		T tVar3 = Variables.var(t3);
		T tVarComm = Variables.var(comm);
		DTT dtt1 = Variables.var(d1, t1, comm);
		DTT dtt2 = Variables.var(d2, comm, t1);
		DTT dtt3 = Variables.var(d3, comm, t2);
		Set<ApplicationVariable> applVars = new HashSet<ApplicationVariable>();
		applVars.add(tVar1);
		applVars.add(tVar2);
		applVars.add(tVar3);
		applVars.add(tVarComm);
		applVars.add(dtt1);
		applVars.add(dtt2);
		applVars.add(dtt3);
		DefaultRoutingEncoding encoding = new DefaultRoutingEncoding();
		Map<T, Set<CommunicationFlow>> map = encoding.findCommunicationFlows(applVars);
		assertEquals(1, map.keySet().size());
		assertTrue(map.keySet().contains(tVarComm));
		Set<CommunicationFlow> flows = map.get(tVarComm);
		assertEquals(2, flows.size());
		assertTrue(flows.contains(new CommunicationFlow(dtt1, dtt2)));
		assertTrue(flows.contains(new CommunicationFlow(dtt1, dtt3)));
	}
}
