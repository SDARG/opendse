package net.sf.opendse.encoding.routing;

import java.util.Set;

import org.opt4j.satdecoding.Constraint;

import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;

/**
 * The {@link CycleBreakEncoderOrder} generates {@link Constraint}s preventing
 * cycles by encoding an order relationship between the {@link Resource} used by
 * the {@link Communication} and forbidding that a {@link Resource} is visited
 * twice.
 * 
 * @author Fedor Smirnov
 *
 */
public class CycleBreakEncoderOrder implements CycleBreakEncoder{

	@Override
	public Set<Constraint> toConstraints(T communicationVariable, Architecture<Resource, Link> routing) {
		ResourceOrderEncoder orderEncoder = new ResourceOrderEncoder();
		return orderEncoder.generateResourceOrderConstraints(communicationVariable, routing);
	}

}
