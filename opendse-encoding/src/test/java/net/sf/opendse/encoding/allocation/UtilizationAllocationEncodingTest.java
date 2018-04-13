package net.sf.opendse.encoding.allocation;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.opt4j.satdecoding.Constraint;

import edu.uci.ics.jung.graph.util.EdgeType;
import net.sf.opendse.encoding.variables.CLRR;
import net.sf.opendse.encoding.variables.CR;
import net.sf.opendse.encoding.variables.M;
import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.encoding.variables.RoutingVariable;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;
import verification.ConstraintVerifier;

public class UtilizationAllocationEncodingTest {

	@Test
	public void test() {
		Resource r0 = new Resource("r0");
		Resource r1 = new Resource("r1");
		Resource r2 = new Resource("r2");
		Link l0 = new Link("l0");
		Link l1 = new Link("l1");
		Architecture<Resource, Link> arch = new Architecture<Resource, Link>();
		arch.addEdge(l0, r1, r0, EdgeType.UNDIRECTED);
		arch.addEdge(l1, r2, r0, EdgeType.UNDIRECTED);
		Task t = new Task("t0");
		Communication comm = new Communication("c");
		Mapping<Task, Resource> m0 = new Mapping<Task, Resource>("m0", t, r1);
		Mapping<Task, Resource> m1 = new Mapping<Task, Resource>("m1", t, r2);
		M mVar0 = Variables.varM(m0);
		M mVar1 = Variables.varM(m1);
		CLRR cl0_f = Variables.varCLRR(comm, l0, r1, r0);
		CLRR cl0_b = Variables.varCLRR(comm, l0, r0, r1);
		CLRR cl1_f = Variables.varCLRR(comm, l1, r0, r2);
		CLRR cl1_b = Variables.varCLRR(comm, l1, r2, r0);
		CR cr1 = Variables.varCR(comm, r1);
		CR cr2 = Variables.varCR(comm, r2);
		CR cr0 = Variables.varCR(comm, r0);
		AllocationEncodingUtilization encoder = new AllocationEncodingUtilization();
		Set<MappingVariable> mappingVariables = new HashSet<MappingVariable>();
		mappingVariables.add(mVar0);
		mappingVariables.add(mVar1);
		Set<RoutingVariable> routingVariables = new HashSet<RoutingVariable>();
		routingVariables.add(cl1_b);
		routingVariables.add(cl0_b);
		routingVariables.add(cl1_f);
		routingVariables.add(cl0_f);
		routingVariables.add(cr0);
		routingVariables.add(cr1);
		routingVariables.add(cr2);
		Set<Constraint> cs = encoder.toConstraints(mappingVariables, routingVariables, arch);
		assertEquals(22, cs.size());
		Set<Object> activated = new HashSet<Object>();
		activated.add(mVar0);
		activated.add(cl0_f);
		activated.add(cr0);
		activated.add(cr1);
		Set<Object> deactivated = new HashSet<Object>();
		deactivated.add(mVar1);
		deactivated.add(cl0_b);
		deactivated.add(cl1_b);
		deactivated.add(cl1_f);
		deactivated.add(cr2);
		ConstraintVerifier verifyAllocation = new ConstraintVerifier(activated, deactivated, cs);
		verifyAllocation.verifyVariableActivated(Variables.varR(r1));
		verifyAllocation.verifyVariableActivated(Variables.varR(r0));
		verifyAllocation.verifyVariableDeactivated(Variables.varR(r2));
		verifyAllocation.verifyVariableActivated(Variables.varL(l0));
		verifyAllocation.verifyVariableDeactivated(Variables.varL(l1));
	}
}
