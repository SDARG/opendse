package net.sf.opendse.encoding.routing;

import java.util.HashSet;
import java.util.Set;

import org.opt4j.satdecoding.Constraint;

import com.google.inject.Singleton;

import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;

/**
 * The {@link AdditionalRoutingConstraintsEncoderMulti} contains a set of
 * {@link AdditionalRoutingConstraintsEncoder}s and adds all of their
 * constraints during the encoding process.
 * 
 * @author Fedor Smirnov
 *
 */
@Singleton
public class AdditionalRoutingConstraintsEncoderMulti implements AdditionalRoutingConstraintsEncoder {

	protected final Set<AdditionalRoutingConstraintsEncoder> encoders;

	public AdditionalRoutingConstraintsEncoderMulti() {
		this.encoders = new HashSet<AdditionalRoutingConstraintsEncoder>();
	}

	/**
	 * Adds the given encoder to the encoder set used for the encoding of additional
	 * routing constraints.
	 * 
	 * @param encoder
	 *            the given encoder
	 */
	public void addEncoder(AdditionalRoutingConstraintsEncoder encoder) {
		encoders.add(encoder);
	}

	@Override
	public Set<Constraint> toConstraints(T communicationVariable, Set<CommunicationFlow> communicationFlows,
			Architecture<Resource, Link> routing) {
		Set<Constraint> result = new HashSet<Constraint>();
		for (AdditionalRoutingConstraintsEncoder encoder : encoders) {
			result.addAll(encoder.toConstraints(communicationVariable, communicationFlows, routing));
		}
		return result;
	}

}
