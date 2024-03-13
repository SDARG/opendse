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

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.map.LazyMap;

import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;

public class DistanceFlowLayout<V, E> implements Layout<V, E> {

	protected Graph<V, E> graph;

	private int maxX = 0;
	private int maxY = 0;

	private int ox = 100;
	private int oy = 50;

	protected Map<V, Point2D> locations = LazyMap.decorate(new HashMap<V, Point2D>(), new Transformer<V, Point2D>() {
		public Point2D transform(V vertex) {
			return new Point2D.Double();
		}
	});

	public DistanceFlowLayout(Graph<V, E> graph) {
		this.graph = graph;
		doLayout();
	}

	@Override
	public Graph<V, E> getGraph() {
		return graph;
	}

	@Override
	public Dimension getSize() {
		return new Dimension(maxX + ox / 2, maxY + oy);
	}

	@Override
	public void initialize() {

	}

	@Override
	public boolean isLocked(V arg0) {
		return false;
	}

	@Override
	public void lock(V arg0, boolean arg1) {
	}

	@Override
	public void reset() {
	}

	@Override
	public void setGraph(Graph<V, E> arg0) {
		doLayout();
	}

	@Override
	public void setInitializer(Transformer<V, Point2D> arg0) {
	}

	@Override
	public void setLocation(V vertex, Point2D location) {
		locations.get(vertex).setLocation(location);
	}

	@Override
	public void setSize(Dimension arg0) {
	}

	@Override
	public Point2D transform(V vertex) {
		return locations.get(vertex);
	}

	protected void doLayout() {
		WeakComponentClusterer<V, E> clusterer = new WeakComponentClusterer<V, E>();
		Set<Set<V>> sets = clusterer.transform(graph);

		Set<DirectedGraph<V, E>> graphs = new HashSet<DirectedGraph<V, E>>();
		for (Set<V> set : sets) {
			DirectedGraph<V, E> g = new DirectedSparseGraph<V, E>();

			for (V v : set) {
				g.addVertex(v);
			}
			for (V v : set) {
				for (E e : graph.getOutEdges(v)) {
					if (graph.getSource(e) != null && graph.getSource(e).equals(v)) {
						V target = graph.getOpposite(v, e);
						if (g.findEdge(v, target) == null) {
							g.addEdge(e, v, target);
						}
					}
				}
			}
			graphs.add(g);
		}

		int xOffset = 0;

		for (DirectedGraph<V, E> graph : graphs) {
			CycleBreakFilter<V, E> cycleBreak = new CycleBreakFilter<V, E>();

			Set<E> edges = cycleBreak.transform(graph);
			for (E edge : edges) {
				graph.removeEdge(edge);
			}

			Map<Integer, List<V>> level = new HashMap<Integer, List<V>>();

			BellmanFord<V, E> bellmanFord = new BellmanFord<V, E>();
			Transformer<V, Double> t = bellmanFord.transform(graph);

			for (V vertex : graph.getVertices()) {
				int l = t.transform(vertex).intValue();
				if (level.containsKey(l)) {
					level.get(l).add(vertex);
				} else {
					List<V> set = new ArrayList<V>();
					set.add(vertex);
					level.put(l, set);
				}
			}

			int maxVerticesPerLevel = 0;
			for (List<V> set : level.values()) {
				maxVerticesPerLevel = Math.max(maxVerticesPerLevel, set.size());
			}

			for (int i = 0;; i++) {
				if (!level.containsKey(i)) {
					break;
				} else {
					List<V> vertices = level.get(i);
					if (i > 0) {
						List<V> previous = level.get(i - 1);
						Comparator<V> comp = new DependencyComparator(graph, previous);
						Collections.sort(vertices, comp);
					}

					int offset = (ox / 2) * (maxVerticesPerLevel - vertices.size() - 1);
					int x = offset;
					for (V vertex : vertices) {
						Point2D location = new Point2D.Double(x + xOffset + ox / 2, i * oy + oy / 2);
						setLocation(vertex, location);
						x += ox;
					}
				}
			}

			for (V vertex : graph.getVertices()) {
				xOffset = Math.max(xOffset, (int) transform(vertex).getX());
			}

			xOffset += ox;
		}

		for (V v : graph.getVertices()) {
			Point2D location = transform(v);
			maxX = Math.max(maxX, (int) location.getX());
			maxY = Math.max(maxY, (int) location.getY());
		}
	}

	class DependencyComparator implements Comparator<V> {

		final Graph<V, E> graph;
		final List<V> previous;

		public DependencyComparator(Graph<V, E> graph, List<V> previous) {
			super();
			this.graph = graph;
			this.previous = previous;
		}

		@Override
		public int compare(V v1, V v2) {

			Double i1 = 0d;
			Double i2 = 0d;
			int c1 = 0;
			int c2 = 0;

			for (int i = 0; i < previous.size(); i++) {
				V v = previous.get(i);
				if (graph.isNeighbor(v, v1)) {
					i1 += i;
					c1++;
				}
				if (graph.isNeighbor(v, v2)) {
					i2 += i;
					c2++;
				}
			}

			if (c1 == 0 || c2 == 0) {
				return 0;
			} else {
				i1 /= c1;
				i2 /= c2;
				return i1.compareTo(i2);
			}

		}

	}

}