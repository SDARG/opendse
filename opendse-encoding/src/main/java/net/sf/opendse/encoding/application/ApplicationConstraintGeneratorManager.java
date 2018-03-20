package net.sf.opendse.encoding.application;

import com.google.inject.ImplementedBy;

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
	 *            the attribute String of the activation mode
	 * @return the {@link ApplicationModeConstraintGenerator} for the given
	 *         activation mode
	 */
	public ApplicationModeConstraintGenerator getConstraintGenerator(String activationMode);

}
