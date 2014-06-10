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
