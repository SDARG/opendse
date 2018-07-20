package net.sf.opendse.optimization.validation;

import javax.inject.Inject;

import net.sf.opendse.model.Specification;
import net.sf.opendse.model.SpecificationWrapper;
import net.sf.opendse.model.ValidSpecificationPredicate;

/**
 * The {@link SpecificationValidator} fails fast if the {@link Specification} is
 * infeasible.
 * 
 * It uses the {@link ValidSpecificationPredicate} and should lead to more
 * helpful errors than later constraint exceptions of the SAT solver.
 * 
 * @author Felix Reimann
 *
 */
public class SpecificationValidator {
	/**
	 * Creates a new {@link SpecificationValidator}.
	 * 
	 * @param specificationWrapper
	 *            the specification to test
	 * @param validSpecificationPredicate
	 *            the specification predicate to use
	 */
	@Inject
	public SpecificationValidator(SpecificationWrapper specificationWrapper,
			ValidSpecificationPredicate validSpecificationPredicate) {
		if (!validSpecificationPredicate.evaluate(specificationWrapper.getSpecification())) {
			throw new IllegalArgumentException("Invalid specification");
		}
	}
}
