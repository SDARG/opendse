package net.sf.opendse.encoding.preprocessing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Models;
import net.sf.opendse.model.Models.DirectedLink;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.properties.ArchitectureElementPropertyService;
import net.sf.opendse.model.properties.ResourcePropertyService;

public class ProxyRoutingsShortestPath implements ProxyRoutings {

	class Connection {
		protected Resource src;
		protected Resource dest;

		public Connection(Resource src, Resource dest) {
			this.src = src;
			this.dest = dest;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((dest == null) ? 0 : dest.hashCode());
			result = prime * result + ((src == null) ? 0 : src.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Connection other = (Connection) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (dest == null) {
				if (other.dest != null)
					return false;
			} else if (!dest.equals(other.dest))
				return false;
			if (src == null) {
				if (other.src != null)
					return false;
			} else if (!src.equals(other.src))
				return false;
			return true;
		}

		private ProxyRoutingsShortestPath getOuterType() {
			return ProxyRoutingsShortestPath.this;
		}
	}

	protected final Map<Resource, Set<DirectedLink>> resource2ProxyMap = new HashMap<Resource, Set<DirectedLink>>();
	protected final Map<Resource, Set<DirectedLink>> proxy2ResourceMap = new HashMap<Resource, Set<DirectedLink>>();
	protected final Map<Connection, Set<DirectedLink>> proxyInternalConnectionMaps = new HashMap<ProxyRoutingsShortestPath.Connection, Set<DirectedLink>>();
	protected final Map<DirectedLink, Set<Resource>> dirLink2RelevantSrcResources = new HashMap<Models.DirectedLink, Set<Resource>>();
	protected final Map<DirectedLink, Set<Resource>> dirLink2RelevantDestResources = new HashMap<Models.DirectedLink, Set<Resource>>();
	protected final Set<DirectedLink> invariantLinks = new HashSet<Models.DirectedLink>();
	protected final Map<String, Set<DirectedLink>> proxyLinkMap = new HashMap<String, Set<DirectedLink>>();

	public ProxyRoutingsShortestPath(Architecture<Resource, Link> architecture) {
		// find all invariant links
		for (Link l : architecture.getEdges()) {
			if(!ArchitectureElementPropertyService.getOffersRoutingVariety(l)) {
				DirectedLink first = new DirectedLink(l, architecture.getEndpoints(l).getFirst(), architecture.getEndpoints(l).getSecond());
				DirectedLink second = new DirectedLink(l, architecture.getEndpoints(l).getSecond(), architecture.getEndpoints(l).getFirst());
				invariantLinks.add(first);
				invariantLinks.add(second);
			}
		}
		
		// find all proxies
		Set<String> proxies = new HashSet<String>();
		for (Resource res : architecture) {
			if (!ResourcePropertyService.getProxyId(res).equals(res.getId())) {
				proxies.add(ResourcePropertyService.getProxyId(res));
			}
		}
		
		for (DirectedLink dl : invariantLinks) {
			Resource first = dl.getSource();
			Resource second = dl.getDest();
			String proxy = null;
			if (!ResourcePropertyService.getProxyId(first).equals(first.getId())) {
				proxy = ResourcePropertyService.getProxyId(first);
			}
			if (!ResourcePropertyService.getProxyId(second).equals(second.getId())) {
				proxy = ResourcePropertyService.getProxyId(second);
			}
			if (proxy != null) {
				if (!proxyLinkMap.containsKey(proxy)) {
					proxyLinkMap.put(proxy, new HashSet<Models.DirectedLink>());
				}
				proxyLinkMap.get(proxy).add(dl);
			}
		}
		
		// group the resources into sets according to their proxies
		Map<Resource, Set<Resource>> proxy2ResourcesMap = new HashMap<Resource, Set<Resource>>();
		for (Resource res : architecture) {
			String proxyId = ResourcePropertyService.getProxyId(res);
			if (!proxyId.equals(res.getId())) {
				Resource proxy = architecture.getVertex(proxyId);
				if (!proxy2ResourcesMap.containsKey(proxy)) {
					proxy2ResourcesMap.put(proxy, new HashSet<Resource>());
				}
				proxy2ResourcesMap.get(proxy).add(res);
			}
		}

		// for each entry, finds 1) the route from proxy to the resource and the other
		// way round
		for (Entry<Resource, Set<Resource>> entry : proxy2ResourcesMap.entrySet()) {
			Resource proxy = entry.getKey();
			Set<Resource> proxyArea = entry.getValue();
			for (Resource res : proxyArea) {
				resource2ProxyMap.put(res, new HashSet<Models.DirectedLink>(
						Models.getLinks(RoutingSearch.findShortestPath(res, proxy, architecture))));
				proxy2ResourceMap.put(res, new HashSet<Models.DirectedLink>(
						Models.getLinks(RoutingSearch.findShortestPath(proxy, res, architecture))));
			}
			for (Resource src : proxyArea) {
				for (Resource dest : proxyArea) {
					if (!src.equals(dest)) {
						Connection connection = new Connection(src, dest);
						proxyInternalConnectionMaps.put(connection, new HashSet<Models.DirectedLink>(
								Models.getLinks(RoutingSearch.findShortestPath(src, dest, architecture))));
					}
				}
			}
		}

		// fill the maps of the resources relevant to the directed links
		for (Entry<Resource, Set<DirectedLink>> srcEntry : resource2ProxyMap.entrySet()) {
			Resource src = srcEntry.getKey();
			Set<DirectedLink> links = srcEntry.getValue();
			for (DirectedLink link : links) {
				if (!dirLink2RelevantSrcResources.containsKey(link)) {
					dirLink2RelevantSrcResources.put(link, new HashSet<Resource>());
				}
				dirLink2RelevantSrcResources.get(link).add(src);
			}
		}

		for (Entry<Resource, Set<DirectedLink>> destEntry : proxy2ResourceMap.entrySet()) {
			Resource dest = destEntry.getKey();
			Set<DirectedLink> links = destEntry.getValue();
			for (DirectedLink link : links) {
				if (!dirLink2RelevantDestResources.containsKey(link)) {
					dirLink2RelevantDestResources.put(link, new HashSet<Resource>());
				}
				dirLink2RelevantDestResources.get(link).add(dest);
			}
		}
	}

	@Override
	public Set<DirectedLink> getResourceToProxyLinks(Resource resource) {
		if (!resource2ProxyMap.containsKey(resource)) {
			return new HashSet<Models.DirectedLink>();
		} else {
			return resource2ProxyMap.get(resource);
		}
	}

	@Override
	public Set<DirectedLink> getProxyToResourceLinks(Resource resource) {
		if (!proxy2ResourceMap.containsKey(resource)) {
			return new HashSet<Models.DirectedLink>();
		} else {
			return proxy2ResourceMap.get(resource);
		}
	}

	@Override
	public Set<DirectedLink> getLinksBetweenResources(Resource src, Resource dest) {
		Connection conn = new Connection(src, dest);
		if (!proxyInternalConnectionMaps.containsKey(conn)) {
			return new HashSet<Models.DirectedLink>();
		} else {
			return proxyInternalConnectionMaps.get(conn);
		}
	}

	@Override
	public Set<Resource> getRelevantSourceResources(DirectedLink directedLink) {
		if (!dirLink2RelevantSrcResources.containsKey(directedLink)) {
			return new HashSet<Resource>();
		} else {
			return dirLink2RelevantSrcResources.get(directedLink);
		}
	}

	@Override
	public Set<Resource> getRelevantDestinationResources(DirectedLink directedLink) {
		if (!dirLink2RelevantDestResources.containsKey(directedLink)) {
			return new HashSet<Resource>();
		} else {
			return dirLink2RelevantDestResources.get(directedLink);
		}
	}

	@Override
	public Set<DirectedLink> getInvariantLinks() {
		return invariantLinks;
	}

	@Override
	public Set<DirectedLink> getProxyLinks(String proxyId) {
		return proxyLinkMap.get(proxyId);
	}

}
