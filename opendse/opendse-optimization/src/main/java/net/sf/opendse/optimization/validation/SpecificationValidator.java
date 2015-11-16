package net.sf.opendse.optimization.validation;

import javax.inject.Inject;

import net.sf.opendse.model.ValidSpecificationPredicate;
import net.sf.opendse.optimization.SpecificationWrapper;

public class SpecificationValidator {
	@Inject
	public SpecificationValidator(SpecificationWrapper specificationWrapper,
			ValidSpecificationPredicate validSpecificationPredicate) {
		if (!validSpecificationPredicate.evaluate(specificationWrapper.getSpecification())) {
			throw new IllegalArgumentException("Invalid specification");
		}
	}
}
