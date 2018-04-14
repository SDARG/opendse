package net.sf.opendse.encoding.routing;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.opt4j.satdecoding.Constraint;
import edu.uci.ics.jung.graph.util.EdgeType;
import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;
import verification.ConstraintVerifier;

public class CycleBreakEncoderOrderTest {

	@Test
	public void test() {
		Architecture<Resource, Link> routing = new Architecture<Resource, Link>();
		Resource r0 = new Resource("r0");
		Resource r1 = new Resource("r1");
		Resource r2 = new Resource("r2");
		Resource r3 = new Resource("r3");
		Resource r4 = new Resource("r4");
		Resource r5 = new Resource("r5");
		Resource r6 = new Resource("r6");
		Link l0 = new Link("l0");
		Link l1 = new Link("l1");
		Link l2 = new Link("l2");
		Link l3 = new Link("l3");
		Link l4 = new Link("l4");
		Link l5 = new Link("l5");
		Link l6 = new Link("l6");
		Link l7 = new Link("l7");
		routing.addEdge(l0, r0, r1, EdgeType.UNDIRECTED);
		routing.addEdge(l1, r0, r2, EdgeType.UNDIRECTED);
		routing.addEdge(l2, r1, r2, EdgeType.UNDIRECTED);
		routing.addEdge(l3, r2, r3, EdgeType.UNDIRECTED);
		routing.addEdge(l4, r2, r4, EdgeType.UNDIRECTED);
		routing.addEdge(l5, r4, r5, EdgeType.UNDIRECTED);
		routing.addEdge(l6, r4, r6, EdgeType.UNDIRECTED);
		routing.addEdge(l7, r5, r6, EdgeType.UNDIRECTED);
		Communication comm = new Communication("comm");
		T commVar = Variables.varT(comm);
		CycleBreakEncoderOrder cycleBreaker = new CycleBreakEncoderOrder();
		Set<Constraint> cs = cycleBreaker.toConstraints(commVar, routing);
		assertEquals(331, cs.size());
		Set<Object> activated = new HashSet<Object>();
		activated.add(Variables.varCLRR(comm, l0, r0, r1));
		activated.add(Variables.varCLRR(comm, l2, r1, r2));
		activated.add(Variables.varCLRR(comm, l3, r2, r3));
		ConstraintVerifier verifyNoCyclesInRouting = new ConstraintVerifier(activated, new HashSet<Object>(), cs);
		verifyNoCyclesInRouting.verifyVariableDeactivated(Variables.varCLRR(comm, l1, r2, r0));
		activated.add(Variables.varCLRR(comm, l5, r5, r4));
		activated.add(Variables.varCLRR(comm, l6, r4, r6));
		activated.add(Variables.varCLRR(comm, l7, r6, r5));
		// the constraints should not be solvable
		boolean assertionError = false;
		try {
			new ConstraintVerifier(activated, new HashSet<Object>(), cs);
		} catch (AssertionError error) {
			assertionError = true;
		}
		assertTrue(assertionError);
	}
}
