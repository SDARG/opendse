package net.sf.opendse.optimization.validation;

import org.opt4j.core.problem.ProblemModule;

public class SpecificationValidatorModule extends ProblemModule {

	@Override
	protected void config() {
		bind(SpecificationValidator.class).asEagerSingleton();
	}
}
