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

public class EndNodeEncoderMappingTest {
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
		Task t2 = new Task("t2");
		Task t3 = new Task("t3");
		Communication comm = new Communication("comm");
		Communication comm2 = new Communication("comm2");
		Dependency d0 = new Dependency("d0");
		Dependency d1 = new Dependency("d1");
		Dependency d2 = new Dependency("d2");
		Dependency d3 = new Dependency("d3");
		DTT srcDet = Variables.varDTT(d0, t0, comm);
		DTT destDet = Variables.varDTT(d1, comm, t1);
		DTT srcDet2 = Variables.varDTT(d2, t2, comm2);
		DTT destDet2 = Variables.varDTT(d3, comm2, t3);
		CommunicationFlow commFlow = new CommunicationFlow(srcDet, destDet);
		CommunicationFlow commFlow2 = new CommunicationFlow(srcDet2, destDet2);
		Mapping<Task, Resource> m0 = new Mapping<Task, Resource>("m0", t0, r0);
		Mapping<Task, Resource> m1 = new Mapping<Task, Resource>("m1", t0, r1);
		Mapping<Task, Resource> m2 = new Mapping<Task, Resource>("m2", t1, r0);
		Mapping<Task, Resource> m3 = new Mapping<Task, Resource>("m3", t1, r1);
		Mapping<Task, Resource> m4 = new Mapping<Task, Resource>("m4", t2, r1);
		Mapping<Task, Resource> m5 = new Mapping<Task, Resource>("m5", t3, r1);
		Set<MappingVariable> mappingVars = new HashSet<MappingVariable>();
		mappingVars.add(Variables.varM(m0));
		mappingVars.add(Variables.varM(m1));
		mappingVars.add(Variables.varM(m2));
		mappingVars.add(Variables.varM(m3));
		mappingVars.add(Variables.varM(m4));
		mappingVars.add(Variables.varM(m5));
		EndNodeEncoderMapping encoder = new EndNodeEncoderMapping();
		Set<Constraint> cs = encoder.toConstraints(commFlow, routing, mappingVars);
		cs.addAll(encoder.toConstraints(commFlow2, routing, mappingVars));
		assertEquals(18, cs.size());
		DDsR r0Src = Variables.varDDsR(commFlow, r0);
		DDsR r1Src = Variables.varDDsR(commFlow, r1);
		DDdR r0Dest = Variables.varDDdR(commFlow, r0);
		DDsR r2Src = Variables.varDDsR(commFlow, r2);
		DDdR r1Dest = Variables.varDDdR(commFlow, r1);
		DDdR r2Dest = Variables.varDDdR(commFlow, r2);
		DDdR r1Des2 = Variables.varDDdR(commFlow2, r1);
		DDsR r1Src2 = Variables.varDDsR(commFlow2, r1);
		Set<Object> activated = new HashSet<Object>();
		activated.add(Variables.varM(m0));
		activated.add(Variables.varM(m1));
		activated.add(Variables.varM(m2));
		activated.add(Variables.varM(m4));
		activated.add(Variables.varM(m5));
		Set<Object> deactivated = new HashSet<Object>();
		deactivated.add(Variables.varM(m3));
		ConstraintVerifier verifyEndNodeSetting = new ConstraintVerifier(activated, deactivated, cs);
		verifyEndNodeSetting.verifyVariableActivated(r0Src);
		verifyEndNodeSetting.verifyVariableActivated(r1Src);
		verifyEndNodeSetting.verifyVariableActivated(r0Dest);
		verifyEndNodeSetting.verifyVariableDeactivated(r2Src);
		verifyEndNodeSetting.verifyVariableDeactivated(r2Dest);
		verifyEndNodeSetting.verifyVariableDeactivated(r1Dest);
		verifyEndNodeSetting.verifyVariableActivated(r1Des2);
		verifyEndNodeSetting.verifyVariableActivated(r1Src2);
	}
}
