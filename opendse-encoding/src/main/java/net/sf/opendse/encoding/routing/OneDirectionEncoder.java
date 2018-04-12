package net.sf.opendse.encoding.routing;

import java.util.Set;

import org.opt4j.satdecoding.Constraint;

import com.google.inject.ImplementedBy;

import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

/**
 * The {@link OneDirectionEncoder} formulates constraints that ensure that a
 * {@link Link} can route a {@link Communication} only in one direction.
 * 
 * @author Fedor Smirnov
 *
 */
@ImplementedBy(OneDirectionEncoderDefault.class)
public interface OneDirectionEncoder {

	/**
	 * Formulates constraints that ensure that a {@link Link} can route a
	 * {@link Communication} only in one direction.
	 * 
	 * @param communicationVariable
	 *            the {@link T} variable encoding the activation of the
	 *            communication {@link Task} that is being routed
	 * @param routing
	 *            the {@link Architecture} representing the possibilities for the
	 *            routing of the communication
	 * @return a set of {@link Constraint}s that ensures that a {@link Link} can
	 *         route a {@link Communication} only in one direction
	 */
	public Set<Constraint> toConstraints(T communicationVariable, Architecture<Resource, Link> routing);

}
