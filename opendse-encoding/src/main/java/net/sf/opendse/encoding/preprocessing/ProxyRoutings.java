package net.sf.opendse.encoding.preprocessing;

import java.util.Set;

import net.sf.opendse.model.Models.DirectedLink;
import net.sf.opendse.model.Resource;

/**
 * The {@link ProxyRoutings} contains the paths between the resources and their
 * proxies as well as between the resources sharing the same proxies.
 * 
 * @author Fedor Smirnov
 *
 */
public interface ProxyRoutings {

	/**
	 * Returns a set of all {@link DirectedLink}s that do not offer any routing
	 * variety.
	 * 
	 * @return a set of all {@link DirectedLink}s that do not offer any routing
	 *         variety
	 */
	public Set<DirectedLink> getInvariantLinks();
	
	/**
	 * Returns the set of the links from the proxy area of the given proxy. 
	 * 
	 * @param proxyId the id of the given proxy
	 * @return the set of the links from the proxy area of the given proxy
	 */
	public Set<DirectedLink> getProxyLinks(String proxyId);

	/**
	 * Returns the set of {@link DirectedLink}s that form the path leading from the
	 * given {@link Resource} to its proxy. Returns an empty set if the given
	 * resource has no proxy.
	 * 
	 * @param resource
	 *            the {@link Resource} that is actual source of the routing
	 * @return the set of {@link DirectedLink}s that form the path leading from the
	 *         given {@link Resource} to its proxy
	 */
	public Set<DirectedLink> getResourceToProxyLinks(Resource resource);

	/**
	 * Returns the set of {@link DirectedLink}s that form the path leading from the
	 * proxy to the given {@link Resource}. Returns an empty set if the given
	 * resource has no proxy.
	 * 
	 * @param resource
	 *            the {@link Resource} that is the actual destination of the routing
	 * @return the set of {@link DirectedLink}s that form the path leading from the
	 *         proxy to the given {@link Resource}
	 */
	public Set<DirectedLink> getProxyToResourceLinks(Resource resource);

	/**
	 * Returns the set of {@link DirectedLink}s that form the path connecting two
	 * actual {@link Resource}s within the same proxy area. Returns an empty set if
	 * the given resources are equal or from different proxy areas.
	 * 
	 * @param src
	 *            the actual {@link Resource} that is the source of the route
	 * @param dest
	 *            the actual {@link Resource} that is the destination of the route
	 * @return the set of {@link DirectedLink}s that form the path connecting two
	 *         actual {@link Resource}s within the same proxy area
	 */
	public Set<DirectedLink> getLinksBetweenResources(Resource src, Resource dest);

	/**
	 * Returns the set of {@link Resource}s that are relevant for the activation of
	 * the given {@link DirectedLink}. Relevant means that the mapping of a source
	 * task to one of these resources necessitates the activation of the link.
	 * Returns an empty set if the given link is not from a proxy area.
	 * 
	 * @param directedLink
	 *            the {@link DirectedLink} in question
	 * @return the set of {@link Resource}s that are relevant for the activation of
	 *         the given {@link DirectedLink}. Relevant means that the mapping of a
	 *         source task to one of these resources necessitates the activation of
	 *         the link
	 */
	public Set<Resource> getRelevantSourceResources(DirectedLink directedLink);

	/**
	 * Returns the set of {@link Resource}s that are relevant for the activation of
	 * the given {@link DirectedLink}. Relevant means that the mapping of a
	 * destination task to one of these resources necessitates the activation of the
	 * link. Returns an empty set if the given link is not from a proxy area.
	 * 
	 * @param directedLink
	 *            the {@link DirectedLink} in question
	 * @return the set of {@link Resource}s that are relevant for the activation of
	 *         the given {@link DirectedLink}. Relevant means that the mapping of a
	 *         destination task to one of these resources necessitates the
	 *         activation of the link
	 */
	public Set<Resource> getRelevantDestinationResources(DirectedLink directedLink);

}
