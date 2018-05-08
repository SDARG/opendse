package net.sf.opendse.encoding.application;

import com.google.inject.ImplementedBy;

/**
 * provides the {@link ApplicationConstraintGenerator}s for the different
 * activation modes
 * 
 * @author Fedor Smirnov
 *
 */
@ImplementedBy(ApplicationConstraintManagerDefault.class)
public interface ApplicationConstraintManager {

	/**
	 * provides the {@link ApplicationConstraintGenerator}s for the different
	 * activation modes
	 * 
	 * @param activationMode
	 *            the {@link ActivationModes} of the application elements in
	 *            question
	 * @return the {@link ApplicationConstraintGenerator} for the given
	 *         activation mode
	 */
	public ApplicationConstraintGenerator getConstraintGenerator(String activationMode);

}
