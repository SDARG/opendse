package net.sf.opendse.encoding;

import org.junit.Test;
import org.opt4j.satdecoding.Constraint;

import edu.uci.ics.jung.graph.util.EdgeType;
import net.sf.opendse.encoding.allocation.AllocationEncodingUtilization;
import net.sf.opendse.encoding.application.ApplicationConstraintManagerDefault;
import net.sf.opendse.encoding.application.ApplicationEncodingMode;
import net.sf.opendse.encoding.mapping.MappingConstraintManagerDefault;
import net.sf.opendse.encoding.mapping.MappingEncodingMode;
import net.sf.opendse.encoding.routing.ActivationEncoderDefault;
import net.sf.opendse.encoding.routing.AdditionalRoutingConstraintsEncoderNone;
import net.sf.opendse.encoding.routing.CommunicationFlowRoutingManager;
import net.sf.opendse.encoding.routing.CommunicationFlowRoutingManagerDefault;
import net.sf.opendse.encoding.routing.CommunicationHierarchyEncoderDefault;
import net.sf.opendse.encoding.routing.CommunicationRoutingManagerDefault;
import net.sf.opendse.encoding.routing.CycleBreakEncoderColor;
import net.sf.opendse.encoding.routing.EndNodeEncoderMapping;
import net.sf.opendse.encoding.routing.OneDirectionEncoderDefault;
import net.sf.opendse.encoding.routing.ProxyEncoderCompact;
import net.sf.opendse.encoding.routing.RoutingEdgeEncoderNonRedundant;
import net.sf.opendse.encoding.routing.RoutingEncodingFlexible;
import net.sf.opendse.encoding.routing.RoutingResourceEncoderDefault;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Application;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.properties.ApplicationElementPropertyService;
import net.sf.opendse.model.properties.ApplicationElementPropertyService.ActivationModes;
import net.sf.opendse.optimization.SpecificationWrapper;
import verification.ConstraintVerifier;

import static org.mockito.Mockito.mock;

import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

public class AlternativesImplementationEncodingTest {

	protected static class Problem {

		protected Specification spec;

		protected Architecture<Resource, Link> arch = new Architecture<Resource, Link>();
		protected Resource r0 = new Resource("r0");
		protected Resource r1 = new Resource("r1");
		protected Resource r2 = new Resource("r2");
		protected Link l0 = new Link("l0");
		protected Link l1 = new Link("l1");

		protected Application<Task, Dependency> appl = new Application<Task, Dependency>();
		protected Task t0 = new Task("t0");
		protected Task t1 = new Task("t1");
		protected Task t2 = new Task("t2");
		protected Communication c0 = new Communication("c0");
		protected Communication c1 = new Communication("c1");
		protected Dependency d0 = new Dependency("d0");
		protected Dependency d1 = new Dependency("d1");
		protected Dependency d2 = new Dependency("d2");
		protected Dependency d3 = new Dependency("d3");
		protected Dependency d4 = new Dependency("d4");

		protected Mappings<Task, Resource> mappings = new Mappings<Task, Resource>();
		protected Mapping<Task, Resource> m0 = new Mapping<Task, Resource>("m0", t0, r0);
		protected Mapping<Task, Resource> m1 = new Mapping<Task, Resource>("m1", t1, r2);
		protected Mapping<Task, Resource> m2 = new Mapping<Task, Resource>("m2", t2, r1);

		protected Problem() {
			arch.addEdge(l0, r0, r1, EdgeType.UNDIRECTED);
			arch.addEdge(l1, r0, r2, EdgeType.UNDIRECTED);
			appl.addEdge(d4, t0, c0, EdgeType.DIRECTED);
			appl.addEdge(d0, c0, t2, EdgeType.DIRECTED);
			appl.addEdge(d1, c0, t1, EdgeType.DIRECTED);
			appl.addEdge(d2, t1, c1, EdgeType.DIRECTED);
			appl.addEdge(d3, c1, t2, EdgeType.DIRECTED);
			ApplicationElementPropertyService.setActivationMode(d0, ActivationModes.ALTERNATIVE);
			ApplicationElementPropertyService.setActivationMode(d1, ActivationModes.ALTERNATIVE);
			ApplicationElementPropertyService.setActivationMode(d2, ActivationModes.ALTERNATIVE);
			ApplicationElementPropertyService.setActivationMode(d3, ActivationModes.ALTERNATIVE);
			ApplicationElementPropertyService.setActivationMode(t1, ActivationModes.ALTERNATIVE);
			ApplicationElementPropertyService.setActivationMode(c1, ActivationModes.ALTERNATIVE);
			ApplicationElementPropertyService.setAlternativeAttributes(d0, "func", "a");
			ApplicationElementPropertyService.setAlternativeAttributes(d1, "func", "b");
			ApplicationElementPropertyService.setAlternativeAttributes(d2, "func", "b");
			ApplicationElementPropertyService.setAlternativeAttributes(d3, "func", "b");
			ApplicationElementPropertyService.setAlternativeAttributes(t1, "func", "b");
			ApplicationElementPropertyService.setAlternativeAttributes(c1, "func", "b");

			mappings.add(m0);
			mappings.add(m1);
			mappings.add(m2);

			spec = new Specification(appl, arch, mappings);
		}
	}

	protected static RoutingEncoding getRoutingEncoding() {
		CommunicationFlowRoutingManager communicationFlowManager = new CommunicationFlowRoutingManagerDefault(
				new ActivationEncoderDefault(), new EndNodeEncoderMapping(), new RoutingResourceEncoderDefault(),
				new RoutingEdgeEncoderNonRedundant(), new ProxyEncoderCompact());
		CommunicationRoutingManagerDefault routingEncoderManager = new CommunicationRoutingManagerDefault(
				new OneDirectionEncoderDefault(), new CycleBreakEncoderColor(),
				new CommunicationHierarchyEncoderDefault(), communicationFlowManager,
				new AdditionalRoutingConstraintsEncoderNone());

		return new RoutingEncodingFlexible(routingEncoderManager);
	}

	@Test
	public void test() {
		Problem prob = new Problem();
		SpecificationWrapper wrapper = mock(SpecificationWrapper.class);
		when(wrapper.getSpecification()).thenReturn(prob.spec);
		ApplicationEncoding applicationEncoding = new ApplicationEncodingMode(
				new ApplicationConstraintManagerDefault());
		MappingEncoding mappingEncoding = new MappingEncodingMode(new MappingConstraintManagerDefault());
		RoutingEncoding routingEncoding = getRoutingEncoding();
		AllocationEncoding allocationEncoding = new AllocationEncodingUtilization();
		SpecificationPreprocessor preprocessor = new SpecificationPreprocessorNone();
		ImplementationEncodingModularDefault encoding = new ImplementationEncodingModularDefault(preprocessor,
				applicationEncoding, mappingEncoding, routingEncoding, allocationEncoding, wrapper);
		Set<Constraint> cs = encoding.toConstraints();
		// alternative a tests
		Set<Object> active = new HashSet<Object>();
		active.add(Variables.varDTT(prob.d0, prob.c0, prob.t2));
		ConstraintVerifier verifyAlternativeA = new ConstraintVerifier(active, new HashSet<Object>(), cs);
		// verify correct appl encoding
		verifyAlternativeA.verifyVariableActivated(Variables.varT(prob.t0));
		verifyAlternativeA.verifyVariableActivated(Variables.varT(prob.c0));
		verifyAlternativeA.verifyVariableActivated(Variables.varT(prob.t2));
		verifyAlternativeA.verifyVariableActivated(Variables.varDTT(prob.d4, prob.t0, prob.c0));

		verifyAlternativeA.verifyVariableDeactivated(Variables.varT(prob.t1));
		verifyAlternativeA.verifyVariableDeactivated(Variables.varT(prob.c1));
		verifyAlternativeA.verifyVariableDeactivated(Variables.varDTT(prob.d1, prob.c0, prob.t1));
		verifyAlternativeA.verifyVariableDeactivated(Variables.varDTT(prob.d2, prob.t1, prob.c1));
		verifyAlternativeA.verifyVariableDeactivated(Variables.varDTT(prob.d3, prob.c1, prob.t2));
		// verify correct mapping encoding
		verifyAlternativeA.verifyVariableActivated(Variables.varM(prob.m0));
		verifyAlternativeA.verifyVariableActivated(Variables.varM(prob.m2));

		verifyAlternativeA.verifyVariableDeactivated(Variables.varM(prob.m1));
		// verify correct routing
		verifyAlternativeA.verifyVariableActivated(Variables.varCLRR(prob.c0, prob.l0, prob.r0, prob.r1));
		verifyAlternativeA.verifyVariableActivated(Variables.varCR(prob.c0, prob.r0));
		verifyAlternativeA.verifyVariableActivated(Variables.varCR(prob.c0, prob.r1));

		verifyAlternativeA.verifyVariableDeactivated(Variables.varCLRR(prob.c0, prob.l0, prob.r1, prob.r0));
		verifyAlternativeA.verifyVariableDeactivated(Variables.varCLRR(prob.c0, prob.l1, prob.r0, prob.r2));
		verifyAlternativeA.verifyVariableDeactivated(Variables.varCLRR(prob.c0, prob.l1, prob.r2, prob.r0));
		verifyAlternativeA.verifyVariableDeactivated(Variables.varCR(prob.c0, prob.r2));

		verifyAlternativeA.verifyVariableDeactivated(Variables.varCLRR(prob.c1, prob.l0, prob.r0, prob.r1));
		verifyAlternativeA.verifyVariableDeactivated(Variables.varCLRR(prob.c1, prob.l0, prob.r1, prob.r0));
		verifyAlternativeA.verifyVariableDeactivated(Variables.varCLRR(prob.c1, prob.l1, prob.r0, prob.r2));
		verifyAlternativeA.verifyVariableDeactivated(Variables.varCLRR(prob.c1, prob.l1, prob.r2, prob.r0));
		verifyAlternativeA.verifyVariableDeactivated(Variables.varCR(prob.c1, prob.r2));
		verifyAlternativeA.verifyVariableDeactivated(Variables.varCR(prob.c1, prob.r1));
		verifyAlternativeA.verifyVariableDeactivated(Variables.varCR(prob.c1, prob.r0));
		// verify correct allocation
		verifyAlternativeA.verifyVariableActivated(Variables.varR(prob.r0));
		verifyAlternativeA.verifyVariableActivated(Variables.varR(prob.r1));
		verifyAlternativeA.verifyVariableDeactivated(Variables.varR(prob.r2));
		verifyAlternativeA.verifyVariableActivated(Variables.varL(prob.l0));
		verifyAlternativeA.verifyVariableDeactivated(Variables.varL(prob.l1));

		// alternative b tests
		active.clear();
		active.add(Variables.varDTT(prob.d1, prob.c0, prob.t1));
		ConstraintVerifier verifyAlternativeB = new ConstraintVerifier(active, new HashSet<Object>(), cs);
		// verify correct application encoding
		verifyAlternativeB.verifyVariableActivated(Variables.varT(prob.t0));
		verifyAlternativeB.verifyVariableActivated(Variables.varT(prob.c0));
		verifyAlternativeB.verifyVariableActivated(Variables.varT(prob.t1));
		verifyAlternativeB.verifyVariableActivated(Variables.varT(prob.c1));
		verifyAlternativeB.verifyVariableActivated(Variables.varT(prob.t2));
		verifyAlternativeB.verifyVariableActivated(Variables.varDTT(prob.d4, prob.t0, prob.c0));
		verifyAlternativeB.verifyVariableActivated(Variables.varDTT(prob.d1, prob.c0, prob.t1));
		verifyAlternativeB.verifyVariableActivated(Variables.varDTT(prob.d2, prob.t1, prob.c1));
		verifyAlternativeB.verifyVariableActivated(Variables.varDTT(prob.d3, prob.c1, prob.t2));

		verifyAlternativeB.verifyVariableDeactivated(Variables.varDTT(prob.d0, prob.c0, prob.t2));
		// verify correct mapping encoding
		verifyAlternativeB.verifyVariableActivated(Variables.varM(prob.m0));
		verifyAlternativeB.verifyVariableActivated(Variables.varM(prob.m1));
		verifyAlternativeB.verifyVariableActivated(Variables.varM(prob.m2));
		// verify correct routing
		verifyAlternativeB.verifyVariableActivated(Variables.varCLRR(prob.c0, prob.l1, prob.r0, prob.r2));
		verifyAlternativeB.verifyVariableActivated(Variables.varCR(prob.c0, prob.r0));
		verifyAlternativeB.verifyVariableActivated(Variables.varCR(prob.c0, prob.r2));
		verifyAlternativeB.verifyVariableDeactivated(Variables.varCLRR(prob.c0, prob.l1, prob.r2, prob.r0));
		verifyAlternativeB.verifyVariableDeactivated(Variables.varCLRR(prob.c0, prob.l0, prob.r0, prob.r1));
		verifyAlternativeB.verifyVariableDeactivated(Variables.varCLRR(prob.c0, prob.l0, prob.r1, prob.r0));
		verifyAlternativeB.verifyVariableDeactivated(Variables.varCR(prob.c0, prob.r1));
		verifyAlternativeB.verifyVariableActivated(Variables.varCLRR(prob.c1, prob.l1, prob.r2, prob.r0));
		verifyAlternativeB.verifyVariableActivated(Variables.varCLRR(prob.c1, prob.l0, prob.r0, prob.r1));
		verifyAlternativeB.verifyVariableActivated(Variables.varCR(prob.c1, prob.r2));
		verifyAlternativeB.verifyVariableActivated(Variables.varCR(prob.c1, prob.r1));
		verifyAlternativeB.verifyVariableActivated(Variables.varCR(prob.c1, prob.r0));
		verifyAlternativeB.verifyVariableDeactivated(Variables.varCLRR(prob.c1, prob.l1, prob.r0, prob.r2));
		verifyAlternativeB.verifyVariableDeactivated(Variables.varCLRR(prob.c1, prob.l0, prob.r1, prob.r0));
		// verify correct allocation
		verifyAlternativeB.verifyVariableActivated(Variables.varR(prob.r0));
		verifyAlternativeB.verifyVariableActivated(Variables.varR(prob.r1));
		verifyAlternativeB.verifyVariableActivated(Variables.varR(prob.r2));
		verifyAlternativeB.verifyVariableActivated(Variables.varL(prob.l0));
		verifyAlternativeB.verifyVariableActivated(Variables.varL(prob.l1));
	}
}
