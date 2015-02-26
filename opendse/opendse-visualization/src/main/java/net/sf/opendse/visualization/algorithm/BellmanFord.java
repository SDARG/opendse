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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ConstantFactory;
import org.apache.commons.collections15.functors.MapTransformer;
import org.apache.commons.collections15.map.LazyMap;

import edu.uci.ics.jung.graph.Graph;

/**
 * The {@code BellmanFord} algorithm.
 * 
 * @author lukasiewycz
 * 
 * @param <V>
 *            the type of vertices
 * @param <E>
 *            the type of edges
 */
public class BellmanFord<V, E> implements Transformer<Graph<V, E>, Transformer<V, Double>> {

	protected final Transformer<V, Double> values;

	/**
	 * Constructs the algorithm with node value of 1.
	 */
	public BellmanFord() {
		this(new Transformer<V, Double>() {
			@Override
			public Double transform(V v) {
				return 1d;
			}
		});
	}

	/**
	 * Constructs the algorithm with individual node values.
	 * 
	 * @param values
	 *            the values
	 */
	public BellmanFord(Transformer<V, Double> values) {
		this.values = values;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.commons.collections15.Transformer#transform(java.lang.Object)
	 */
	@Override
	public Transformer<V, Double> transform(Graph<V, E> graph) {

		Set<V> visited = new HashSet<V>();
		Map<V, Double> result = LazyMap.decorate(new HashMap<V, Double>(), new ConstantFactory<Double>(0d));

		for (V vertex : graph.getVertices()) {
			if (graph.getPredecessorCount(vertex) == 0) {
				visited.add(vertex);
			}
		}

		while (!visited.isEmpty()) {
			Set<V> next = new HashSet<V>();
			for (V vertex : visited) {
				for (V successor : graph.getSuccessors(vertex)) {
					double value = result.get(vertex) + values.transform(vertex);
					if (result.get(successor) < value) {
						result.put(successor, value);
						next.add(successor);
					}
				}
			}
			visited = next;
		}

		return MapTransformer.getInstance(result);
	}

}
