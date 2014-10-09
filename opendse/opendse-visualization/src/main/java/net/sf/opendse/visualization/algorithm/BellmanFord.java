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
