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
package net.sf.opendse.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.functors.InstantiateFactory;
import org.apache.commons.collections15.map.LazyMap;

/**
 * The {@code Routings} maps {@link Task} elements to a subgraph of the
 * {@code Architecture} consisting of {@link Resource} vertices and {@link Edge}
 * edges.
 * 
 * @author Martin Lukasiewycz
 * 
 * @param <T>
 *            the type of task
 * @param <R>
 *            the type of resource
 * @param <L>
 *            the type of edge (in the architecture)
 */
public class Routings<T extends Task, R extends Resource, L extends Link> {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Map<T, Architecture<R, L>> map = LazyMap.decorate(new HashMap<T, Architecture<R, L>>(),
			new InstantiateFactory(Architecture.class));

	/**
	 * Sets the routing for a task.
	 * 
	 * @param task
	 *            the task
	 * @param routing
	 *            the routing
	 */
	public void set(T task, Architecture<R, L> routing) {
		map.put(task, routing);
	}

	/**
	 * Gets a routing for a task
	 * 
	 * @param task
	 *            the task
	 * @return the routing
	 */
	public Architecture<R, L> get(T task) {
		return map.get(task);
	}

	/**
	 * Removes the routing.
	 * 
	 * @param task
	 *            the communication task
	 * @return the routing
	 */
	public Architecture<R, L> remove(T task) {
		return map.remove(task);
	}

	/**
	 * Returns all tasks that have a routing.
	 * 
	 * @return all tasks
	 */
	public Set<T> getTasks() {
		return map.keySet();
	}
	
	/**
	 * Returns all routings.
	 * 
	 * @return all routings
	 */
	public Collection<Architecture<R, L>> getRoutings(){
		return map.values();
	}

}
