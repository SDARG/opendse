package net.sf.opendse.encoding.routing;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.opt4j.satdecoding.Constraint;

import edu.uci.ics.jung.graph.util.EdgeType;
import net.sf.opendse.encoding.variables.ApplicationVariable;
import net.sf.opendse.encoding.variables.CLRR;
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
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Routings;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.properties.ResourcePropertyService;
import verification.ConstraintVerifier;

public class ProxyRoutingTest {

	protected Task t0 = new Task("t0");
	protected Task t1 = new Task("t1");
	protected Task t2 = new Task("t2");
	protected Task t3 = new Task("t3");

	protected Communication c0 = new Communication("c0");
	protected Communication c1 = new Communication("c1");

	protected Dependency d0 = new Dependency("d0");
	protected Dependency d1 = new Dependency("d1");
	protected Dependency d2 = new Dependency("d2");
	protected Dependency d3 = new Dependency("d3");

	protected T tvar0 = Variables.varT(t0);
	protected T tvar1 = Variables.varT(t1);
	protected T tvar2 = Variables.varT(t2);
	protected T tvar3 = Variables.varT(t3);
	protected T tvarc0 = Variables.varT(c0);
	protected T tvarc1 = Variables.varT(c1);

	protected DTT dttVar0 = Variables.varDTT(d0, t0, c0);
	protected DTT dttVar1 = Variables.varDTT(d1, c0, t1);
	protected DTT dttVar2 = Variables.varDTT(d2, t2, c1);
	protected DTT dttVar3 = Variables.varDTT(d3, c1, t3);

	protected Set<ApplicationVariable> applVars;

	protected Resource r0 = new Resource("r0");
	protected Resource r1 = new Resource("r1");
	protected Resource r2 = new Resource("r2");
	protected Resource r3 = new Resource("r3");
	protected Resource r4 = new Resource("r4");

	protected Link l0 = new Link("l0");
	protected Link l1 = new Link("l1");
	protected Link l2 = new Link("l2");
	protected Link l3 = new Link("l3");

	protected Architecture<Resource, Link> routing = new Architecture<Resource, Link>();
	protected Routings<Task, Resource, Link> routings = new Routings<Task, Resource, Link>();

	protected Mappings<Task, Resource> mappings = new Mappings<Task, Resource>();
	protected Mapping<Task, Resource> m0 = new Mapping<Task, Resource>("m0", t0, r0);
	protected Mapping<Task, Resource> m1 = new Mapping<Task, Resource>("m1", t1, r3);
	protected Mapping<Task, Resource> m2 = new Mapping<Task, Resource>("m2", t2, r0);
	protected Mapping<Task, Resource> m3 = new Mapping<Task, Resource>("m3", t3, r4);

	protected M mVar0 = Variables.varM(m0);
	protected M mVar1 = Variables.varM(m1);
	protected M mVar2 = Variables.varM(m2);
	protected M mVar3 = Variables.varM(m3);
	protected Set<MappingVariable> mappingVariables = new HashSet<MappingVariable>();

	protected void init() {
		applVars = new HashSet<ApplicationVariable>();
		applVars.add(tvar0);
		applVars.add(tvar1);
		applVars.add(tvar2);
		applVars.add(tvar3);
		applVars.add(tvarc0);
		applVars.add(tvarc1);
		applVars.add(dttVar0);
		applVars.add(dttVar1);
		applVars.add(dttVar2);
		applVars.add(dttVar3);

		ResourcePropertyService.setProxyId(r0, r1);
		ResourcePropertyService.setProxyId(r4, r1);
		ResourcePropertyService.setProxyId(r3, r2);
		routing.addEdge(l2, r1, r2, EdgeType.UNDIRECTED);
		routings.set(c0, routing);
		routings.set(c1, routing);

		mappings.add(m0);
		mappings.add(m1);
		mappings.add(m2);
		mappings.add(m3);
		mappingVariables.add(mVar0);
		mappingVariables.add(mVar1);
		mappingVariables.add(mVar2);
		mappingVariables.add(mVar3);
	}

	@Test
	public void test() {
		init();
		RoutingEncodingFlexible encoder = new RoutingEncodingFlexible(new CommunicationRoutingManagerDefault());

		Set<Constraint> cs = encoder.toConstraints(applVars, mappingVariables, routings);
		ConstraintVerifier verifyRouting = new ConstraintVerifier(cs);
		for (ApplicationVariable applVar : applVars) {
			verifyRouting.activateVariable(applVar);
		}
		for (MappingVariable mVar : mappingVariables) {
			verifyRouting.activateVariable(mVar);
		}

		
		CLRR c0l2 = Variables.varCLRR(c0, l2, r1, r2);
		CLRR c0l0 = Variables.varCLRR(c0, l0, r0, r1);
		CLRR c0l3 = Variables.varCLRR(c0, l3, r2, r3);
		CLRR c0l1 = Variables.varCLRR(c0, l1, r1, r4);
		
		CLRR c1l2 = Variables.varCLRR(c1, l2, r1, r2);
		CLRR c1l0 = Variables.varCLRR(c1, l0, r0, r1);
		CLRR c1l3 = Variables.varCLRR(c1, l3, r2, r3);
		CLRR c1l1 = Variables.varCLRR(c1, l1, r1, r4);
		
		verifyRouting.verifyVariableActivated(c0l2);
		verifyRouting.verifyVariableActivated(c0l0);
		verifyRouting.verifyVariableActivated(c0l3);
		verifyRouting.verifyVariableDeactivated(c0l1);
		
		verifyRouting.verifyVariableDeactivated(c1l2);
		verifyRouting.verifyVariableDeactivated(c1l0);
		verifyRouting.verifyVariableDeactivated(c1l3);
		verifyRouting.verifyVariableDeactivated(c1l1);
	}

}
