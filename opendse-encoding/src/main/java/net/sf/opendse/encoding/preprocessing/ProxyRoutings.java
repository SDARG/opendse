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
	 * Returns the set of {@link DirectedLink}s that form the path leading from the
	 * given {@link Resource} to its proxy.
	 * 
	 * @param resource
	 *            the {@link Resource} that is actual source of the routing
	 * @return the set of {@link DirectedLink}s that form the path leading from the
	 *         given {@link Resource} to its proxy
	 */
	public Set<DirectedLink> getResourceToProxyLinks(Resource resource);

	/**
	 * Returns the set of {@link DirectedLink}s that form the path leading from the
	 * proxy to the given {@link Resource}.
	 * 
	 * @param resource
	 *            the {@link Resource} that is the actual destination of the routing
	 * @return the set of {@link DirectedLink}s that form the path leading from the
	 *         proxy to the given {@link Resource}
	 */
	public Set<DirectedLink> getProxyToResourceLinks(Resource resource);

	/**
	 * Returns the set of {@link DirectedLink}s that form the path connecting two
	 * actual {@link Resource}s within the same proxy area.
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
	 * link.
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
