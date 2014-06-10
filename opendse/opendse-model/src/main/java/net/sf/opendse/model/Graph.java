package net.sf.opendse.model;

import java.util.Collection;
import java.util.Iterator;

import edu.uci.ics.jung.graph.SparseGraph;

/**
 * The {@code Graph} is the default graph implementation.
 * 
 * @author Martin Lukasiewycz
 * 
 * @param <V>
 *            the type of vertices
 * @param <E>
 *            the type of edges
 */
public class Graph<V extends Node, E extends Edge> extends SparseGraph<V, E> implements Iterable<V> {

	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<V> iterator() {
		return this.getVertices().iterator();
	}

	/**
	 * Return the vertex.
	 * 
	 * @return the vertex
	 */
	public V getVertex(V v) {
		return getVertex(v.getId());
	}

	/**
	 * Return the edge.
	 * 
	 * @return the edge
	 */
	public E getEdge(E e) {
		return getEdge(e.getId());
	}

	/**
	 * Return the vertex with the specified id.
	 * 
	 * @param id
	 *            the id
	 * @return the vertex
	 */
	public V getVertex(String id) {
		for (V v : getVertices()) {
			if (v.getId().equals(id)) {
				return v;
			}
		}
		return null;
	}

	/**
	 * Return the edge with the specified id.
	 * 
	 * @param id
	 *            the id
	 * @return the edge
	 */
	public E getEdge(String id) {
		for (E e : getEdges()) {
			if (e.getId().equals(id)) {
				return e;
			}
		}
		return null;
	}

	/**
	 * Removes the vertices and returns {@code true} if at least one vertex was
	 * removed.
	 * 
	 * @param vertices
	 *            the vertices to remove
	 * @return true if at least one vertex was removed
	 */
	public boolean removeVertices(Collection<V> vertices) {
		boolean b = false;
		for (V vertex : vertices) {
			b |= removeVertex(vertex);
		}
		return b;
	}
}
