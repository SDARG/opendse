package net.sf.opendse.encoding.routing;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.opt4j.satdecoding.Constraint;

import net.sf.opendse.encoding.variables.ApplicationVariable;
import net.sf.opendse.encoding.variables.DTT;
import net.sf.opendse.encoding.variables.M;
import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Models;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;
import routing.ProxyEncoderCompactTestRes;
import net.sf.opendse.model.Models.DirectedLink;
import verification.ConstraintVerifier;

public class ProxyEncoderCompactTest {

	@Test
	public void test() {

		Specification spec = ProxyEncoderCompactTestRes.makeSpec();

		// mappings
		Set<MappingVariable> mappingVariables = new HashSet<MappingVariable>();
		Set<Object> activated = new HashSet<Object>();
		for (Mapping<Task, Resource> m : spec.getMappings()) {
			M mVar = Variables.varM(m);
			mappingVariables.add(mVar);
			activated.add(mVar);
		}

		Task c0 = spec.getApplication().getVertex("c0");
		Task c1 = spec.getApplication().getVertex("c1");

		Set<ApplicationVariable> applicationVariables = new HashSet<ApplicationVariable>();
		for (Task task : spec.getApplication()) {
			T tVar = Variables.varT(task);
			applicationVariables.add(tVar);
			activated.add(tVar);
		}
		for (Dependency dep : spec.getApplication().getEdges()) {
			DTT dVar = Variables.varDTT(dep, spec.getApplication().getSource(dep), spec.getApplication().getDest(dep));
			activated.add(dVar);
			applicationVariables.add(dVar);
		}

		Link l2 = spec.getArchitecture().getEdge("l2");
		Link l3 = spec.getArchitecture().getEdge("l3");
		Link l6 = spec.getArchitecture().getEdge("l6");
		Link l7 = spec.getArchitecture().getEdge("l7");
		Link l4 = spec.getArchitecture().getEdge("l4");
		Link l5 = spec.getArchitecture().getEdge("l5");
		Link l1 = spec.getArchitecture().getEdge("l1");
		Link l0 = spec.getArchitecture().getEdge("l0");
		Link l8 = spec.getArchitecture().getEdge("l8");
		Link l9 = spec.getArchitecture().getEdge("l9");
		Link l10 = spec.getArchitecture().getEdge("l10");
		Link l11 = spec.getArchitecture().getEdge("l11");

		Resource r0 = spec.getArchitecture().getVertex("r0");
		Resource r1 = spec.getArchitecture().getVertex("r1");
		Resource r2 = spec.getArchitecture().getVertex("r2");
		Resource r3 = spec.getArchitecture().getVertex("r3");
		Resource r4 = spec.getArchitecture().getVertex("r4");
		Resource r5 = spec.getArchitecture().getVertex("r5");
		Resource r6 = spec.getArchitecture().getVertex("r6");
		Resource r7 = spec.getArchitecture().getVertex("r7");
		Resource r8 = spec.getArchitecture().getVertex("r8");
		Resource r9 = spec.getArchitecture().getVertex("r9");
		Resource r10 = spec.getArchitecture().getVertex("r10");
		Resource r11 = spec.getArchitecture().getVertex("r11");

		ProxyEncoderCompact encoder = new ProxyEncoderCompact();
		OneDirectionEncoderDefault oneDirectionEncoder = new OneDirectionEncoderDefault();
		Set<Constraint> cs_c0 = encoder.toConstraints(c0, spec.getRoutings().get(c0), mappingVariables,
				applicationVariables);
		Set<Constraint> cs_c1 = encoder.toConstraints(c1, spec.getRoutings().get(c1), mappingVariables,
				applicationVariables);
		cs_c0.addAll(oneDirectionEncoder.toConstraints(Variables.varT(c0), spec.getRoutings().get(c0)));
		cs_c1.addAll(oneDirectionEncoder.toConstraints(Variables.varT(c1), spec.getRoutings().get(c1)));

		ConstraintVerifier verifier_c0 = new ConstraintVerifier(activated, new HashSet<Object>(), cs_c0);
		verifier_c0.deactivateVariable(Variables.varCLRR(c0, l9, r0, r10));
		verifier_c0.deactivateVariable(Variables.varCLRR(c0, l9, r10, r0));
		verifier_c0.deactivateVariable(Variables.varCLRR(c0, l11, r10, r11));
		verifier_c0.deactivateVariable(Variables.varCLRR(c0, l11, r11, r10));
		verifier_c0.deactivateVariable(Variables.varCLRR(c0, l10, r11, r0));
		verifier_c0.deactivateVariable(Variables.varCLRR(c0, l10, r0, r11));
		Set<DirectedLink> inactive = new HashSet<Models.DirectedLink>(Models.getLinks(spec.getArchitecture()));
		Set<DirectedLink> active = new HashSet<Models.DirectedLink>();
		active.add(new DirectedLink(l2, r3, r1));
		active.add(new DirectedLink(l3, r1, r4));
		active.add(new DirectedLink(l6, r4, r7));
		active.add(new DirectedLink(l7, r4, r8));
		inactive.removeAll(active);
		for (DirectedLink activeLink : active) {
			verifier_c0.verifyVariableActivated(Variables.varCLRR(c0, activeLink));
		}

		verifier_c0.verifyVariableDeactivated(Variables.varCLRR(c0, new DirectedLink(l2, r1, r3)));
		verifier_c0.verifyVariableDeactivated(Variables.varCLRR(c0, new DirectedLink(l3, r4, r1)));
		verifier_c0.verifyVariableDeactivated(Variables.varCLRR(c0, new DirectedLink(l6, r7, r4)));
		verifier_c0.verifyVariableDeactivated(Variables.varCLRR(c0, new DirectedLink(l7, r8, r4)));
		verifier_c0.verifyVariableDeactivated(Variables.varCLRR(c0, new DirectedLink(l8, r9, r4)));
		verifier_c0.verifyVariableDeactivated(Variables.varCLRR(c0, new DirectedLink(l8, r4, r9)));
		verifier_c0.verifyVariableDeactivated(Variables.varCLRR(c0, new DirectedLink(l5, r2, r6)));
		verifier_c0.verifyVariableDeactivated(Variables.varCLRR(c0, new DirectedLink(l5, r6, r2)));
		verifier_c0.verifyVariableDeactivated(Variables.varCLRR(c0, new DirectedLink(l4, r5, r2)));
		verifier_c0.verifyVariableDeactivated(Variables.varCLRR(c0, new DirectedLink(l4, r2, r5)));
		verifier_c0.verifyVariableDeactivated(Variables.varCLRR(c0, new DirectedLink(l1, r1, r2)));
		verifier_c0.verifyVariableDeactivated(Variables.varCLRR(c0, new DirectedLink(l1, r2, r1)));
		verifier_c0.verifyVariableDeactivated(Variables.varCLRR(c0, new DirectedLink(l0, r0, r1)));
		verifier_c0.verifyVariableDeactivated(Variables.varCLRR(c0, new DirectedLink(l0, r1, r0)));

		ConstraintVerifier verifier_c1 = new ConstraintVerifier(activated, new HashSet<Object>(), cs_c1);
		verifier_c1.activateVariable(Variables.varCLRR(c1, l9, r0, r10));
		verifier_c1.deactivateVariable(Variables.varCLRR(c1, l9, r10, r0));
		verifier_c1.deactivateVariable(Variables.varCLRR(c1, l11, r11, r10));
		verifier_c1.deactivateVariable(Variables.varCLRR(c1, l11, r10, r11));
		verifier_c1.deactivateVariable(Variables.varCLRR(c1, l10, r0, r11));
		verifier_c1.deactivateVariable(Variables.varCLRR(c1, l10, r11, r0));

		verifier_c1.verifyVariableActivated(Variables.varCLRR(c1, new DirectedLink(l4, r5, r2)));
		verifier_c1.verifyVariableActivated(Variables.varCLRR(c1, new DirectedLink(l1, r2, r1)));
		verifier_c1.verifyVariableActivated(Variables.varCLRR(c1, new DirectedLink(l0, r1, r0)));

		verifier_c1.verifyVariableDeactivated(Variables.varCLRR(c1, new DirectedLink(l4, r2, r5)));
		verifier_c1.verifyVariableDeactivated(Variables.varCLRR(c1, new DirectedLink(l1, r1, r2)));
		verifier_c1.verifyVariableDeactivated(Variables.varCLRR(c1, new DirectedLink(l0, r0, r1)));
		verifier_c1.verifyVariableDeactivated(Variables.varCLRR(c1, new DirectedLink(l4, r2, r5)));
		verifier_c1.verifyVariableDeactivated(Variables.varCLRR(c1, new DirectedLink(l6, r7, r4)));
		verifier_c1.verifyVariableDeactivated(Variables.varCLRR(c1, new DirectedLink(l6, r4, r7)));
		verifier_c1.verifyVariableDeactivated(Variables.varCLRR(c1, new DirectedLink(l8, r9, r4)));
		verifier_c1.verifyVariableDeactivated(Variables.varCLRR(c1, new DirectedLink(l8, r4, r9)));
		verifier_c1.verifyVariableDeactivated(Variables.varCLRR(c1, new DirectedLink(l7, r8, r4)));
		verifier_c1.verifyVariableDeactivated(Variables.varCLRR(c1, new DirectedLink(l7, r4, r8)));
		verifier_c1.verifyVariableDeactivated(Variables.varCLRR(c1, new DirectedLink(l3, r1, r4)));
		verifier_c1.verifyVariableDeactivated(Variables.varCLRR(c1, new DirectedLink(l3, r4, r1)));
		verifier_c1.verifyVariableDeactivated(Variables.varCLRR(c1, new DirectedLink(l2, r1, r3)));
		verifier_c1.verifyVariableDeactivated(Variables.varCLRR(c1, new DirectedLink(l2, r3, r1)));
		verifier_c1.verifyVariableDeactivated(Variables.varCLRR(c1, new DirectedLink(l5, r2, r6)));
		verifier_c1.verifyVariableDeactivated(Variables.varCLRR(c1, new DirectedLink(l5, r6, r2)));
	}
}
