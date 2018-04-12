package net.sf.opendse.encoding.routing;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.opt4j.satdecoding.Constraint;

import edu.uci.ics.jung.graph.util.EdgeType;
import net.sf.opendse.encoding.variables.DDLRR;
import net.sf.opendse.encoding.variables.DTT;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;
import verification.ConstraintVerifier;

public class ActivationEncoderDefaultTest {

	@Test
	public void test() {
		Task t0 = new Task("t0");
		Task t1 = new Task("t1");
		Communication comm = new Communication("comm");
		Dependency d0 = new Dependency("d0");
		Dependency d1 = new Dependency("d1");
		DTT srcDep = Variables.varDTT(d0, t0, comm);
		DTT destDep = Variables.varDTT(d1, comm, t1);
		CommunicationFlow flow = new CommunicationFlow(srcDep, destDep);
		Resource r0 = new Resource("r0");
		Resource r1 = new Resource("r1");
		Link l = new Link("l");
		Architecture<Resource, Link> routing = new Architecture<Resource, Link>();
		routing.addEdge(l, r0, r1, EdgeType.UNDIRECTED);
		DDLRR commFlowVar = Variables.varDDLRR(flow, l, r0, r1);
		Set<Object> deactivatedSrc = new HashSet<Object>();
		deactivatedSrc.add(srcDep);
		Set<Object> deactivatedDest = new HashSet<Object>();
		deactivatedDest.add(destDep);
		Set<Object> deactivatedBoth = new HashSet<Object>();
		deactivatedBoth.add(srcDep);
		deactivatedBoth.add(destDep);
		ActivationEncoderDefault encoder = new ActivationEncoderDefault();
		Set<Constraint> cs = encoder.toConstraints(flow, routing);
		assertEquals(2, cs.size());
		ConstraintVerifier verifyDeactivation1 = new ConstraintVerifier(new HashSet<Object>(), deactivatedSrc, cs);
		ConstraintVerifier verifyDeactivation2 = new ConstraintVerifier(new HashSet<Object>(), deactivatedDest, cs);
		ConstraintVerifier verifyDeactivation3 = new ConstraintVerifier(new HashSet<Object>(), deactivatedBoth, cs);
		verifyDeactivation1.verifyVariableDeactivated(commFlowVar);
		verifyDeactivation2.verifyVariableDeactivated(commFlowVar);
		verifyDeactivation3.verifyVariableDeactivated(commFlowVar);
	}
}