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
package net.sf.opendse.optimization;

import net.sf.opendse.model.Specification;

import org.opt4j.core.Objectives;

/**
 * The {@code ImplementationEvaluator} evaluates the implementation.
 * 
 * @see DesignSpaceExplorationEvaluator
 * @author Martin Lukasiewycz
 * 
 */
public interface ImplementationEvaluator {

	/**
	 * Evaluates the objectives and returns a new implementation if the
	 * evaluator changed the implementation.
	 * 
	 * @param implementation
	 *            the implementation
	 * @param objectives
	 *            the objectives
	 * @return a new implementation if the implementation was changes,
	 *         {@code null} otherwise
	 */
	public Specification evaluate(Specification implementation, Objectives objectives);

	/**
	 * Returns the priority of the evaluator.
	 * 
	 * @return the priority of the evaluator
	 */
	public int getPriority();

}
