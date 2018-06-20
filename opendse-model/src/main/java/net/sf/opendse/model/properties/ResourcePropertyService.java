package net.sf.opendse.model.properties;

import java.util.HashSet;
import java.util.Set;

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
		PROXY_RESOURCE("proxy resource id"), PROXY_DISTANCE("proxy distance"), LOWER_RESOURCES(
				"lower proxied resources");
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

	/**
	 * Returns {@code true} if the given resource has a proxy.
	 * 
	 * @param resource
	 *            The given resource
	 * @return {@code true} if the given resource has a proxy
	 */
	public static boolean hasProxy(Resource resource) {
		return !getProxyId(resource).equals(resource.getId());
	}

	/**
	 * Sets the proxy distance of the given resource. Can only be used for resources
	 * that have a proxy.
	 * 
	 * @param resource
	 *            the given resource
	 * @param distance
	 *            the proxy distance
	 */
	public static void setProxyDistance(Resource resource, int distance) {
		if (!hasProxy(resource)) {
			throw new IllegalArgumentException("The resource " + resource + " has no proxy.");
		}
		String attrName = ResourceAttributes.PROXY_DISTANCE.xmlName;
		resource.setAttribute(attrName, distance);
	}

	/**
	 * Returns the hop distance to the proxy of the given resource. Returns 0 if the
	 * resource has no proxy.
	 * 
	 * @param resource
	 * @return the hop distance to the proxy of the given resource. Returns 0 if the
	 *         resource has no proxy.
	 */
	public static int getProxyDistance(Resource resource) {
		String attrName = ResourceAttributes.PROXY_DISTANCE.xmlName;
		if (!isAttributeSet(resource, attrName)) {
			return 0;
		} else {
			return (Integer) getAttribute(resource, attrName);
		}
	}

	/**
	 * Returns that resources that are below the given resource in the proxy tree.
	 * Returns an empty set if there are no lower resources.
	 * 
	 * @param resource
	 *            the given resource
	 * @return the set of resources that are below the given resource in the proxy
	 *         tree. Returns an empty set if there are no lower resources.
	 */
	@SuppressWarnings("unchecked")
	public static Set<Resource> getLowerResources(Resource resource) {
		String attrName = ResourceAttributes.LOWER_RESOURCES.xmlName;
		if (!isAttributeSet(resource, attrName)) {
			return new HashSet<Resource>();
		} else {
			return (HashSet<Resource>) getAttribute(resource, attrName);
		}
	}

	/**
	 * Add the given lowerResource to the lower Resources annotated for the given
	 * resource.
	 * 
	 * @param resource
	 *            the given resource (on a higher level in the proxy tree)
	 * @param lowerResource
	 *            (under the resource in the proxy tree)
	 */
	public static void addLowerResource(Resource resource, Resource lowerResource) {
		if (!hasProxy(lowerResource)) {
			throw new IllegalArgumentException("The resource " + lowerResource + " has no proxy.");
		}
		String attrName = ResourceAttributes.LOWER_RESOURCES.xmlName;
		Set<Resource> lowerResources = getLowerResources(resource);
		lowerResources.add(lowerResource);
		resource.setAttribute(attrName, lowerResources);
	}
}
