package net.sf.opendse.encoding.routing;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.opt4j.satdecoding.Constraint;

import net.sf.opendse.encoding.variables.DDdR;
import net.sf.opendse.encoding.variables.DDsR;
import net.sf.opendse.encoding.variables.DTT;
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

public class MappingEndNodeEncoderTest {
	@Test
	public void test() {
		Resource r0 = new Resource("r0");
		Resource r1 = new Resource("r1");
		Resource r2 = new Resource("r2");
		Architecture<Resource, Link> routing = new Architecture<Resource, Link>();
		routing.addVertex(r0);
		routing.addVertex(r1);
		routing.addVertex(r2);
		Task t0 = new Task("t0");
		Task t1 = new Task("t1");
		Communication comm = new Communication("comm");
		Dependency d0 = new Dependency("d0");
		Dependency d1 = new Dependency("d1");
		DTT srcDet = Variables.varDTT(d0, t0, comm);
		DTT destDet = Variables.varDTT(d1, comm, t1);
		CommunicationFlow commFlow = new CommunicationFlow(srcDet, destDet);
		Mapping<Task, Resource> m0 = new Mapping<Task, Resource>("m0", t0, r0);
		Mapping<Task, Resource> m1 = new Mapping<Task, Resource>("m1", t0, r1);
		Mapping<Task, Resource> m2 = new Mapping<Task, Resource>("m2", t1, r0);
		Mapping<Task, Resource> m3 = new Mapping<Task, Resource>("m3", t1, r1);
		Set<MappingVariable> mappingVars = new HashSet<MappingVariable>();
		mappingVars.add(Variables.varM(m0));
		mappingVars.add(Variables.varM(m1));
		mappingVars.add(Variables.varM(m2));
		mappingVars.add(Variables.varM(m3));
		MappingEndNodeEncoder encoder = new MappingEndNodeEncoder();
		Set<Constraint> cs = encoder.toConstraints(commFlow, routing, mappingVars);
		assertEquals(10, cs.size());
		DDsR r0Src = Variables.varDDsR(commFlow, r0);
		DDsR r1Src = Variables.varDDsR(commFlow, r1);
		DDdR r0Dest = Variables.varDDdR(commFlow, r0);
		DDsR r2Src = Variables.varDDsR(commFlow, r2);
		DDdR r1Dest = Variables.varDDdR(commFlow, r1);
		DDdR r2Dest = Variables.varDDdR(commFlow, r2);
		Set<Object> activated = new HashSet<Object>();
		activated.add(Variables.varM(m0));
		activated.add(Variables.varM(m1));
		activated.add(Variables.varM(m2));
		Set<Object> deactivated = new HashSet<Object>();
		deactivated.add(Variables.varM(m3));
		ConstraintVerifier verifyEndNodeSetting = new ConstraintVerifier(activated, deactivated, cs);
		verifyEndNodeSetting.verifyVariableActivated(r0Src);
		verifyEndNodeSetting.verifyVariableActivated(r1Src);
		verifyEndNodeSetting.verifyVariableActivated(r0Dest);
		verifyEndNodeSetting.verifyVariableDeactivated(r2Src);
		verifyEndNodeSetting.verifyVariableDeactivated(r2Dest);
		verifyEndNodeSetting.verifyVariableDeactivated(r1Dest);
	}
}
