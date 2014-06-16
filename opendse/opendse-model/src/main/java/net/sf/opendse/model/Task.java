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

/**
 * The {@code Task} is the basic vertex element for {@link Application} graphs.
 * 
 * @author Martin Lukasiewycz
 * 
 */
public class Task extends Node {

	/**
	 * Type of a {@code Task}.
	 * 
	 * @author Martin Lukasiewycz
	 * 
	 */
	public enum Type {
		/**
		 * Functional task.
		 */
		FUNCTION,
		/**
		 * Communication task.
		 */
		COMMUNICATION;
	}

	/**
	 * Constructs a new task.
	 * 
	 * @param id
	 *            the id
	 */
	public Task(String id) {
		super(id);
	}

	/**
	 * Constructs a new parent.
	 * 
	 * @param parent
	 *            the parent
	 */
	public Task(Element parent) {
		super(parent);
	}

}
