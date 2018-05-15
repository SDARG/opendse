package net.sf.opendse.encoding.preprocessing;

import java.util.Map;
import edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath;
import edu.uci.ics.jung.graph.util.EdgeType;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;

/**
 * The {@link RoutingSearch} offers static methods that find routings in a given
 * {@link Architecture}.
 * 
 * @author Fedor Smirnov
 *
 */
public class RoutingSearch {

	/**
	 * Finds the shortest path between the two given {@link Resource}s and returns
	 * it as an {@link Architecture}. Throws an {@link IllegalArgumentException} if
	 * there is no path between the given resources.
	 * 
	 * @param source
	 *            the {@link Resource} where the path starts
	 * @param destination
	 *            the {@link Resource} where the path ends
	 * @param architecture
	 *            the {@link Architecture} graph containing all {@link Link}s and
	 *            {@link Resource}s that are available to build the path
	 * @return the {@link Architecture} consisting of the {@link Resource}s and
	 *         {@link Link}s forming the shortest path between source and
	 *         destination
	 */
	public static Architecture<Resource, Link> findShortestPath(Resource source, Resource destination,
			Architecture<Resource, Link> architecture) {
		Architecture<Resource, Link> result = new Architecture<Resource, Link>();

		UnweightedShortestPath<Resource, Link> shortestPath = new UnweightedShortestPath<Resource, Link>(architecture);
		// checks if destination is reachable from source
		if (!shortestPath.getDistanceMap(source).containsKey(destination)) {
			throw new IllegalArgumentException("The node " + destination + " can not be reached from " + source);
		}
		Map<Resource, Link> inLinkMap = shortestPath.getIncomingEdgeMap(source);
		Resource cur = destination;
		while(!cur.equals(source)) {
			Link link = inLinkMap.get(cur);
			Resource predecessor = architecture.getOpposite(cur, link);
			result.addEdge(link, predecessor, cur, EdgeType.DIRECTED);
			cur = predecessor;
		}
		return result;
	}
}
