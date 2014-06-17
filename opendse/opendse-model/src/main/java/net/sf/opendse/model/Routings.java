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
