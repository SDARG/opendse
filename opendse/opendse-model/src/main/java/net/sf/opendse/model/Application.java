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
package net.sf.opendse.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * The {@code Application} is the default implementation of the application
 * graph.
 * 
 * @author Martin Lukasiewycz
 * 
 * @param <T>
 *            the type of vertices
 * @param <D>
 *            the type of edges
 */
public class Application<T extends Task, D extends Dependency> extends Graph<T, D> {

	private static final long serialVersionUID = 1L;

	protected final Map<String, Attributes> fmap = new HashMap<String, Attributes>();

	public void add(Function<T, D> function) {
		for (T t : function) {
			if (containsVertex(t)) {
				throw new RuntimeException("Application already contains " + t);
			}
		}
		for (D d : function.getEdges()) {
			if (containsEdge(d)) {
				throw new RuntimeException("Application already contains " + d);
			}
		}
		for (T t : function) {
			addVertex(t);
		}
		for (D d : function.getEdges()) {
			addEdge(d, function.getEndpoints(d), function.getEdgeType(d));
		}
		T t = function.iterator().next();
		fmap.put(t.getId(), function.getAttributes());
	}

	@Override
	public boolean removeVertex(T t) {
		if (fmap.containsKey(t.getId())) {
			Attributes attributes = fmap.remove(t.getId());

			if (getNeighborCount(t) > 0) {
				T next = getNeighbors(t).iterator().next();
				fmap.put(next.getId(), attributes);
			}
		}

		return super.removeVertex(t);
	}

	public Function<T, D> getFunction(T task) {
		Set<T> tasks = getReachable(task);
		return buildFunction(tasks);
	}

	public Function<T, D> getFunction(D dependency) {
		return getFunction(this.getSource(dependency));
	}

	public Function<T, D> getFunction(String id) {
		for (Entry<String, Attributes> entry : fmap.entrySet()) {
			if (id.equals(entry.getValue().getAttribute("ID"))) {
				return getFunction(getVertex(entry.getKey()));
			}
		}
		return null;
	}

	public Set<Function<T, D>> getFunctions() {
		WeakComponentClusterer<T, D> wcc = new WeakComponentClusterer<T, D>();
		Set<Set<T>> cluster = wcc.transform(this);

		Set<Function<T, D>> functions = new HashSet<Function<T, D>>();
		for (Set<T> tasks : cluster) {
			Function<T, D> function = buildFunction(tasks);
			functions.add(function);
		}
		return functions;
	}

	protected String nextFunctionId() {
		int i = 0;
		while (true) {
			String id = "func" + i;
			i++;

			boolean exists = false;
			for (Attributes attributes : fmap.values()) {
				if (id.equals(attributes.getAttribute("ID"))) {
					exists = true;
				}
			}
			if (!exists) {
				return id;
			}

		}
	}

	protected Function<T, D> buildFunction(Set<T> tasks) {
		Attributes attributes = getAttributes(tasks);
		
		if (attributes.getAttribute("ID") == null) {
			attributes.setAttribute("ID", nextFunctionId());
		}

		Function<T, D> function = new Function<T, D>(attributes);
		for (T t : tasks) {
			function.addVertex(t);

			Collection<D> outEdges = getOutEdges(t);
			for (D d : outEdges) {
				T dest = getDest(d);
				function.addEdge(d, t, dest);
			}
		}
		return function;
	}

	protected Attributes getAttributes(Set<T> tasks) {
		assert !tasks.isEmpty();
		for (T t : tasks) {
			if (fmap.containsKey(t.getId())) {
				return fmap.get(t.getId());
			}
		}
		Attributes attributes = new Attributes();
		T t = tasks.iterator().next();
		fmap.put(t.getId(), attributes);
		return attributes;
	}

	protected Set<T> getReachable(T task) {
		Set<T> reachable = new HashSet<T>();
		Set<T> current = new HashSet<T>();
		current.add(task);
		while (!current.isEmpty()) {
			reachable.addAll(current);
			Set<T> next = new HashSet<T>();
			for (T t : current) {
				Collection<T> neighbors = new HashSet<T>(getNeighbors(t));
				neighbors.removeAll(reachable);
				next.addAll(neighbors);
			}
			current = next;
		}
		return reachable;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.uci.ics.jung.graph.AbstractGraph#addEdge(java.lang.Object,
	 * java.util.Collection)
	 */
	@Override
	public boolean addEdge(D dependency, Collection<? extends T> vertices) {
		return super.addEdge(dependency, vertices, EdgeType.DIRECTED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.uci.ics.jung.graph.AbstractGraph#addEdge(java.lang.Object,
	 * edu.uci.ics.jung.graph.util.Pair)
	 */
	@Override
	public boolean addEdge(D dependency, Pair<? extends T> endpoints) {
		return super.addEdge(dependency, endpoints, EdgeType.DIRECTED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.uci.ics.jung.graph.AbstractGraph#addEdge(java.lang.Object,
	 * java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean addEdge(D dependency, T v1, T v2) {
		return super.addEdge(dependency, v1, v2, EdgeType.DIRECTED);
	}

}
