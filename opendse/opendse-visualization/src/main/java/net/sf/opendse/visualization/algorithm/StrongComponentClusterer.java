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

import static java.lang.Math.min;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.ArrayStack;
import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.graph.Graph;

/**
 * The {@code StrongComponentClusterer} clusters a graph into strongly connected
 * components using Tarjan's algorithms.
 * 
 * @see Robert Tarjan: Depth-first search and linear graph algorithms. In: SIAM
 *      Journal on Computing. Vol. 1 (1972), No. 2, P. 146-160.
 * @author lukasiewycz
 * 
 * @param <V>
 *            the type of vertices
 * @param <E>
 *            the type of edges
 */
public class StrongComponentClusterer<V, E> implements Transformer<Graph<V, E>, Set<Set<V>>> {
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.commons.collections15.Transformer#transform(java.lang.Object)
	 */
	@Override
	public Set<Set<V>> transform(Graph<V, E> graph) {
		Algorithm algorithm = new Algorithm();
		return algorithm.transform(graph);
	}
	

	private class Algorithm implements Transformer<Graph<V, E>, Set<Set<V>>> {

		int index = 0;

		ArrayStack<V> stack = new ArrayStack<V>();
		Map<V, Integer> indexMap = new HashMap<V, Integer>();
		Map<V, Integer> lowlinkMap = new HashMap<V, Integer>();
		Graph<V, E> graph;
		Set<Set<V>> scc = new HashSet<Set<V>>();

		@Override
		public Set<Set<V>> transform(Graph<V, E> graph) {
			this.graph = graph;
			for (V v : graph.getVertices()) {
				if (!indexMap.containsKey(v)) {
					go(v);
				}
			}			
			return scc;
		}

		public void go(V v) {
			indexMap.put(v, index);
			lowlinkMap.put(v, index);
			index++;
			stack.push(v);

			for (V vs : graph.getSuccessors(v)) {
				if (!indexMap.containsKey(vs)) {
					go(vs);
					lowlinkMap.put(v, min(lowlinkMap.get(v), lowlinkMap.get(vs)));
				} else if (stack.contains(vs)) {
					lowlinkMap.put(v, min(lowlinkMap.get(v), indexMap.get(vs)));
				}
			}
			
			final int lowlink = lowlinkMap.get(v);
			final int index = indexMap.get(v);
			
			if (lowlink == index) {				
				Set<V> component = new HashSet<V>();
				V vs = null;
				do {
					vs = stack.pop();
					component.add(vs);
				} while (!vs.equals(v));
				scc.add(component);
			}
		}
	}

}
