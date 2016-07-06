package net.sf.opendse.optimization.validation;

import net.sf.opendse.optimization.io.IOModule;

import org.opt4j.core.config.Icons;
import org.opt4j.core.config.annotations.Icon;
import org.opt4j.core.config.annotations.Info;

/**
 * The {@link SpecificationValidatorModule} binds the
 * {@link SpecificationValidator}.
 * 
 * @author Felix Reimann
 *
 */
@Info("Fails fast if the specification is infeasible.")
@Icon(Icons.PROBLEM)
public class SpecificationValidatorModule extends IOModule {

	@Override
	protected void config() {
	}
}
