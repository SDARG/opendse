/*******************************************************************************
 * Copyright (c) 2015 OpenDSE
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
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
