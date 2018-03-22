package net.sf.opendse.encoding.mapping;

import java.util.Set;

import org.opt4j.satdecoding.Constraint;

import net.sf.opendse.encoding.ApplicationEncoding;
import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

/**
 * The {@link MappingConstraintGenerator} formulates the constraints describing
 * a correct mapping of the given processes.
 * 
 * @author Fedor Smirnov
 *
 */
public interface MappingConstraintGenerator {

	/**
	 * Formulates the mapping constraints for the given processes.
	 * 
	 * @param processVariables
	 *            the variables used by the {@link ApplicationEncoding} to encode
	 *            the processes
	 * @param mappings
	 *            the mappings defined by the user
	 * @return the set of constraints encoding a valid mapping of the given
	 *         processes
	 */
	public Set<Constraint> toConstraints(Set<T> processVariables, Mappings<Task, Resource> mappings);
}
