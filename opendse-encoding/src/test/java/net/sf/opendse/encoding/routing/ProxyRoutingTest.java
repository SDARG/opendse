package net.sf.opendse.encoding.routing;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.opt4j.satdecoding.Constraint;

import net.sf.opendse.encoding.RoutingEncoding;
import net.sf.opendse.encoding.variables.ApplicationVariable;
import net.sf.opendse.encoding.variables.CLRR;
import net.sf.opendse.encoding.variables.CR;
import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Application;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Routings;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;
import routing.ProxyRoutingTestRes;
import verification.ConstraintVerifier;

public class ProxyRoutingTest {

	protected static RoutingEncoding getRoutingEncoding() {
		CommunicationFlowRoutingManager communicationFlowManager = new CommunicationFlowRoutingManagerDefault(
				new ActivationEncoderDefault(), new EndNodeEncoderMapping(), new RoutingResourceEncoderDefault(),
				new RoutingEdgeEncoderNonRedundant());
		CommunicationRoutingManagerDefault routingEncoderManager = new CommunicationRoutingManagerDefault(
				new OneDirectionEncoderDefault(), new CycleBreakEncoderColor(),
				new CommunicationHierarchyEncoderDefault(), communicationFlowManager, new ProxyEncoderCompact(),
				new AdditionalRoutingConstraintsEncoderNone());

		return new RoutingEncodingFlexible(routingEncoderManager);
	}

	@Test
	public void test() {
		Specification spec = ProxyRoutingTestRes.makeSpec();
		Application<Task, Dependency> appl = spec.getApplication();
		Architecture<Resource, Link> arch = spec.getArchitecture();
		Mappings<Task, Resource> mappings = spec.getMappings();
		Routings<Task, Resource, Link> routings = spec.getRoutings();
		Set<ApplicationVariable> applVars = new HashSet<ApplicationVariable>();
		for (Task t : spec.getApplication()) {
			applVars.add(Variables.varT(t));
		}
		for (Dependency d : spec.getApplication().getEdges()) {
			applVars.add(Variables.varDTT(d, spec.getApplication().getSource(d), spec.getApplication().getDest(d)));
		}
		Set<MappingVariable> mappingVariables = new HashSet<MappingVariable>();
		for (Mapping<Task, Resource> m : mappings) {
			mappingVariables.add(Variables.varM(m));
		}
		RoutingEncoding encoder = getRoutingEncoding();
		Set<Constraint> cs = encoder.toConstraints(applVars, mappingVariables, routings);
		ConstraintVerifier verifyRouting = new ConstraintVerifier(cs);
		for (ApplicationVariable applVar : applVars) {
			verifyRouting.activateVariable(applVar);
		}
		for (MappingVariable mVar : mappingVariables) {
			verifyRouting.activateVariable(mVar);
		}
		
		Task c0 = appl.getVertex("c0");
		Task c1 = appl.getVertex("c1");
		Task c2 = appl.getVertex("c2");
		
		Resource r0 = arch.getVertex("r0");
		Resource r1 = arch.getVertex("r1");
		Resource r2 = arch.getVertex("r2");
		Resource r3 = arch.getVertex("r3");
		Resource r4 = arch.getVertex("r4");
		
		Link l0 = arch.getEdge("l0");
		Link l1 = arch.getEdge("l1");
		Link l2 = arch.getEdge("l2");
		Link l3 = arch.getEdge("l3");

		CLRR c0l2 = Variables.varCLRR(c0, l2, r1, r2);
		CLRR c0l0 = Variables.varCLRR(c0, l0, r0, r1);
		CLRR c0l3 = Variables.varCLRR(c0, l3, r2, r3);
		CLRR c0l1 = Variables.varCLRR(c0, l1, r1, r4);

		CLRR c1l2 = Variables.varCLRR(c1, l2, r1, r2);
		CLRR c1l0 = Variables.varCLRR(c1, l0, r0, r1);
		CLRR c1l3 = Variables.varCLRR(c1, l3, r2, r3);
		CLRR c1l1 = Variables.varCLRR(c1, l1, r1, r4);

		CLRR c2l2 = Variables.varCLRR(c2, l2, r1, r2);
		CLRR c2l0 = Variables.varCLRR(c2, l0, r0, r1);
		CLRR c2l3 = Variables.varCLRR(c2, l3, r2, r3);
		CLRR c2l1 = Variables.varCLRR(c2, l1, r1, r4);
		CR c2r0 = Variables.varCR(c2, r0);
		CR c2r1 = Variables.varCR(c2, r1);
		CR c2r2 = Variables.varCR(c2, r2);
		CR c2r3 = Variables.varCR(c2, r3);
		CR c2r4 = Variables.varCR(c2, r4);

		verifyRouting.verifyVariableActivated(c0l2);
		verifyRouting.verifyVariableActivated(c0l0);
		verifyRouting.verifyVariableActivated(c0l3);
		verifyRouting.verifyVariableDeactivated(c0l1);

		verifyRouting.verifyVariableDeactivated(c1l2);
		verifyRouting.verifyVariableActivated(c1l0);
		verifyRouting.verifyVariableDeactivated(c1l3);
		verifyRouting.verifyVariableActivated(c1l1);

		verifyRouting.verifyVariableDeactivated(c2l0);
		verifyRouting.verifyVariableDeactivated(c2l1);
		verifyRouting.verifyVariableDeactivated(c2l2);
		verifyRouting.verifyVariableDeactivated(c2l3);
		verifyRouting.verifyVariableActivated(c2r0);
		verifyRouting.verifyVariableDeactivated(c2r1);
		verifyRouting.verifyVariableDeactivated(c2r2);
		verifyRouting.verifyVariableDeactivated(c2r3);
		verifyRouting.verifyVariableDeactivated(c2r4);
	}
}
