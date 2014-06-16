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
package net.sf.opendse.optimization.test.generator;

/**
 * The {@code IdProvider} generates unique ids with a given prefix.
 * 
 * @author lukasiewycz
 * 
 */
public class IdProvider {

	protected final String s;
	protected int i = 0;

	/**
	 * Constructs an {@code IdProvider}.
	 * 
	 * @param prefix
	 *            the prefix
	 */
	public IdProvider(String prefix) {
		this.s = prefix;
	}

	/**
	 * Returns the next id.
	 * 
	 * @return the next id
	 */
	public synchronized String next() {
		String result = s + i;
		i++;
		return result;
	}

}
