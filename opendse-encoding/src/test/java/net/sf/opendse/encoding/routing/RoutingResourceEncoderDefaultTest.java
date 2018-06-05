package net.sf.opendse.encoding.routing;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.opt4j.satdecoding.Constraint;

import edu.uci.ics.jung.graph.util.EdgeType;
import net.sf.opendse.encoding.variables.DTT;
import net.sf.opendse.encoding.variables.M;
import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;
import verification.ConstraintVerifier;

public class RoutingResourceEncoderDefaultTest {

	@Test
	public void test() {
		Task t0 = new Task("t0");
		Task t1 = new Task("t1");
		Communication comm = new Communication("comm");
		Dependency d0 = new Dependency("d0");
		Dependency d1 = new Dependency("d1");
		DTT srcDvar = Variables.varDTT(d0, t0, comm);
		DTT destDvar = Variables.varDTT(d1, comm, t1);
		CommunicationFlow commFlow = new CommunicationFlow(srcDvar, destDvar);
		Resource res0 = new Resource("r0");
		Resource res1 = new Resource("r1");
		Resource res2 = new Resource("r2");
		Link l0 = new Link("l0");
		Link l1 = new Link("l1");
		Architecture<Resource, Link> routing = new Architecture<Resource, Link>();
		routing.addEdge(l0, res0, res1, EdgeType.UNDIRECTED);
		routing.addEdge(l1, res1, res2, EdgeType.UNDIRECTED);
		Mapping<Task, Resource> mapping1 = new Mapping<Task, Resource>("m1", t0, res0);
		Mapping<Task, Resource> mapping2 = new Mapping<Task, Resource>("m2", t1, res2);
		M mVar1 = Variables.varM(mapping1);
		M mVar2 = Variables.varM(mapping2);
		Set<MappingVariable> mappingVars = new HashSet<MappingVariable>();
		mappingVars.add(mVar1);
		mappingVars.add(mVar2);
		RoutingResourceEncoderDefault encoder = new RoutingResourceEncoderDefault();
		Set<Constraint> cs = encoder.toConstraints(commFlow, routing, mappingVars);
		Set<Object> activated = new HashSet<Object>();
		activated.add(mVar1);
		activated.add(Variables.varDDLRR(commFlow, l0, res0, res1));
		Set<Object> deactivated = new HashSet<Object>();
		deactivated.add(mVar2);
		deactivated.add(Variables.varDDsR(commFlow, res1));
		deactivated.add(Variables.varDDsR(commFlow, res2));
		deactivated.add(Variables.varDDdR(commFlow, res0));
		deactivated.add(Variables.varDDdR(commFlow, res1));
		deactivated.add(Variables.varDDdR(commFlow, res2));
		deactivated.add(Variables.varDDLRR(commFlow, l0, res1, res0));
		deactivated.add(Variables.varDDLRR(commFlow, l1, res1, res2));
		deactivated.add(Variables.varDDLRR(commFlow, l1, res2, res1));
		ConstraintVerifier constraintVerifier = new ConstraintVerifier(activated, deactivated, cs);
		constraintVerifier.verifyVariableActivated(Variables.varDDR(commFlow, res0));
		constraintVerifier.verifyVariableActivated(Variables.varDDR(commFlow, res1));
		constraintVerifier.verifyVariableDeactivated(Variables.varDDR(commFlow, res2));
	}
}