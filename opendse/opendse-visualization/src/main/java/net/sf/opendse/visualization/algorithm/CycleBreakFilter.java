/**
 * OpenDSE is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * OpenDSE is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with OpenDSE. If not, see http://www.gnu.org/licenses/.
 */
package net.sf.opendse.visualization.algorithm;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.filters.FilterUtils;
import edu.uci.ics.jung.graph.DirectedGraph;

public class CycleBreakFilter<V, E> implements Transformer<DirectedGraph<V, E>, Set<E>> {

	StrongComponentClusterer<V, E> clusterer = new StrongComponentClusterer<V, E>();

	@Override
	public Set<E> transform(DirectedGraph<V, E> graph) {
		Set<E> edges = new HashSet<E>();
		Set<Set<V>> components = clusterer.transform(graph);

		for (Set<V> component : components) {
			if (component.size() > 1) {
				DirectedGraph<V, E> g = FilterUtils.createInducedSubgraph(component, graph);
			
				V v = g.getVertices().iterator().next();
				for(E e: g.getIncidentEdges(v)){
					edges.add(e);
					g.removeEdge(e);
				}
				edges.addAll(transform(g));
			}
		}

		return edges;
	}

}
