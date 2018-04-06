package net.sf.opendse.encoding.application;

import com.google.inject.ImplementedBy;

import net.sf.opendse.model.properties.ApplicationElementPropertyService.ActivationModes;

/**
 * provides the {@link ApplicationModeConstraintGenerator}s for the different
 * activation modes
 * 
 * @author Fedor Smirnov
 *
 */
@ImplementedBy(DefaultConstraintGeneratorManager.class)
public interface ApplicationConstraintGeneratorManager {

	/**
	 * provides the {@link ApplicationModeConstraintGenerator}s for the different
	 * activation modes
	 * 
	 * @param activationMode
	 *            the {@link ActivationModes} of the application elements in
	 *            question
	 * @return the {@link ApplicationModeConstraintGenerator} for the given
	 *         activation mode
	 */
	public ApplicationModeConstraintGenerator getConstraintGenerator(ActivationModes activationMode);

}
