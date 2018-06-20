package net.sf.opendse.model.properties;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Test;

import net.sf.opendse.model.Resource;

public class ResourcePropertyServiceTest {

	@Test
	public void test() {
		String idStringRes = "res";
		Resource res = new Resource(idStringRes);
		assertEquals(idStringRes, ResourcePropertyService.getProxyId(res));
		assertFalse(ResourcePropertyService.hasProxy(res));
		Set<Resource> lower = ResourcePropertyService.getLowerResources(res);
		assertTrue(lower.isEmpty());
		assertEquals(0, ResourcePropertyService.getProxyDistance(res));
		String proxyId = "proxy";
		Resource proxy = new Resource(proxyId);
		ResourcePropertyService.setProxyId(res, proxy);
		assertEquals(proxyId, ResourcePropertyService.getProxyId(res));
		assertTrue(ResourcePropertyService.hasProxy(res));
		ResourcePropertyService.setProxyDistance(res, 1);
		assertEquals(1, ResourcePropertyService.getProxyDistance(res));
		lower.add(res);
		ResourcePropertyService.addLowerResource(proxy, res);
		assertTrue(ResourcePropertyService.getLowerResources(proxy).size() == 1);
		assertTrue(ResourcePropertyService.getLowerResources(proxy).contains(res));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testExceptionLower() {
		Resource res = new Resource("res");
		Resource res1 = new Resource("res1");
		ResourcePropertyService.addLowerResource(res, res1);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testException() {
		Resource res = new Resource("res");
		ResourcePropertyService.setProxyDistance(res,1);
	}

}
