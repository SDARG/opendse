package net.sf.opendse.encoding.routing;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.opt4j.satdecoding.Constraint;

import edu.uci.ics.jung.graph.util.EdgeType;
import net.sf.opendse.encoding.variables.CLRR;
import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;
import verification.ConstraintVerifier;

public class DefaultOneDirectionEncoderTest {

	@Test
	public void test() {
		Resource r0 = new Resource("r0");
		Resource r1 = new Resource("r1");
		Resource r2 = new Resource("r2");
		Link l0 = new Link("l0");
		Link l1 = new Link("l1");
		Architecture<Resource, Link> routing = new Architecture<Resource, Link>();
		routing.addEdge(l0, r0, r1, EdgeType.UNDIRECTED);
		routing.addEdge(l1, r1, r2, EdgeType.UNDIRECTED);
		Communication comm = new Communication("comm");
		T commVar = Variables.varT(comm);
		DefaultOneDirectionEncoder encoder = new DefaultOneDirectionEncoder();
		Set<Constraint> cs = encoder.toConstraints(commVar, routing);
		assertEquals(2, cs.size());
		CLRR l0_front = Variables.varCLRR(comm, l0, r0, r1);
		CLRR l0_back = Variables.varCLRR(comm, l0, r1, r0);
		CLRR l1_front = Variables.varCLRR(comm, l1, r1, r2);
		CLRR l1_back = Variables.varCLRR(comm, l1, r2, r1);
		Set<Object> activated = new HashSet<Object>();
		activated.add(l0_front);
		activated.add(l1_front);
		ConstraintVerifier verifyOneDirection = new ConstraintVerifier(activated, new HashSet<Object>(), cs);
		verifyOneDirection.verifyVariableDeactivated(l0_back);
		verifyOneDirection.verifyVariableDeactivated(l1_back);
	}
}