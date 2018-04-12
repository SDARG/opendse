package net.sf.opendse.encoding.routing;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.opt4j.satdecoding.Constraint;

import edu.uci.ics.jung.graph.util.EdgeType;
import net.sf.opendse.encoding.variables.CLRR;
import net.sf.opendse.encoding.variables.CR;
import net.sf.opendse.encoding.variables.DDLRR;
import net.sf.opendse.encoding.variables.DDR;
import net.sf.opendse.encoding.variables.DTT;
import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;
import verification.ConstraintVerifier;

public class DefaultCommunicationHierarchyEncoderTest {

	@Test
	public void test() {
		Task t0 = new Task("t0");
		Task t1 = new Task("t1");
		Task t2 = new Task("t2");
		Communication comm = new Communication("comm");
		Dependency d0 = new Dependency("d0");
		Dependency d1 = new Dependency("d1");
		Dependency d2 = new Dependency("d2");
		DTT dtt0 = Variables.varDTT(d0, t0, comm);
		DTT dtt1 = Variables.varDTT(d1, comm, t1);
		DTT dtt2 = Variables.varDTT(d2, comm, t2);
		Resource r0 = new Resource("r0");
		Resource r1 = new Resource("r1");
		Resource r2 = new Resource("r2");
		Link l0 = new Link("l0");
		Link l1 = new Link("l1");
		Architecture<Resource, Link> routing = new Architecture<Resource, Link>();
		routing.addEdge(l0, r0, r1, EdgeType.UNDIRECTED);
		routing.addEdge(l1, r1, r2, EdgeType.UNDIRECTED);
		CommunicationFlow cf1 = new CommunicationFlow(dtt0, dtt1);
		CommunicationFlow cf2 = new CommunicationFlow(dtt0, dtt2);
		Set<CommunicationFlow> flows = new HashSet<CommunicationFlow>();
		flows.add(cf1);
		flows.add(cf2);
		T commVar = Variables.varT(comm);
		CommunicationHierarchyEncoderDefault encoder = new CommunicationHierarchyEncoderDefault();
		Set<Constraint> cs = encoder.toConstraints(commVar, flows, routing);
		assertEquals(21, cs.size());
		CLRR commRoutVar1 = Variables.varCLRR(comm, l0, r0, r1);
		DDLRR flow1Rout1 = Variables.varDDLRR(cf1, l0, r0, r1);
		Set<Object> unactive = new HashSet<Object>();
		DDR flow1R0 = Variables.varDDR(cf1, r0);
		unactive.add(flow1R0);
		unactive.add(Variables.varDDR(cf2, r0));
		CR commR0 = Variables.varCR(comm, r0);
		Set<Object> active1 = new HashSet<Object>();
		active1.add(flow1Rout1);
		ConstraintVerifier verifyActiveIfFlowActive = new ConstraintVerifier(active1, unactive, cs);
		verifyActiveIfFlowActive.verifyVariableActivated(commRoutVar1);
		verifyActiveIfFlowActive.verifyVariableDeactivated(commR0);
		CLRR commRoutVar2 = Variables.varCLRR(comm, l1, r1, r2);
		DDLRR flow1Rout2 = Variables.varDDLRR(cf1, l1, r1, r2);
		DDLRR flow2Rout2 = Variables.varDDLRR(cf2, l1, r1, r2);
		DDR flo2R2 = Variables.varDDR(cf2, r2);
		DDR flo1R2 = Variables.varDDR(cf1, r2);
		CR commR2 = Variables.varCR(comm, r2);
		Set<Object> deactivated = new HashSet<Object>();
		deactivated.add(flow1Rout2);
		deactivated.add(flow2Rout2);
		deactivated.add(flo1R2);
		deactivated.add(flo2R2);
		ConstraintVerifier verifyUnactiveIfAllInactive = new ConstraintVerifier(new HashSet<Object>(), deactivated, cs);
		verifyUnactiveIfAllInactive.verifyVariableDeactivated(commRoutVar2);
		verifyUnactiveIfAllInactive.verifyVariableDeactivated(commR2);
		deactivated.remove(flo2R2);
		Set<Object> act = new HashSet<Object>();
		act.add(flo2R2);
		ConstraintVerifier verifyResourceActivation = new ConstraintVerifier(act, deactivated, cs);
		verifyResourceActivation.verifyVariableActivated(commR2);
	}
}
