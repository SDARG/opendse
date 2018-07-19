package net.sf.opendse.encoding.routing;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.opt4j.satdecoding.Constraint;

import net.sf.opendse.encoding.routing.res.ProxyEncoderCompactTest2Res;
import net.sf.opendse.encoding.variables.ApplicationVariable;
import net.sf.opendse.encoding.variables.DTT;
import net.sf.opendse.encoding.variables.M;
import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;
import verification.ConstraintVerifier;

public class ProxyEncoderCompactTest2 {

	@Test
	public void test() {
		
		Specification spec = ProxyEncoderCompactTest2Res.getSpecification();
		
		Set<MappingVariable> mappingVariables = new HashSet<MappingVariable>();
		Set<Object> activated = new HashSet<Object>();
		for (Mapping<Task, Resource> m : spec.getMappings()) {
			M mVar = Variables.varM(m);
			mappingVariables.add(mVar);
			activated.add(mVar);
		}

		Task c = spec.getApplication().getVertex("c");

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
		
		Link l0 = spec.getArchitecture().getEdge("l0");
		Link l1 = spec.getArchitecture().getEdge("l1");
		Link l2 = spec.getArchitecture().getEdge("l2");
		Link l3 = spec.getArchitecture().getEdge("l3");
		Link l4 = spec.getArchitecture().getEdge("l4");
		Link l5 = spec.getArchitecture().getEdge("l5");

		Resource r0 = spec.getArchitecture().getVertex("r0");
		Resource r1 = spec.getArchitecture().getVertex("r1");
		Resource r2 = spec.getArchitecture().getVertex("r2");
		Resource r3 = spec.getArchitecture().getVertex("r3");
		Resource r4 = spec.getArchitecture().getVertex("r4");
		Resource r5 = spec.getArchitecture().getVertex("r5");

		ProxyEncoderCompact encoder = new ProxyEncoderCompact();
		OneDirectionEncoderDefault oneDirectionEncoder = new OneDirectionEncoderDefault();
		Set<Constraint> cs = encoder.toConstraints(c, spec.getRoutings().get(c), mappingVariables,
				applicationVariables);
		cs.addAll(oneDirectionEncoder.toConstraints(Variables.varT(c), spec.getRoutings().get(c)));
		ConstraintVerifier verifier = new ConstraintVerifier(cs);
		for (Object var : activated) {
			verifier.activateVariable(var);
		}
		verifier.activateVariable(Variables.varCLRR(c, l3, r1, r4));
		verifier.activateVariable(Variables.varCLRR(c, l4, r3, r4));
		verifier.deactivateVariable(Variables.varCLRR(c, l3, r4, r1));
		verifier.deactivateVariable(Variables.varCLRR(c, l4, r4, r3));
		verifier.deactivateVariable(Variables.varCLRR(c, l2, r3, r1));
		verifier.deactivateVariable(Variables.varCLRR(c, l2, r1, r3));
		
		verifier.verifyVariableActivated(Variables.varCLRR(c, l0, r0, r1));
		verifier.verifyVariableActivated(Variables.varCLRR(c, l5, r4, r5));
		verifier.verifyVariableActivated(Variables.varCLRR(c, l1, r2, r3));
		
		verifier.verifyVariableDeactivated(Variables.varCLRR(c, l0, r1, r0));
		verifier.verifyVariableDeactivated(Variables.varCLRR(c, l5, r5, r4));
		verifier.verifyVariableDeactivated(Variables.varCLRR(c, l1, r3, r2));
	}

}
