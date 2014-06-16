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
 * The {@code Resource} is the basic vertex element in {@link Architecture}
 * graphs.
 * 
 * @author Martin Lukasiewycz
 * 
 */
public class Resource extends Node {

	/**
	 * Constructs a new resource.
	 * 
	 * @param id
	 *            the id
	 */
	public Resource(String id) {
		super(id);
	}

	/**
	 * Constructs a new resource.
	 * 
	 * @param parent
	 *            the parent
	 */
	public Resource(Element parent) {
		super(parent);
	}

}
