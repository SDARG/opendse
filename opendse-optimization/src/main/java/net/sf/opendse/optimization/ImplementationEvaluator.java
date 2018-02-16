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
