package net.sf.opendse.encoding;

import java.util.Set;

import org.opt4j.satdecoding.Constraint;

import com.google.inject.ImplementedBy;

import net.sf.opendse.model.Application;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Task;
import net.sf.opendse.encoding.application.ApplicationEncodingMode;
import net.sf.opendse.encoding.variables.DTT;
import net.sf.opendse.encoding.variables.T;

/**
 * The {@link ApplicationEncoding} encodes valid applications. An encoder
 * implementing this interface has to encode a {@link T} variable for each
 * {@link Task} and a {@link DTT} variable for each {@link Dependency} that can
 * possibly be active in a valid {@link Application}.
 * 
 * @author Fedor Smirnov
 *
 */
@ImplementedBy(ApplicationEncodingMode.class)
public interface ApplicationEncoding {

	/**
	 * Formulates and returns a set of constraints describing all valid
	 * {@link Application}s that can be derived from the input application (which
	 * describes the entire application design space). Specifically, the formulated
	 * constraints encode the {@link T} and the {@link DTT} variables that then
	 * contain the information about the implementation application.
	 * 
	 * @param application
	 *            the application graph describing the entire design space for the
	 *            application of the problem at hand
	 * @return the set of application constraints enforcing a valid assignment of
	 *         the {@link T} and the {@link DTT}
	 */
	public Set<Constraint> toConstraints(Application<Task, Dependency> application);

}
