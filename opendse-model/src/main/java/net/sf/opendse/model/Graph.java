/*******************************************************************************
 * Copyright (c) 2015 OpenDSE
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package net.sf.opendse.model;

import java.util.Collection;
import java.util.Iterator;

import edu.uci.ics.jung.graph.SparseMultigraph;

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
public class Graph<V extends Node, E extends Edge> extends SparseMultigraph<V, E> implements Iterable<V> {

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
	 * @param v
	 *            the vertex
	 * 
	 * @return the vertex
	 */
	public V getVertex(V v) {
		return getVertex(v.getId());
	}

	/**
	 * Return the edge.
	 * 
	 * @param e
	 *            the edge
	 * 
	 * @return the edge
	 */
	public E getEdge(E e) {
		return getEdge(e.getId());
	}

	/**
	 * Return the vertex with the specified id or {@code null} if no vertex with the given {@code id} exists.
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
	 * Return the edge with the specified id or {@code null} if no edge with the given {@code id} exists.
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
	 * Removes the vertices and returns {@code true} if at least one vertex was removed.
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
