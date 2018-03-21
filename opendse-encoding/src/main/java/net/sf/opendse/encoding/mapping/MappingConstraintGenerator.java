package net.sf.opendse.encoding.mapping;

import java.util.Set;

import org.opt4j.satdecoding.Constraint;

import net.sf.opendse.encoding.ApplicationEncoding;
import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

/**
 * The {@link MappingConstraintGenerator} formulates the constraint describing a
 * correct mapping of the given processes and returns the
 * {@link MappingVariable}s relevant for other enconding modules.
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
	 * @param constraints
	 *            the constraints formulated so far
	 * @return the set of {@link MappingVariable}s relevant for other encoding
	 *         modules
	 */
	public Set<MappingVariable> toConstraints(Set<T> processVariables, Mappings<Task, Resource> mappings,
			Set<Constraint> constraints);
}
