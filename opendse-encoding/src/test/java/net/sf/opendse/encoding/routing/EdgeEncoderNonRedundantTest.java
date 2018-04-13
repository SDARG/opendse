package net.sf.opendse.encoding.routing;

import static org.junit.Assert.*;

import org.junit.Test;
import org.opt4j.satdecoding.Constraint;

import edu.uci.ics.jung.graph.util.EdgeType;
import net.sf.opendse.encoding.variables.DTT;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;
import verification.ConstraintVerifier;
import java.util.HashSet;
import java.util.Set;

public class EdgeEncoderNonRedundantTest {

	@Test
	public void test() {
		Resource r0 = new Resource("r0");
		Resource r1 = new Resource("r1");
		Resource r2 = new Resource("r2");
		Resource r3 = new Resource("r3");
		Resource r4 = new Resource("r4");
		Resource r5 = new Resource("r5");
		Link l0 = new Link("l0");
		Link l1 = new Link("l1");
		Link l2 = new Link("l2");
		Link l3 = new Link("l3");
		Link l4 = new Link("l4");
		Architecture<Resource, Link> routing = new Architecture<Resource, Link>();
		routing.addEdge(l0, r0, r1, EdgeType.UNDIRECTED);
		routing.addEdge(l1, r1, r5, EdgeType.UNDIRECTED);
		routing.addEdge(l2, r1, r4, EdgeType.UNDIRECTED);
		routing.addEdge(l3, r5, r2, EdgeType.UNDIRECTED);
		routing.addEdge(l4, r2, r3, EdgeType.UNDIRECTED);
		Task t0 = new Task("t0");
		Task t1 = new Task("t1");
		Communication comm = new Communication("comm");
		Dependency d0 = new Dependency("d0");
		Dependency d1 = new Dependency("d1");
		DTT srcDep = Variables.varDTT(d0, t0, comm);
		DTT destDep = Variables.varDTT(d1, comm, t1);
		CommunicationFlow commFlow = new CommunicationFlow(srcDep, destDep);
		RoutingEdgeEncoderNonRedundant encoder = new RoutingEdgeEncoderNonRedundant();
		Set<Constraint> cs = encoder.toConstraints(commFlow, routing);
		assertEquals(18, cs.size());
		Set<Object> activated = new HashSet<Object>();
		Set<Object> deactivated = new HashSet<Object>();
		activated.add(Variables.varDDsR(commFlow, r1));
		activated.add(Variables.varDDdR(commFlow, r2));
		deactivated.add(Variables.varDDdR(commFlow, r1));
		deactivated.add(Variables.varDDdR(commFlow, r0));
		deactivated.add(Variables.varDDdR(commFlow, r3));
		deactivated.add(Variables.varDDdR(commFlow, r4));
		deactivated.add(Variables.varDDdR(commFlow, r5));
		deactivated.add(Variables.varDDsR(commFlow, r0));
		deactivated.add(Variables.varDDsR(commFlow, r2));
		deactivated.add(Variables.varDDsR(commFlow, r3));
		deactivated.add(Variables.varDDsR(commFlow, r4));
		deactivated.add(Variables.varDDsR(commFlow, r5));
		ConstraintVerifier verifyEdgeActivation = new ConstraintVerifier(activated, deactivated, cs);
		verifyEdgeActivation.verifyVariableDeactivated(Variables.varDDLRR(commFlow, l0, r1, r0));
		verifyEdgeActivation.verifyVariableDeactivated(Variables.varDDLRR(commFlow, l2, r1, r4));
		verifyEdgeActivation.verifyVariableDeactivated(Variables.varDDLRR(commFlow, l4, r2, r3));
		verifyEdgeActivation.verifyVariableActivated(Variables.varDDLRR(commFlow, l1, r1, r5));
		verifyEdgeActivation.verifyVariableActivated(Variables.varDDLRR(commFlow, l3, r5, r2));
	}
}