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
 * The {@code Link} is the basic edge element in {@link Architecture} graphs.
 * 
 * @author lukasiewycz
 * 
 */
public class Link extends Edge {

	/**
	 * Constructs a new link.
	 * 
	 * @param id
	 *            the id
	 */
	public Link(String id) {
		super(id);
	}

	/**
	 * Constructs a new link.
	 * 
	 * @param parent
	 *            the parent
	 */
	public Link(Element parent) {
		super(parent);
	}

}
