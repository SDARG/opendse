package net.sf.opendse.encoding.preprocessing;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Test;

import edu.uci.ics.jung.graph.util.EdgeType;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Models.DirectedLink;
import net.sf.opendse.model.properties.ResourcePropertyService;

public class ProxyRoutingsShortestPathTest {

	@Test
	public void test() {
		
		Architecture<Resource, Link> arch = new Architecture<Resource, Link>();
		Resource r0 = new Resource("r0");
		Resource r1 = new Resource("r1");
		Resource r2 = new Resource("r2");
		Resource r3 = new Resource("r3");
		Resource r4 = new Resource("r4");
		Resource proxy = new Resource("proxy");
		
		Link l0 = new Link("l0");
		Link l1 = new Link("l1");
		Link l2 = new Link("l2");
		Link l3 = new Link("l3");
		Link l4 = new Link("l4");
		
		arch.addEdge(l0, r0, r2, EdgeType.UNDIRECTED);
		arch.addEdge(l1, r1, r2, EdgeType.UNDIRECTED);
		arch.addEdge(l2, r3, r2, EdgeType.UNDIRECTED);
		arch.addEdge(l3, r3, proxy, EdgeType.UNDIRECTED);
		arch.addEdge(l4, proxy, r4, EdgeType.UNDIRECTED);
		
		ResourcePropertyService.setProxyId(r0, proxy);
		ResourcePropertyService.setProxyId(r1, proxy);
		ResourcePropertyService.setProxyId(r2, proxy);
		ResourcePropertyService.setProxyId(r3, proxy);
		
		DirectedLink dirLink0_2 = new DirectedLink(l0, r0, r2);
		DirectedLink dirLink2_0 = new DirectedLink(l0, r2, r0);
		DirectedLink dirLink2_1 = new DirectedLink(l1, r2, r1);
		DirectedLink dirLink2_3 = new DirectedLink(l2, r2, r3);
		DirectedLink dirLink3_2 = new DirectedLink(l2, r3, r2);
		DirectedLink dirLink3_pr = new DirectedLink(l3, r3, proxy);
		DirectedLink dirLinkPr_3 = new DirectedLink(l3, proxy, r3);
		DirectedLink dirLinkPr_4 = new DirectedLink(l4, proxy, r4);
		DirectedLink dirLink4_pr = new DirectedLink(l4, r4, proxy);
		
		ProxyRoutingsShortestPath proxyRoutings = new ProxyRoutingsShortestPath(arch);
		
		Set<DirectedLink> r0_2_proxy = proxyRoutings.getResourceToProxyLinks(r0);
		assertEquals(3, r0_2_proxy.size());
		assertTrue(r0_2_proxy.contains(dirLink0_2));
		assertTrue(r0_2_proxy.contains(dirLink2_3));
		assertTrue(r0_2_proxy.contains(dirLink3_pr));
		
		Set<DirectedLink> proxy_2_r1 = proxyRoutings.getProxyToResourceLinks(r1);
		assertEquals(3, proxy_2_r1.size());
		assertTrue(proxy_2_r1.contains(dirLinkPr_3));
		assertTrue(proxy_2_r1.contains(dirLink3_2));
		assertTrue(proxy_2_r1.contains(dirLink2_1));
		
		Set<DirectedLink> r0_2_r1 = proxyRoutings.getLinksBetweenResources(r0, r1);
		assertEquals(2, r0_2_r1.size());
		assertTrue(r0_2_r1.contains(dirLink0_2));
		assertTrue(r0_2_r1.contains(dirLink2_1));
		
		Set<DirectedLink> r0_2_r0 = proxyRoutings.getLinksBetweenResources(r0, r0);
		assertTrue(r0_2_r0.isEmpty());
		
		Set<DirectedLink> r0_2_r4 = proxyRoutings.getLinksBetweenResources(r0, r4);
		assertTrue(r0_2_r4.isEmpty());
		
		Set<DirectedLink> r4_2_pr = proxyRoutings.getResourceToProxyLinks(r4);
		assertTrue(r4_2_pr.isEmpty());
		
		Set<DirectedLink> pr_2_r4 = proxyRoutings.getProxyToResourceLinks(r4);
		assertTrue(pr_2_r4.isEmpty());
		
		assertTrue(proxyRoutings.getRelevantSourceResources(dirLinkPr_4).isEmpty());
		assertTrue(proxyRoutings.getRelevantSourceResources(dirLink4_pr).isEmpty());
		assertTrue(proxyRoutings.getRelevantDestinationResources(dirLinkPr_4).isEmpty());
		assertTrue(proxyRoutings.getRelevantDestinationResources(dirLink4_pr).isEmpty());
		
		Set<Resource> relSourceResourcesLink_r0_r2 = proxyRoutings.getRelevantSourceResources(dirLink0_2);
		assertEquals(1, relSourceResourcesLink_r0_r2.size());
		Set<Resource> relDestResourcesLink_r0_r2 = proxyRoutings.getRelevantDestinationResources(dirLink0_2);
		assertEquals(0, relDestResourcesLink_r0_r2.size());
		
		Set<Resource> relSourceResourcesLink_r2_r0 = proxyRoutings.getRelevantSourceResources(dirLink2_0);
		assertEquals(0, relSourceResourcesLink_r2_r0.size());
		Set<Resource> relDestResourcesLink_r2_r0 = proxyRoutings.getRelevantDestinationResources(dirLink2_0);
		assertEquals(1, relDestResourcesLink_r2_r0.size());
		
		Set<Resource> relSourceResourcesLink_r2_r3 = proxyRoutings.getRelevantSourceResources(dirLink2_3);
		assertEquals(3, relSourceResourcesLink_r2_r3.size());
		assertTrue(relSourceResourcesLink_r2_r3.contains(r0));
		assertTrue(relSourceResourcesLink_r2_r3.contains(r1));
		assertTrue(relSourceResourcesLink_r2_r3.contains(r2));
		Set<Resource> relDestResourcesLink_r2_r3 = proxyRoutings.getRelevantDestinationResources(dirLink2_3);
		assertEquals(0, relDestResourcesLink_r2_r3.size());
		
		Set<Resource> relSourceResourcesLink_r3_r2 = proxyRoutings.getRelevantSourceResources(dirLink3_2);
		assertEquals(0, relSourceResourcesLink_r3_r2.size());
		Set<Resource> relDestResourcesLink_r3_r2 = proxyRoutings.getRelevantDestinationResources(dirLink3_2);
		assertEquals(3, relDestResourcesLink_r3_r2.size());
		assertTrue(relDestResourcesLink_r3_r2.contains(r0));
		assertTrue(relDestResourcesLink_r3_r2.contains(r1));
		assertTrue(relDestResourcesLink_r3_r2.contains(r2));
	}
}
