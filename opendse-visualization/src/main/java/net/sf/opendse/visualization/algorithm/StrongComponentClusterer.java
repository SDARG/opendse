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
 * components using Tarjan's algorithms. See: Robert Tarjan: Depth-first search
 * and linear graph algorithms. In: SIAM Journal on Computing. Vol. 1 (1972),
 * No. 2, P. 146-160.
 * 
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
