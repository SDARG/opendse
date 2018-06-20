package net.sf.opendse.encoding.preprocessing;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.uci.ics.jung.graph.util.EdgeType;
import net.sf.opendse.model.Application;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.properties.ArchitectureElementPropertyService;
import net.sf.opendse.model.properties.ResourcePropertyService;

public class ProxySearchTest {

	@Test
	public void test() {
		Resource r0 = new Resource("r0");
		Resource r1 = new Resource("r1");
		Resource r2 = new Resource("r2");
		Resource r3 = new Resource("r3");
		Resource r4 = new Resource("r4");
		Resource r5 = new Resource("r5");
		Resource r6 = new Resource("r6");
		
		Link l0 = new Link("l0");
		Link l1 = new Link("l1");
		Link l2 = new Link("l2");
		Link l3 = new Link("l3");
		Link l4 = new Link("l4");
		Link l5 = new Link("l5");
		Link l6 = new Link("l6");
		
		Architecture<Resource, Link> arch = new Architecture<Resource, Link>();
		arch.addEdge(l0, r0, r2, EdgeType.UNDIRECTED);
		arch.addEdge(l1, r1, r2, EdgeType.UNDIRECTED);
		arch.addEdge(l2, r2, r3, EdgeType.UNDIRECTED);
		arch.addEdge(l3, r3, r4, EdgeType.UNDIRECTED);
		arch.addEdge(l4, r3, r5, EdgeType.UNDIRECTED);
		arch.addEdge(l5, r4, r6, EdgeType.UNDIRECTED);
		arch.addEdge(l6, r5, r6, EdgeType.UNDIRECTED);
		
		Specification spec = new Specification(new Application<Task, Dependency>(), arch, new Mappings<Task, Resource>());
		ProxySearch search = new ProxySearch();
		search.preprocessSpecification(spec);
		Specification processedSpec = spec; 
		Architecture<Resource, Link> preprocessedArch = processedSpec.getArchitecture();
		
		assertFalse(ArchitectureElementPropertyService.getOffersRoutingVariety(preprocessedArch.getEdge(l0)));
		assertFalse(ArchitectureElementPropertyService.getOffersRoutingVariety(preprocessedArch.getEdge(l1)));
		assertFalse(ArchitectureElementPropertyService.getOffersRoutingVariety(preprocessedArch.getEdge(l2)));
		assertTrue(ArchitectureElementPropertyService.getOffersRoutingVariety(preprocessedArch.getEdge(l3)));
		assertTrue(ArchitectureElementPropertyService.getOffersRoutingVariety(preprocessedArch.getEdge(l4)));
		assertTrue(ArchitectureElementPropertyService.getOffersRoutingVariety(preprocessedArch.getEdge(l5)));
		assertTrue(ArchitectureElementPropertyService.getOffersRoutingVariety(preprocessedArch.getEdge(l6)));
		
		assertTrue(ResourcePropertyService.getProxyId(preprocessedArch.getVertex(r0)).equals(r3.getId()));
		assertTrue(ResourcePropertyService.getProxyId(preprocessedArch.getVertex(r1)).equals(r3.getId()));
		assertTrue(ResourcePropertyService.getProxyId(preprocessedArch.getVertex(r2)).equals(r3.getId()));
		assertTrue(ResourcePropertyService.getProxyId(preprocessedArch.getVertex(r3)).equals(r3.getId()));
		assertTrue(ResourcePropertyService.getProxyId(preprocessedArch.getVertex(r4)).equals(r4.getId()));
		assertTrue(ResourcePropertyService.getProxyId(preprocessedArch.getVertex(r5)).equals(r5.getId()));
		assertTrue(ResourcePropertyService.getProxyId(preprocessedArch.getVertex(r6)).equals(r6.getId()));
		
		assertTrue(ArchitectureElementPropertyService.getOuterElementId(l2).equals(r2.getId()));
		assertTrue(ArchitectureElementPropertyService.getOuterElementId(l0).equals(r0.getId()));
		assertTrue(ArchitectureElementPropertyService.getOuterElementId(l1).equals(r1.getId()));
	}
}
