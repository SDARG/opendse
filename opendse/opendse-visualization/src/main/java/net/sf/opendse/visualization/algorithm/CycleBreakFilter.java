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
