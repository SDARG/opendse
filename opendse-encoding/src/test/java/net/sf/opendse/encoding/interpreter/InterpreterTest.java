package net.sf.opendse.encoding.interpreter;

import static org.junit.Assert.*;

import org.junit.Test;
import org.opt4j.satdecoding.Model;

import edu.uci.ics.jung.graph.util.EdgeType;
import net.sf.opendse.encoding.ImplementationEncoding;
import net.sf.opendse.encoding.variables.AllocationVariable;
import net.sf.opendse.encoding.variables.ApplicationVariable;
import net.sf.opendse.encoding.variables.CLRR;
import net.sf.opendse.encoding.variables.CR;
import net.sf.opendse.encoding.variables.DTT;
import net.sf.opendse.encoding.variables.InterfaceVariable;
import net.sf.opendse.encoding.variables.L;
import net.sf.opendse.encoding.variables.M;
import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.encoding.variables.R;
import net.sf.opendse.encoding.variables.RoutingVariable;
import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Application;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Element;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Routings;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;
import net.sf.opendse.optimization.SpecificationWrapper;

import static org.mockito.Mockito.mock;

import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.verify;

public class InterpreterTest {

	public static Interpreter getInterpreter() {
		ImplementationEncoding mockEncoding = mock(ImplementationEncoding.class);
		SpecificationWrapper mockWrapper = mock(SpecificationWrapper.class);
		return new Interpreter(mockEncoding, mockWrapper);
	}
	
	@Test
	public void testToImplementation() {
		ImplementationEncoding encoding = mock(ImplementationEncoding.class);
		SpecificationWrapper wrapper = mock(SpecificationWrapper.class);
		when(encoding.getInterfaceVariables()).thenReturn(new HashSet<InterfaceVariable>());
		Specification s = new Specification(new Application<Task, Dependency>(), new Architecture<Resource, Link>(), new Mappings<Task, Resource>());
		when(wrapper.getSpecification()).thenReturn(s);
		Interpreter inter = new Interpreter(encoding, wrapper);
		Model m = new Model();
		Specification spec = inter.toImplementation(m);
		assertEquals(0, spec.getApplication().getVertexCount());
		assertEquals(0, spec.getArchitecture().getVertexCount());
		assertEquals(0, spec.getMappings().size());
		assertEquals(0, spec.getRoutings().getTasks().size());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testDecodeRoutingsExc() {
		RoutingVariable mockVar = mock(RoutingVariable.class);
		Set<RoutingVariable> routingVars = new HashSet<RoutingVariable>();
		routingVars.add(mockVar);
		Interpreter inter = getInterpreter();
		inter.decodeRoutings(routingVars, new Model(), new Application<Task, Dependency>(), new Architecture<Resource, Link>());
	}
	
	@Test
	public void testDecodeRoutings() {
		Task t1 = new Task("t1");
		Task t2 = new Task("t2");
		Communication comm = new Communication("comm");
		Dependency d1 = new Dependency("d1");
		Dependency d2 = new Dependency("d2");
		Application<Task, Dependency> appl = new Application<Task, Dependency>();
		appl.addEdge(d1, t1, comm, EdgeType.DIRECTED);
		appl.addEdge(d2, comm, t2, EdgeType.DIRECTED);
		Resource r1 = new Resource("r1");
		Resource r2 = new Resource("r2");
		Resource r3 = new Resource("r3");
		Link l1 = new Link("l1");
		Link l2 = new Link("l2");
		Architecture<Resource, Link> arch = new Architecture<Resource, Link>();
		arch.addEdge(l1, r1, r2, EdgeType.UNDIRECTED);
		arch.addEdge(l2, r2, r3, EdgeType.UNDIRECTED);
		Set<RoutingVariable> routingVars = new HashSet<RoutingVariable>();
		CR cr1 = Variables.varCR(comm, r1);
		CR cr2 = Variables.varCR(comm, r2);
		CR cr3 = Variables.varCR(comm, r3);
		CLRR clrr1 = Variables.varCLRR(comm, l1, r1, r2);
		CLRR clrr2 = Variables.varCLRR(comm, l2, r2, r3);
		routingVars.add(cr1);
		routingVars.add(cr2);
		routingVars.add(cr3);
		routingVars.add(clrr1);
		routingVars.add(clrr2);
		Model m = new Model();
		m.set(cr1, true);
		m.set(cr2, true);
		m.set(cr3, false);
		m.set(clrr1, true);
		m.set(clrr2, false);
		Interpreter inter = getInterpreter();
		Routings<Task, Resource, Link> decodedRoutings = inter.decodeRoutings(routingVars, m, appl, arch);
		Architecture<Resource, Link> routing = decodedRoutings.get(comm);
		assertTrue(routing.getVertex(r1) != null);
		assertTrue(routing.getVertex(r2) != null);
		assertTrue(routing.getVertex(r3) == null);
		assertTrue(routing.getEdge(l1) != null);
		assertTrue(routing.getEdge(l2) == null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testMissingElementExc() {
		Element e = new Element("e");
		Interpreter inter = getInterpreter();
		inter.missingElementException(e);
	}

	@Test
	public void testDecodeMappings() {
		Resource r = new Resource("r");
		Task t1 = new Task("t1");
		Task t2 = new Task("t2");
		Mapping<Task, Resource> m1 = new Mapping<Task, Resource>("m1", t1, r);
		Mapping<Task, Resource> m2 = new Mapping<Task, Resource>("m2", t2, r);
		Mappings<Task, Resource> mappings = new Mappings<Task, Resource>();
		mappings.add(m1);
		mappings.add(m2);
		Architecture<Resource, Link> arch = new Architecture<Resource, Link>();
		arch.addVertex(r);
		Application<Task, Dependency> appl = new Application<Task, Dependency>();
		appl.addVertex(t1);
		appl.addVertex(t2);
		M mVar1 = Variables.varM(m1);
		M mVar2 = Variables.varM(m2);
		Set<MappingVariable> mappingVars = new HashSet<MappingVariable>();
		mappingVars.add(mVar1);
		mappingVars.add(mVar2);
		Model m = new Model();
		m.set(mVar1, true);
		m.set(mVar2, false);
		Interpreter inter = getInterpreter();
		Mappings<Task, Resource> implMappings = inter.decodeMappings(mappingVars, m, mappings, arch, appl);
		assertEquals(1, implMappings.get(r).size());
		assertEquals(m1, implMappings.get(r).iterator().next());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDecodeMappingsExc() {
		Interpreter inter = getInterpreter();
		MappingVariable mockVar = mock(MappingVariable.class);
		Set<MappingVariable> mappingVars = new HashSet<MappingVariable>();
		mappingVars.add(mockVar);
		Model m = new Model();
		m.set(mockVar, true);
		inter.decodeMappings(mappingVars, m, new Mappings<Task, Resource>(), new Architecture<Resource, Link>(),
				new Application<Task, Dependency>());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetImplementationMappingExc2() {
		Task t = new Task("t");
		Resource r = new Resource("r");
		Mapping<Task, Resource> m = new Mapping<Task, Resource>("m", t, r);
		Mappings<Task, Resource> mappings = new Mappings<Task, Resource>();
		Architecture<Resource, Link> arch = new Architecture<Resource, Link>();
		arch.addVertex(r);
		Interpreter inter = getInterpreter();
		inter.getImplementationMapping(m, mappings, arch, new Application<Task, Dependency>());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetImplementationMappingExc1() {
		Task t = new Task("t");
		Resource r = new Resource("r");
		Mapping<Task, Resource> m = new Mapping<Task, Resource>("m", t, r);
		Mappings<Task, Resource> mappings = new Mappings<Task, Resource>();
		Application<Task, Dependency> appl = new Application<Task, Dependency>();
		appl.addVertex(t);
		Interpreter inter = getInterpreter();
		inter.getImplementationMapping(m, mappings, new Architecture<Resource, Link>(), appl);
	}

	@Test
	public void testGetImplementationMapping() {
		Task t = new Task("t");
		Resource r = new Resource("r");
		Mapping<Task, Resource> m = new Mapping<Task, Resource>("m", t, r);
		Mappings<Task, Resource> mappings = new Mappings<Task, Resource>();
		Architecture<Resource, Link> arch = new Architecture<Resource, Link>();
		arch.addVertex(r);
		Application<Task, Dependency> appl = new Application<Task, Dependency>();
		appl.addVertex(t);
		Interpreter inter = getInterpreter();
		Mapping<Task, Resource> newM = inter.getImplementationMapping(m, mappings, arch, appl);
		mappings.add(m);
		Mapping<Task, Resource> childM = inter.getImplementationMapping(m, mappings, arch, appl);
		assertEquals(m, newM);
		assertNotEquals(m, newM.getParent());
		assertEquals(m, childM);
		assertEquals(m, childM.getParent());
		assertEquals(t, childM.getSource());
		assertEquals(r, childM.getTarget());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDecodeAllocationExc() {
		AllocationVariable mockVar = mock(AllocationVariable.class);
		Set<AllocationVariable> allocVars = new HashSet<AllocationVariable>();
		allocVars.add(mockVar);
		Interpreter inter = getInterpreter();
		Model m = new Model();
		m.set(mockVar, true);
		inter.decodeAllocation(allocVars, m, new Architecture<Resource, Link>());
	}

	@Test
	public void testDecodeAllocation() {
		Resource r1 = new Resource("r1");
		Resource r2 = new Resource("r2");
		Resource r3 = new Resource("r3");
		Link l1 = new Link("l1");
		Link l2 = new Link("l2");
		Architecture<Resource, Link> arch = new Architecture<Resource, Link>();
		arch.addEdge(l1, r1, r2, EdgeType.UNDIRECTED);
		arch.addEdge(l2, r2, r3, EdgeType.UNDIRECTED);
		R rVar1 = Variables.varR(r1);
		R rVar2 = Variables.varR(r2);
		R rVar3 = Variables.varR(r3);
		L lVar1 = Variables.varL(l1);
		L lVar2 = Variables.varL(l2);
		Model m = new Model();
		m.set(rVar1, true);
		m.set(rVar2, true);
		m.set(rVar3, false);
		m.set(lVar1, true);
		m.set(lVar2, false);
		Set<AllocationVariable> allocationVars = new HashSet<AllocationVariable>();
		allocationVars.add(rVar1);
		allocationVars.add(rVar2);
		allocationVars.add(rVar3);
		allocationVars.add(lVar1);
		allocationVars.add(lVar2);
		Interpreter inter = getInterpreter();
		Architecture<Resource, Link> implArch = inter.decodeAllocation(allocationVars, m, arch);
		assertTrue(implArch.getVertex(r1) != null);
		assertTrue(implArch.getVertex(r2) != null);
		assertTrue(implArch.getVertex(r3) == null);
		assertTrue(implArch.getEdge(l1) != null);
		assertTrue(implArch.getEdge(l2) == null);
	}

	@Test
	public void testAddImplementationLink() {
		Link l = new Link("l");
		Resource r1 = new Resource("r1");
		Resource r2 = new Resource("r2");
		Architecture<Resource, Link> arch = new Architecture<Resource, Link>();
		arch.addEdge(l, r1, r2, EdgeType.UNDIRECTED);
		Interpreter inter = getInterpreter();
		Architecture<Resource, Link> implArch = new Architecture<Resource, Link>();
		inter.addImplementationLink(l, implArch, arch);
		assertEquals(l, implArch.getEdge(l));
		inter.addImplementationLink(l, implArch, arch);
		assertEquals(l, implArch.getEdge(l));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetImplementationLinkExc() {
		Link l = new Link("l");
		Interpreter inter = getInterpreter();
		inter.getImplementationLink(l, new Architecture<Resource, Link>());
	}

	@Test
	public void testGetImplementationLink() {
		Link l = new Link("l");
		Resource r1 = new Resource("r1");
		Resource r2 = new Resource("r2");
		Architecture<Resource, Link> arch = new Architecture<Resource, Link>();
		arch.addEdge(l, r1, r2, EdgeType.UNDIRECTED);
		Interpreter inter = getInterpreter();
		Link child = inter.getImplementationLink(l, arch);
		assertEquals(l, child);
		assertEquals(l, child.getParent());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetImplementationResourceExc() {
		Resource res = new Resource("res");
		Interpreter inter = getInterpreter();
		inter.getImplementationResource(res, new Architecture<Resource, Link>());
	}

	@Test
	public void testGetImplementationResource() {
		Architecture<Resource, Link> arch = new Architecture<Resource, Link>();
		Resource res = new Resource("res");
		arch.addVertex(res);
		Interpreter inter = getInterpreter();
		Resource child = inter.getImplementationResource(res, arch);
		assertEquals(res, child);
		assertEquals(res, child.getParent());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDecodeApplicationExc() {
		Task t1 = new Task("t1");
		Task t2 = new Communication("t2");
		Dependency d1 = new Dependency("d1");
		T tVar1 = Variables.varT(t1);
		T tVar2 = Variables.varT(t2);
		DTT dtt1 = Variables.varDTT(d1, t1, t2);
		Set<ApplicationVariable> applicationVariables = new HashSet<ApplicationVariable>();
		applicationVariables.add(tVar1);
		applicationVariables.add(tVar2);
		applicationVariables.add(dtt1);
		Model m = new Model();
		m.set(tVar1, true);
		m.set(tVar2, false);
		m.set(dtt1, true);
		Interpreter inter = getInterpreter();
		inter.decodeApplication(applicationVariables, m, new Application<Task, Dependency>());
	}

	@Test
	public void testDecodeApplication() {
		Task t1 = new Task("t1");
		Task t2 = new Communication("t2");
		Task t3 = new Task("t3");
		Task t4 = new Communication("t4");
		Dependency d1 = new Dependency("d1");
		Dependency d2 = new Dependency("d2");
		T tVar1 = Variables.varT(t1);
		T tVar2 = Variables.varT(t2);
		T tVar3 = Variables.varT(t3);
		T tVar4 = Variables.varT(t4);
		DTT dtt1 = Variables.varDTT(d1, t1, t2);
		DTT dtt2 = Variables.varDTT(d2, t3, t4);
		Set<ApplicationVariable> applicationVariables = new HashSet<ApplicationVariable>();
		applicationVariables.add(tVar1);
		applicationVariables.add(tVar2);
		applicationVariables.add(tVar3);
		applicationVariables.add(tVar4);
		applicationVariables.add(dtt1);
		applicationVariables.add(dtt2);
		Model m = new Model();
		m.set(tVar1, true);
		m.set(tVar2, true);
		m.set(tVar3, false);
		m.set(tVar4, false);
		m.set(dtt1, true);
		m.set(dtt2, false);
		Interpreter inter = getInterpreter();
		Application<Task, Dependency> result = inter.decodeApplication(applicationVariables, m,
				new Application<Task, Dependency>());
		assertTrue(result.getVertex(t1) != null);
		assertTrue(result.getVertex(t2) != null);
		assertTrue(result.getVertex(t3) == null);
		assertTrue(result.getVertex(t4) == null);
		assertTrue(result.getEdge(d1) != null);
		assertTrue(result.getEdge(d2) == null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDecodeApplUnknownVar() {
		ApplicationVariable mockVar = mock(ApplicationVariable.class);
		Set<ApplicationVariable> applVars = new HashSet<ApplicationVariable>();
		applVars.add(mockVar);
		Interpreter inter = getInterpreter();
		inter.decodeApplication(applVars, new Model(), new Application<Task, Dependency>());
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkVariableSettingExc() {
		Interpreter inter = getInterpreter();
		Model model = new Model();
		InterfaceVariable mockVar = mock(InterfaceVariable.class);
		inter.checkVariableSetting(model, mockVar);
	}

	@Test
	public void checkVariableSetting() {
		Interpreter inter = getInterpreter();
		Model model = new Model();
		InterfaceVariable mockVar = mock(InterfaceVariable.class);
		model.set(mockVar, false);
		inter.checkVariableSetting(model, mockVar);
		assertFalse(model.get(mockVar));
	}

	@Test
	public void createImplDependencyTest() {
		Interpreter inter = getInterpreter();
		Application<Task, Dependency> appl = new Application<Task, Dependency>();
		Task t1 = new Task("t1");
		Task t2 = new Task("t2");
		Dependency contained = new Dependency("contained");
		Dependency notContained = new Dependency("not contained");
		appl.addEdge(contained, t1, t2, EdgeType.DIRECTED);
		Dependency d1 = inter.createImplementationDependency(contained, appl);
		Dependency d2 = inter.createImplementationDependency(notContained, appl);
		assertEquals(contained, d1);
		assertEquals(contained, d1.getParent());
		assertEquals(notContained, d2);
		assertNotEquals(notContained, d2.getParent());
	}

	@Test
	public void createImplTaskTest() {
		Interpreter inter = getInterpreter();
		Application<Task, Dependency> appl = new Application<Task, Dependency>();
		Task contained = new Task("t1");
		Task notContained = new Task("t2");
		appl.addVertex(contained);
		Task implT1 = inter.createImplementationTask(contained, appl);
		Task implT2 = inter.createImplementationTask(notContained, appl);
		assertEquals(contained, implT1);
		assertEquals(contained, implT1.getParent());
		assertEquals(notContained, implT2);
		assertNotEquals(notContained, implT2.getParent());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInitUnknownVar() {
		ImplementationEncoding mockEncoding = mock(ImplementationEncoding.class);
		InterfaceVariable interMock = mock(InterfaceVariable.class);
		Set<InterfaceVariable> vars = new HashSet<InterfaceVariable>();
		vars.add(interMock);
		when(mockEncoding.getInterfaceVariables()).thenReturn(vars);
		SpecificationWrapper mockWrapper = mock(SpecificationWrapper.class);
		Interpreter inter = new Interpreter(mockEncoding, mockWrapper);
		inter.initializeInterfaceVariables();
	}

	@Test
	public void testInitVariables() {
		ImplementationEncoding mockEncoding = mock(ImplementationEncoding.class);
		ApplicationVariable applMock = mock(ApplicationVariable.class);
		MappingVariable mapMock = mock(MappingVariable.class);
		RoutingVariable routingMock = mock(RoutingVariable.class);
		AllocationVariable allocationMock = mock(AllocationVariable.class);
		Set<InterfaceVariable> vars = new HashSet<InterfaceVariable>();
		vars.add(applMock);
		vars.add(routingMock);
		vars.add(mapMock);
		vars.add(allocationMock);
		when(mockEncoding.getInterfaceVariables()).thenReturn(vars);
		SpecificationWrapper mockWrapper = mock(SpecificationWrapper.class);
		Interpreter inter = new Interpreter(mockEncoding, mockWrapper);
		assertFalse(inter.variablesInitialized);
		inter.initializeInterfaceVariables();
		verify(mockEncoding).getInterfaceVariables();
		assertEquals(1, inter.applicationVariables.size());
		assertEquals(1, inter.routingVariables.size());
		assertEquals(1, inter.mappingVariables.size());
		assertEquals(1, inter.allocationVariables.size());
		assertTrue(inter.applicationVariables.contains(applMock));
		assertTrue(inter.routingVariables.contains(routingMock));
		assertTrue(inter.allocationVariables.contains(allocationMock));
		assertTrue(inter.mappingVariables.contains(mapMock));
		assertTrue(inter.variablesInitialized);
	}

	@Test(expected = RuntimeException.class)
	public void testCopyExc() {
		Task t = new Task("t");
		Resource res = new Resource("r");
		Mapping<Task, Resource> m = new Mapping<Task, Resource>("m", t, res);
		Interpreter inter = getInterpreter();
		inter.copy(m);
	}

	@Test
	public void testCopy() {
		Interpreter inter = getInterpreter();
		Element element = new Element("parent");
		Element child = inter.copy(element);
		Element secondChild = inter.copy(element);
		assertEquals(child, secondChild);
		assertEquals(element, child);
		assertNotEquals(child, element.getParent());
		assertEquals(element, child.getParent());
	}

	@Test
	public void testGetNotEncodedMessage() {
		InterfaceVariable mockVar = mock(InterfaceVariable.class);
		when(mockVar.toString()).thenReturn("mockVar");
		ImplementationEncoding mockEncoding = mock(ImplementationEncoding.class);
		SpecificationWrapper mockWrapper = mock(SpecificationWrapper.class);
		Interpreter inter = new Interpreter(mockEncoding, mockWrapper);
		String expected = "The variable mockVar is not encoded";
		assertEquals(expected, inter.getNotEncodedMessage(mockVar));
	}

}
