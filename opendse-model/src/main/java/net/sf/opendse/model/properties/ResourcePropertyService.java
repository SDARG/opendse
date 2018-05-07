package net.sf.opendse.model.properties;

import net.sf.opendse.model.Attributes;
import net.sf.opendse.model.Resource;

/**
 * The {@link ResourcePropertyService} offers static methods for a convenient
 * access to the {@link Attributes} of {@link Resource}s.
 * 
 * @author Fedor Smirnov
 *
 */
public class ResourcePropertyService extends AbstractPropertyService {

	public enum ResourceAttributes {
		PROXY_RESOURCE("proxy resource id");
		protected String xmlName;

		private ResourceAttributes(String xmlName) {
			this.xmlName = xmlName;
		}
	}

	private ResourcePropertyService() {
	}

	/**
	 * Returns the id of the {@link Resource} that is the communication proxy for
	 * the given resource. If the proxy parameter is not set, the resource is its
	 * own proxy.
	 * 
	 * @param resource
	 *            the given {@link Resource}
	 * @return the id String of the proxy of the given resource
	 */
	public static String getProxyId(Resource resource) {
		if (!isAttributeSet(resource, ResourceAttributes.PROXY_RESOURCE.xmlName)) {
			return resource.getId();
		} else {
			return (String) getAttribute(resource, ResourceAttributes.PROXY_RESOURCE.xmlName);
		}
	}

	/**
	 * Sets the proxy id attribute of the given resource.
	 * 
	 * @param resource
	 *            the given resource
	 * @param proxy
	 *            the proxy of the given resource
	 */
	public static void setProxyId(Resource resource, Resource proxy) {
		resource.setAttribute(ResourceAttributes.PROXY_RESOURCE.xmlName, proxy.getId());
	}

}
