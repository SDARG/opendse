package net.sf.opendse.model.properties;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sf.opendse.model.Resource;

public class ResourcePropertyServiceTest {

	@Test
	public void test() {
		String idStringRes = "res";
		Resource res = new Resource(idStringRes);
		assertEquals(idStringRes, ResourcePropertyService.getProxyId(res));
		String proxyId = "proxy";
		Resource proxy = new Resource(proxyId);
		ResourcePropertyService.setProxyId(res, proxy);
		assertEquals(proxyId, ResourcePropertyService.getProxyId(res));
	}

}
