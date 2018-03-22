package net.sf.opendse.encoding.routing;

import net.sf.opendse.encoding.variables.DTT;
import net.sf.opendse.model.Task;

/**
 * The {@link CommunicationFlow} models the data dependency between exactly two
 * {@link Task}s and can be described by two {@link DTT}s.
 * 
 * @author Fedor Smirnov
 *
 */
public class CommunicationFlow {

	protected final DTT source;
	protected final DTT destination;

	public CommunicationFlow(DTT source, DTT destination) {
		if (!source.getDestinationTask().equals(destination.getSourceTask())) {
			throw new IllegalArgumentException("The provided DTTs do not form a communication flow.");
		}
		this.source = source;
		this.destination = destination;
	}

	public DTT getSourceDTT() {
		return source;
	}

	public DTT getDestinationDTT() {
		return destination;
	}

	@Override
	public int hashCode() {
		return source.hashCode() - destination.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CommunicationFlow) {
			CommunicationFlow other = (CommunicationFlow) obj;
			return (source.equals(other.source) && destination.equals(other.destination));
		}
		return false;
	}
}
