package net.sf.opendse.encoding.routing;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.opt4j.satdecoding.Constraint;

import edu.uci.ics.jung.graph.util.EdgeType;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;
import verification.ConstraintVerifier;

public class ResourceOrderEncoderTest {

	@Test
	public void test() {
		Architecture<Resource, Link> routing = new Architecture<Resource, Link>();
		Resource r0 = new Resource("r0");
		Resource r1 = new Resource("r1");
		Resource r2 = new Resource("r2");
		Resource r3 = new Resource("r3");
		Link l0 = new Link("l0");
		Link l1 = new Link("l1");
		Link l2 = new Link("l2");
		routing.addEdge(l0, r0, r1, EdgeType.UNDIRECTED);
		routing.addEdge(l1, r1, r2, EdgeType.UNDIRECTED);
		routing.addEdge(l2, r1, r3, EdgeType.UNDIRECTED);
		
		Communication comm = new Communication("comm");
		ResourceOrderEncoder encoder = new ResourceOrderEncoder();
		Set<Constraint> cs = encoder.generateResourceOrderConstraints(Variables.varT(comm), routing);
		assertEquals(60, cs.size());
		Set<Object> active = new HashSet<Object>();
		active.add(Variables.varCLRR(comm, l0, r0, r1));
		active.add(Variables.varCLRR(comm, l1, r1, r2));
		active.add(Variables.varCR(comm, r0));
		active.add(Variables.varCR(comm, r1));
		active.add(Variables.varCR(comm, r2));
		Set<Object> unactive = new HashSet<Object>();
		unactive.add(Variables.varCLRR(comm, l2, r1, r3));
		unactive.add(Variables.varCLRR(comm, l2, r3, r1));
		unactive.add(Variables.varCLRR(comm, l0, r1, r0));
		unactive.add(Variables.varCLRR(comm, l1, r2, r1));
		unactive.add(Variables.varCR(comm, r3));
		ConstraintVerifier verifyOrder = new ConstraintVerifier(active, unactive, cs);
		
		verifyOrder.verifyVariableActivated(Variables.varCRR(comm, r0, r1));
		verifyOrder.verifyVariableActivated(Variables.varCRR(comm, r0, r2));
		verifyOrder.verifyVariableActivated(Variables.varCRR(comm, r1, r2));
		
		verifyOrder.verifyVariableDeactivated(Variables.varCRR(comm, r1, r0));
		verifyOrder.verifyVariableDeactivated(Variables.varCRR(comm, r2, r1));
		verifyOrder.verifyVariableDeactivated(Variables.varCRR(comm, r2, r0));
		
		verifyOrder.verifyVariableDeactivated(Variables.varCRR(comm, r1, r3));
		verifyOrder.verifyVariableDeactivated(Variables.varCRR(comm, r3, r1));
		verifyOrder.verifyVariableDeactivated(Variables.varCRR(comm, r3, r0));
		verifyOrder.verifyVariableDeactivated(Variables.varCRR(comm, r0, r3));
		verifyOrder.verifyVariableDeactivated(Variables.varCRR(comm, r2, r3));
		verifyOrder.verifyVariableDeactivated(Variables.varCRR(comm, r3, r2));
	}
}
