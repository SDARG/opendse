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
 * The {@code Architecture} is the default implementation of the architecture
 * graph.
 * 
 * @author Martin Lukasiewycz
 * 
 * @param <R>
 *            the type of vertices
 * @param <L>
 *            the type of edges
 */
public class Architecture<R extends Resource, L extends Link> extends Graph<R, L> {

	private static final long serialVersionUID = 1L;

}
