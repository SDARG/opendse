package net.sf.opendse.encoding.routing;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.opt4j.satdecoding.Constraint;

import edu.uci.ics.jung.graph.util.EdgeType;
import net.sf.opendse.encoding.variables.DTT;
import net.sf.opendse.encoding.variables.M;
import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Models.DirectedLink;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.properties.ArchitectureElementPropertyService;
import net.sf.opendse.model.properties.ResourcePropertyService;
import verification.ConstraintVerifier;

public class ProxyEncoderTest {

	protected Set<Object> activatedVars = new HashSet<Object>();
	protected Set<Object> deactivatedVars = new HashSet<Object>();

	protected Resource r0 = new Resource("r0");
	protected Resource r1 = new Resource("r1");
	protected Resource r2 = new Resource("r2");
	protected Resource r3 = new Resource("r3");
	protected Link l0 = new Link("l0");
	protected Link l1 = new Link("l1");
	protected Link l2 = new Link("l2");

	protected Task t0 = new Task("t0");
	protected Task t1 = new Task("t1");
	protected Task t2 = new Task("t2");
	protected Communication comm = new Communication("comm");
	protected Dependency d0 = new Dependency("d0");
	protected Dependency d1 = new Dependency("d1");
	protected Dependency d2 = new Dependency("d2");
	protected T tVar0 = Variables.varT(t0);
	protected T tVar1 = Variables.varT(t1);
	protected T tVar2 = Variables.varT(t2);
	protected T tVarComm = Variables.varT(comm);
	protected DTT dttVar0 = Variables.varDTT(d0, t0, comm);
	protected DTT dttVar1 = Variables.varDTT(d1, comm, t1);
	protected DTT dttVar2 = Variables.varDTT(d2, comm, t2);
	protected CommunicationFlow flow_t0_t1 = new CommunicationFlow(dttVar0, dttVar1);
	protected CommunicationFlow flow_t0_t2 = new CommunicationFlow(dttVar0, dttVar2);

	protected Mappings<Task, Resource> mappings = new Mappings<Task, Resource>();
	protected Mapping<Task, Resource> m0 = new Mapping<Task, Resource>("m0", t0, r0);
	protected Mapping<Task, Resource> m1 = new Mapping<Task, Resource>("m1", t1, r1);
	protected Mapping<Task, Resource> m2 = new Mapping<Task, Resource>("m2", t1, r2);
	protected Mapping<Task, Resource> m3 = new Mapping<Task, Resource>("m3", t2, r1);
	protected Mapping<Task, Resource> m4 = new Mapping<Task, Resource>("m4", t2, r2);
	protected M mVar0 = Variables.varM(m0);
	protected M mVar1 = Variables.varM(m1);
	protected M mVar2 = Variables.varM(m2);
	protected M mVar3 = Variables.varM(m3);
	protected M mVar4 = Variables.varM(m4);
	protected Set<MappingVariable> mappingVariables = new HashSet<MappingVariable>();

	protected Architecture<Resource, Link> routing = new Architecture<Resource, Link>();

	public void init() {
		routing.addEdge(l1, r1, r3, EdgeType.UNDIRECTED);
		routing.addEdge(l0, r0, r3, EdgeType.UNDIRECTED);
		routing.addEdge(l2, r2, r3, EdgeType.UNDIRECTED);
		ResourcePropertyService.setProxyId(r0, r3);
		ResourcePropertyService.setProxyId(r1, r3);
		ArchitectureElementPropertyService.setOfferRoutingVariety(r1, false);
		ArchitectureElementPropertyService.setOfferRoutingVariety(r0, false);
		ArchitectureElementPropertyService.setOfferRoutingVariety(l1, false);
		ArchitectureElementPropertyService.setOfferRoutingVariety(l0, false);

		activatedVars.add(tVar0);
		activatedVars.add(tVar1);
		activatedVars.add(tVar2);
		activatedVars.add(tVarComm);
		activatedVars.add(dttVar0);
		activatedVars.add(dttVar1);
		activatedVars.add(dttVar2);

		mappings.add(m0);
		mappings.add(m1);
		mappings.add(m2);
		mappings.add(m3);
		mappings.add(m4);
		activatedVars.add(mVar0);
		activatedVars.add(mVar1);
		deactivatedVars.add(mVar2);
		deactivatedVars.add(mVar3);
		activatedVars.add(mVar4);
		mappingVariables.add(mVar0);
		mappingVariables.add(mVar1);
		mappingVariables.add(mVar2);
		mappingVariables.add(mVar3);
		mappingVariables.add(mVar4);
	}

	@Test
	public void test() {
		init();
		ProxyEncoder proxyEncoder = new ProxyEncoder();
		Set<Constraint> cs = proxyEncoder.toConstraints(flow_t0_t1, routing, mappingVariables);
		ConstraintVerifier verifyFlow1 = new ConstraintVerifier(activatedVars, deactivatedVars, cs);
		verifyFlow1.verifyVariableActivated(Variables.varDDLRR(flow_t0_t1, new DirectedLink(l0, r0, r3)));
		verifyFlow1.verifyVariableActivated(Variables.varDDLRR(flow_t0_t1, new DirectedLink(l1, r3, r1)));
		verifyFlow1.verifyVariableDeactivated(Variables.varDDLRR(flow_t0_t1, new DirectedLink(l0, r3, r0)));
		verifyFlow1.verifyVariableDeactivated(Variables.varDDLRR(flow_t0_t1, new DirectedLink(l1, r1, r3)));
		
		Set<Constraint> cs2 = proxyEncoder.toConstraints(flow_t0_t2, routing, mappingVariables);
		ConstraintVerifier verifyFlow2 = new ConstraintVerifier(activatedVars, deactivatedVars, cs2);
		verifyFlow2.verifyVariableActivated(Variables.varDDLRR(flow_t0_t2, new DirectedLink(l0, r0, r3)));
		verifyFlow2.verifyVariableDeactivated(Variables.varDDLRR(flow_t0_t2, new DirectedLink(l1, r3, r1)));
		verifyFlow2.verifyVariableDeactivated(Variables.varDDLRR(flow_t0_t2, new DirectedLink(l0, r3, r0)));
		verifyFlow2.verifyVariableDeactivated(Variables.varDDLRR(flow_t0_t2, new DirectedLink(l1, r1, r3)));
	}
}
