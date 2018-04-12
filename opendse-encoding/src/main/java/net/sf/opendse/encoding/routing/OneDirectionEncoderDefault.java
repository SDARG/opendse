package net.sf.opendse.encoding.routing;

import java.util.HashSet;
import java.util.Set;

import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.Constraint.Operator;

import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Models.DirectedLink;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

public class OneDirectionEncoderDefault implements OneDirectionEncoder {

	@Override
	public Set<Constraint> toConstraints(T communicationVariable, Architecture<Resource, Link> routing) {
		Set<Constraint> oneDirectionConstraints = new HashSet<Constraint>();
		for (Link link : routing.getEdges()) {
			Resource pointA = routing.getEndpoints(link).getFirst();
			Resource pointB = routing.getEndpoints(link).getSecond();
			DirectedLink frontLink = new DirectedLink(link, pointA, pointB);
			DirectedLink backLink = new DirectedLink(link, pointB, pointA);
			oneDirectionConstraints.add(getOneDirectionConstraint(frontLink, backLink, communicationVariable));
		}
		return oneDirectionConstraints;
	}

	/**
	 * Formulates the {@link Constraint} stating that at most one of the given links
	 * is used to route the given communication.
	 * 
	 * CLR_1R_2 + CLR_2R_1 <= 1
	 * 
	 * @param frontLink
	 *            the {@link DirectedLink} directed from A to B, with A and B being
	 *            the endpoints of the {@link Link} l that frontLink and backLink
	 *            are based on
	 * @param backLink
	 *            the {@link DirectedLink} directed from B to A, with A and B being
	 *            the endpoints of the {@link Link} l that frontLink and backLink
	 *            are based on
	 * @param communicationVariable
	 *            the {@link T} variable encoding the activation of the
	 *            communication {@link Task} that is being routed
	 * @return the {@link Constraint} stating that at most one of the given links is
	 *         used to route the given communication
	 */
	protected Constraint getOneDirectionConstraint(DirectedLink frontLink, DirectedLink backLink,
			T communicationVariable) {
		Constraint result = new Constraint(Operator.LE, 1);
		result.add(Variables.p(Variables.varCLRR(communicationVariable.getTask(), frontLink.getLink(),
				frontLink.getSource(), frontLink.getDest())));
		result.add(Variables.p(Variables.varCLRR(communicationVariable.getTask(), backLink.getLink(),
				backLink.getSource(), backLink.getDest())));
		return result;
	}
}