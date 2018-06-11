package net.sf.opendse.encoding.routing;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.opt4j.satdecoding.Constraint;

import edu.uci.ics.jung.graph.util.EdgeType;
import net.sf.opendse.encoding.variables.DTT;
import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Models.DirectedLink;
import verification.ConstraintVerifier;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

public class CycleBreakEncoderGeneralTest {

	protected static final int minNodeNumber = 3;
	protected static final int maxNodeNumber = 11;

	protected static final String resourceName = "res";
	protected static final String linkName = "link";

	protected static Set<DirectedLink> getAllowedLinks(Architecture<Resource, Link> routingCycle, int nodeNum) {
		Set<DirectedLink> result = new HashSet<DirectedLink>();
		for (int num = 1; num < nodeNum; num++) {
			Resource src = routingCycle.getVertex(resourceName + num);
			Resource dest = routingCycle.getVertex(resourceName + (num + 1));
			Link link = routingCycle.getEdge(linkName + num);
			result.add(new DirectedLink(link, src, dest));
		}
		return result;
	}

	protected static Architecture<Resource, Link> makeCycleRouting(int nodeNumber) {
		Architecture<Resource, Link> arch = new Architecture<Resource, Link>();
		int resNum = 0;
		int linkNum = 0;
		Resource first = new Resource(resourceName + ++resNum);
		Resource current = first;
		for (int i = 1; i < nodeNumber; i++) {
			Resource next = new Resource(resourceName + ++resNum);
			Link connection = new Link(linkName + ++linkNum);
			arch.addEdge(connection, current, next, EdgeType.UNDIRECTED);
			current = next;
		}
		Link lastConnection = new Link(linkName + ++linkNum);
		arch.addEdge(lastConnection, current, first, EdgeType.UNDIRECTED);
		return arch;
	}

	@Test
	public void testCycleBreak() {
		// create cycle situations with different numbers of cycles and test the cycle
		// breakers
		CycleBreakEncoder cycleBreaker = new CycleBreakEncoderColor();
		CommunicationHierarchyEncoderDefault hierarchyEncoder = new CommunicationHierarchyEncoderDefault();
		RoutingResourceEncoderDefault routingResourceEncoder = new RoutingResourceEncoderDefault();

		Communication comm = new Communication("comm");
		Task src = new Task("src");
		Dependency srcDependency = new Dependency("srcDep");
		Dependency destDependency = new Dependency("destDep");
		Task dest = new Task("dest");
		DTT srcDtt = Variables.varDTT(srcDependency, src, comm);
		DTT destDtt = Variables.varDTT(destDependency, comm, dest);
		CommunicationFlow flow = new CommunicationFlow(srcDtt, destDtt);
		Set<CommunicationFlow> flows = new HashSet<CommunicationFlow>();
		flows.add(flow);

		T commVar = Variables.varT(comm);
		for (int nodeNum = minNodeNumber; nodeNum <= maxNodeNumber; nodeNum++) {
			Architecture<Resource, Link> routing = makeCycleRouting(nodeNum);
			Set<Constraint> cs = cycleBreaker.toConstraints(commVar, routing);
			cs.addAll(hierarchyEncoder.toConstraints(commVar, flows, routing));
			cs.addAll(routingResourceEncoder.toConstraints(flow, routing, new HashSet<MappingVariable>()));
			Set<DirectedLink> allowedLinks = getAllowedLinks(routing, nodeNum);
			Resource first = routing.getVertex(resourceName + "1");
			Resource last = routing.getVertex(resourceName + nodeNum);
			Link lastLink = routing.getEdge(linkName + nodeNum);
			DirectedLink lastDirLink = new DirectedLink(lastLink, last, first);
			ConstraintVerifier verifyNoCycles = new ConstraintVerifier(cs);
			for (DirectedLink allowed : allowedLinks) {
				verifyNoCycles.activateVariable(Variables.varCLRR(comm, allowed));
			}
			try {
				verifyNoCycles.verifyVariableDeactivated(Variables.varCLRR(comm, lastDirLink));
			} catch (AssertionError error) {
				throw new AssertionError(error.getMessage() + " nodeNum " + nodeNum);
			}
		}
	}
}
