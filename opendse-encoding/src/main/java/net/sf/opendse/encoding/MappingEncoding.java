package net.sf.opendse.encoding;

import java.util.Set;

import org.opt4j.satdecoding.Constraint;

import com.google.inject.ImplementedBy;

import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;
import net.sf.opendse.encoding.mapping.MappingEncodingMode;
import net.sf.opendse.encoding.variables.ApplicationVariable;
import net.sf.opendse.encoding.variables.M;

/**
 * The {@link MappingEncoding} encodes a valid mapping. An encoder implementing
 * this interface has to encode an {@link M} variable for each mapping that can
 * possibly be active in an implementation.
 * 
 * @author Fedor Smirnov
 *
 */
@ImplementedBy(MappingEncodingMode.class)
public interface MappingEncoding {

	/**
	 * 
	 * Formulates and returns a set of constraints describing all valid bindings of
	 * the tasks encoded by the {@link ApplicationVariable}s provided as input.
	 * Specifically, the formulated constraints encode the {@link M} variables that
	 * then contain the information about the binding decisions in an
	 * implementation.
	 * 
	 * @param mappings
	 *            the mappings provided by the designer
	 * @param applicationVariables
	 *            the variables encoding the possible applications
	 * @return the set of mapping constraints enforcing a valid assignment of the
	 *         {@link M} variables
	 */
	public Set<Constraint> toConstraints(Mappings<Task, Resource> mappings,
			Set<ApplicationVariable> applicationVariables);
}
